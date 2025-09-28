package com.flightbooking.search.service;

import com.flightbooking.search.dto.SearchRequest;
import com.flightbooking.search.dto.SearchResponse;
import com.flightbooking.search.dto.FlightOption;
import com.flightbooking.search.dto.FlightLeg;
import com.flightbooking.search.entity.Itinerary;
import com.flightbooking.search.repository.ItineraryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for flight search operations.
 * Handles both cached itinerary lookup and runtime itinerary generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FlightSearchService {

    private final ItineraryService itineraryService;

    /**
     * Main search method that handles flight search requests.
     * 
     * @param searchRequest The search criteria
     * @return SearchResponse with flight options
     */
    @Transactional(readOnly = true)
    public SearchResponse searchFlights(SearchRequest searchRequest) {
        log.info("Starting flight search: {} to {} on {} for {} passengers", 
                searchRequest.getSource(), searchRequest.getDestination(), 
                searchRequest.getDepartureDate(), searchRequest.getPassengerCount());

        // Validate search request
        if (searchRequest.getSource() == null || searchRequest.getDestination() == null || 
            searchRequest.getDepartureDate() == null || searchRequest.getPassengerCount() == null) {
            throw new RuntimeException("Invalid search request");
        }

        // Step 1: Try to find cached itineraries first
        List<Itinerary> cachedItineraries = itineraryService.findCachedItineraries(searchRequest);
        
        if (!cachedItineraries.isEmpty()) {
            log.info("Found {} cached itineraries for search", cachedItineraries.size());
            return buildSearchResponse(searchRequest, cachedItineraries);
        }

        // Step 2: Generate itineraries on-the-fly if no cache found
        // These will be passenger-agnostic and cached for all future searches
        log.info("No cached itineraries found, generating new ones");
        List<Itinerary> generatedItineraries = itineraryService.generateItineraries(searchRequest);
        


        return buildSearchResponse(searchRequest, generatedItineraries);
    }





    // Note: All itinerary creation logic moved to ItineraryService for better separation of concerns

    /**
     * Build search response from itineraries.
     */
    private SearchResponse buildSearchResponse(SearchRequest searchRequest, List<Itinerary> itineraries) {
        List<FlightOption> flightOptions = itineraries.stream()
                .map(this::convertToFlightOption)
                .collect(Collectors.toList());

        return SearchResponse.builder()
                .source(searchRequest.getSource())
                .destination(searchRequest.getDestination())
                .departureDate(searchRequest.getDepartureDate())
                .passengerCount(searchRequest.getPassengerCount())
                .totalResults(flightOptions.size())
                .options(flightOptions)
                .build();
    }

    /**
     * Convert itinerary to flight option.
     */
    private FlightOption convertToFlightOption(Itinerary itinerary) {
        // For now, we'll create a simplified flight option
        // In the next iteration, we'll add proper flight legs
        return FlightOption.builder()
                .itineraryId(itinerary.getId())
                .source(itinerary.getSource())
                .destination(itinerary.getDestination())
                .departureDate(itinerary.getDepartureDate())
                .legs(itinerary.getLegs())
                .stops(itinerary.getStopsCount())
                .totalPrice(itinerary.getTotalPrice())
                .totalDuration(itinerary.getTotalDuration())
                .flightLegs(parseFlightLegsFromJson(itinerary.getFlightLegsJson()))
                .build();
    }

    /**
     * Parse flight legs from JSON stored in itinerary.
     * Delegates to ItineraryService for JSON parsing.
     */
    private List<FlightLeg> parseFlightLegsFromJson(String flightLegsJson) {
        return itineraryService.parseFlightLegsFromJson(flightLegsJson);
    }

    // Note: Time range validation is now handled in Cypher queries for better performance
}
