package com.flightbooking.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.flightbooking.search.dto.FlightLeg;
import com.flightbooking.search.dto.SearchRequest;
import com.flightbooking.search.entity.FlightInstanceNode;
import com.flightbooking.search.entity.Itinerary;
import com.flightbooking.search.repository.FlightInstanceNodeRepository;
import com.flightbooking.search.repository.ItineraryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Service for itinerary creation and management.
 * Handles graph traversal, validation, and itinerary creation using hybrid approach.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItineraryService {

    private final FlightInstanceNodeRepository flightInstanceNodeRepository;
    private final ItineraryRepository itineraryRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MAX_RESULTS = 100;

    private static final int MAX_STOPS = 2;

        /**
     * Find cached itineraries for the search criteria.
     */
    @Transactional(readOnly = true)
    public List<Itinerary> findCachedItineraries(SearchRequest searchRequest) {
        String searchKey = Itinerary.generateSearchKey(
                searchRequest.getSource(), 
                searchRequest.getDestination(), 
                searchRequest.getDepartureDate()
        );

        return itineraryRepository.findBySearchKeyWithSeatFilter(
                searchKey, 
                searchRequest.getPassengerCount()
        );
    }

        /**
     * Generate itineraries on-the-fly using hybrid graph traversal approach.
     * Creates passenger-agnostic itineraries that will be cached for all future searches.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Itinerary> generateItineraries(SearchRequest searchRequest) {
        log.info("Generating itineraries using hybrid graph traversal: {} to {} on {}", 
                searchRequest.getSource(), searchRequest.getDestination(), searchRequest.getDepartureDate());

        // Delegate to ItineraryService for actual itinerary creation
        List<Itinerary> itineraries = createItineraries(
                searchRequest.getSource(),
                searchRequest.getDestination(), 
                searchRequest.getDepartureDate(),
                MAX_STOPS // Max 2 stops (3 flights total)
        );

                // Step 3: Save generated itineraries for future searches (passenger-agnostic)
                if (!itineraries.isEmpty()) {
                    log.info("Saving {} generated itineraries to cache", itineraries.size());
                    saveUniqueItineraries(itineraries);
                }

        log.info("Generated {} itineraries using hybrid approach", itineraries.size());
        return itineraries;
    }

    /**
     * Create itineraries using hybrid graph traversal approach.
     * Single query finds all paths, Java validates layovers and creates itineraries.
     */
    public List<Itinerary> createItineraries(String source, String destination, LocalDate date, int maxStops) {
        log.debug("Creating itineraries: {} to {} on {} (max {} stops)", source, destination, date, maxStops);
        
        List<Object[]> pathResults = new ArrayList<>();
        
        // Find direct flights first
        List<FlightInstanceNode> directFlights = flightInstanceNodeRepository.findDirectFlights(source, destination);
        for (FlightInstanceNode flight : directFlights) {
            pathResults.add(new Object[]{List.of(flight), 1});
        }
        log.debug("Found {} direct flights", directFlights.size());
        
        // Find connecting flights if maxStops > 0
        if (maxStops > 0) {
            // Convert LocalDate to OffsetDateTime for repository calls
            OffsetDateTime departureDateTime = date.atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
            
            // Find 1-stop connections
            List<FlightInstanceNode> firstLegs = flightInstanceNodeRepository.findConnectingFlightsFirstLeg(source, destination, departureDateTime);
            List<FlightInstanceNode> secondLegs = flightInstanceNodeRepository.findConnectingFlightsSecondLeg(source, destination, departureDateTime);
            
            for (int i = 0; i < Math.min(firstLegs.size(), secondLegs.size()); i++) {
                FlightInstanceNode flight1 = firstLegs.get(i);
                FlightInstanceNode flight2 = secondLegs.get(i);
                List<FlightInstanceNode> flights = List.of(flight1, flight2);
                pathResults.add(new Object[]{flights, 2});
            }
            log.debug("Found {} connecting flights (1 stop)", Math.min(firstLegs.size(), secondLegs.size()));
            
            // Find 2-stop connections if maxStops > 1
            if (maxStops > 1) {
                List<Object[]> twoStopFlights = flightInstanceNodeRepository.findConnectingFlightsWithTwoStops(source, destination, departureDateTime);
                for (Object[] result : twoStopFlights) {
                    FlightInstanceNode flight1 = (FlightInstanceNode) result[0];
                    FlightInstanceNode flight2 = (FlightInstanceNode) result[1];
                    FlightInstanceNode flight3 = (FlightInstanceNode) result[2];
                    List<FlightInstanceNode> flights = List.of(flight1, flight2, flight3);
                    pathResults.add(new Object[]{flights, 3});
                }
                log.debug("Found {} connecting flights (2 stops)", twoStopFlights.size());
            }
        }
        
        log.debug("Found {} total paths from Neo4j graph traversal", pathResults.size());
        
        return pathResults.stream()
                .map(result -> {
                    @SuppressWarnings("unchecked")
                    List<FlightInstanceNode> flights = (List<FlightInstanceNode>) result[0];
                    Integer pathLength = (Integer) result[1];
                    
                    log.debug("Processing path with {} flights, length: {}", flights.size(), pathLength);
                    
                    // Validate flight sequence and layovers in Java (cleaner logic)
                    if (!isValidFlightSequence(flights)) {
                        log.debug("Invalid flight sequence - skipping");
                        return null; // Filter out invalid sequences
                    }
                    
                    return createItineraryFromFlights(flights, date);
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Itinerary::getTotalPrice))
                .limit(MAX_RESULTS)
                .collect(Collectors.toList());
    }

    /**
     * Validate flight sequence for proper connections and layover times.
     */
    private boolean isValidFlightSequence(List<FlightInstanceNode> flights) {
        if (flights == null || flights.size() < 1) {
            return false;
        }
        
        for (int i = 0; i < flights.size() - 1; i++) {
            FlightInstanceNode currentFlight = flights.get(i);
            FlightInstanceNode nextFlight = flights.get(i + 1);
            
            // Check if flights connect properly (destination of current = source of next)
            if (!currentFlight.getDestination().equals(nextFlight.getSource())) {
                log.debug("Invalid connection: {} -> {} (expected: {} -> {})", 
                        currentFlight.getDestination(), nextFlight.getSource(),
                        currentFlight.getDestination(), currentFlight.getDestination());
                return false;
            }
            
            // Check layover constraints (45 minutes to 24 hours)
            if (!isValidLayover(currentFlight.getArrivalTime(), nextFlight.getDepartureTime())) {
                log.debug("Invalid layover: {} to {} (flight {} -> {})", 
                        currentFlight.getArrivalTime(), nextFlight.getDepartureTime(),
                        currentFlight.getFlightNo(), nextFlight.getFlightNo());
                return false;
            }
        }
        return true;
    }

    /**
     * Check if layover time is valid (45 minutes to 24 hours).
     */
    private boolean isValidLayover(OffsetDateTime arrivalTime, OffsetDateTime departureTime) {
        if (departureTime.isBefore(arrivalTime)) {
            return false; // Departure before arrival - invalid
        }

        long layoverMinutes = ChronoUnit.MINUTES.between(arrivalTime, departureTime);
        return layoverMinutes >= 45 && layoverMinutes <= (24 * 60); // 45 min to 24 hours
    }

    /**
     * Create itinerary from a list of flights (handles direct and connecting flights).
     */
    private Itinerary createItineraryFromFlights(List<FlightInstanceNode> flights, LocalDate departureDate) {
        String itineraryId = UUID.randomUUID().toString();
        String searchKey = Itinerary.generateSearchKey(
                flights.get(0).getSource(), 
                flights.get(flights.size() - 1).getDestination(), 
                departureDate
        );

        // Calculate total price and duration
        long totalPrice = flights.stream().mapToLong(FlightInstanceNode::getPriceMoney).sum();
        long totalDuration = ChronoUnit.SECONDS.between(
                flights.get(0).getDepartureTime(), 
                flights.get(flights.size() - 1).getArrivalTime()
        );

        // Find minimum available seats across all flights
        int minAvailableSeats = flights.stream()
                .mapToInt(FlightInstanceNode::getTotalAvailableSeats)
                .min()
                .orElse(0);

        // Debug logging
        log.debug("Flight seats debug - Flight IDs: {}, Available seats: {}", 
                flights.stream().map(FlightInstanceNode::getId).collect(Collectors.toList()),
                flights.stream().map(FlightInstanceNode::getTotalAvailableSeats).collect(Collectors.toList()));

        // Create JSON for flight legs
        String flightLegsJson = createFlightLegsJson(flights);
        
        // Generate hash for uniqueness constraint
        String flightLegsHash = generateFlightLegsHash(flights);
        log.debug("Generated flightLegsHash: {}", flightLegsHash);

        return Itinerary.builder()
                .id(itineraryId)
                .source(flights.get(0).getSource())
                .destination(flights.get(flights.size() - 1).getDestination())
                .departureDate(departureDate)
                .legs(flights.size())
                .totalPrice(totalPrice)
                .totalDuration(totalDuration)
                .minAvailableSeats(minAvailableSeats)
                .searchKey(searchKey)
                .flightLegsJson(flightLegsJson)
                .flightLegsHash(flightLegsHash)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    /**
     * Create JSON representation of flight legs for fast reads.
     */
    private String createFlightLegsJson(List<FlightInstanceNode> flights) {
        StringBuilder jsonBuilder = new StringBuilder("[");
        
        for (int i = 0; i < flights.size(); i++) {
            FlightInstanceNode flight = flights.get(i);
            
            if (i > 0) {
                jsonBuilder.append(",");
            }
            
            long duration = ChronoUnit.SECONDS.between(flight.getDepartureTime(), flight.getArrivalTime());
            
            jsonBuilder.append(String.format(
                    "{\"flightId\":\"%s\",\"flightNo\":\"%s\",\"source\":\"%s\",\"destination\":\"%s\"," +
                    "\"departureTime\":\"%s\",\"arrivalTime\":\"%s\",\"price\":%d,\"duration\":%d,\"remainingSeats\":%d}",
                    flight.getId(), flight.getFlightNo(), flight.getSource(), flight.getDestination(),
                    flight.getDepartureTime(), flight.getArrivalTime(), flight.getPriceMoney(), duration, 
                    flight.getTotalAvailableSeats()
            ));
        }
        
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    /**
     * Parse flight legs from JSON stored in itinerary.
     */
    public List<FlightLeg> parseFlightLegsFromJson(String flightLegsJson) {
        if (flightLegsJson == null || flightLegsJson.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            JsonNode jsonArray = objectMapper.readTree(flightLegsJson);
            
            if (!jsonArray.isArray()) {
                log.warn("Flight legs JSON is not an array: {}", flightLegsJson);
                return new ArrayList<>();
            }
            
            List<FlightLeg> flightLegs = new ArrayList<>();
            
            for (JsonNode legNode : jsonArray) {
                FlightLeg flightLeg = FlightLeg.builder()
                        .flightId(legNode.get("flightId").asText())
                        .flightNo(legNode.get("flightNo").asText())
                        .source(legNode.get("source").asText())
                        .destination(legNode.get("destination").asText())
                        .departureTime(OffsetDateTime.parse(legNode.get("departureTime").asText()))
                        .arrivalTime(OffsetDateTime.parse(legNode.get("arrivalTime").asText()))
                        .price(legNode.get("price").asLong())
                        .duration(legNode.get("duration").asLong())
                        .availableSeats(legNode.get("remainingSeats").asInt())
                        .build();
                
                flightLegs.add(flightLeg);
            }
            
            return flightLegs;
            
        } catch (Exception e) {
            log.error("Failed to parse flight legs JSON: {}", flightLegsJson, e);
            return new ArrayList<>();
        }
    }

    /**
     * Save itineraries with duplicate prevention using the unique constraint.
     * The database will handle uniqueness based on flightLegsHash.
     */
    private void saveUniqueItineraries(List<Itinerary> itineraries) {
        try {
            // Try to save all itineraries - Neo4j will enforce uniqueness constraint
            itineraryRepository.saveAll(itineraries);
            log.info("Successfully saved {} unique itineraries", itineraries.size());
        } catch (Exception e) {
            log.warn("Some itineraries may be duplicates, saving individually: {}", e.getMessage());
            
            // If batch save fails due to constraint violations, save individually
            int savedCount = 0;
            for (Itinerary itinerary : itineraries) {
                try {
                    itineraryRepository.save(itinerary);
                    savedCount++;
                } catch (Exception individualException) {
                    log.debug("Skipping duplicate itinerary with hash: {}", itinerary.getFlightLegsHash());
                }
            }
            log.info("Saved {} out of {} itineraries ({} were duplicates)", 
                    savedCount, itineraries.size(), itineraries.size() - savedCount);
        }
    }

    /**
     * Generate a hash for flight legs to ensure uniqueness.
     * This hash is used for the unique constraint in Neo4j.
     */
    private String generateFlightLegsHash(List<FlightInstanceNode> flights) {
        try {
            // Sort flight IDs to ensure consistent hash regardless of order
            String legIds = flights.stream()
                .map(FlightInstanceNode::getId)
                .sorted()
                .collect(Collectors.joining(","));
            
            // Generate MD5 hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(legIds.getBytes());
            
            // Convert to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
            
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 algorithm not available", e);
            // Fallback to simple hash
            return String.valueOf(flights.stream()
                .map(FlightInstanceNode::getId)
                .sorted()
                .collect(Collectors.joining(","))
                .hashCode());
        }
    }
}
