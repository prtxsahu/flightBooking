package com.flightbooking.search.controller;

import com.flightbooking.search.dto.SearchRequest;
import com.flightbooking.search.dto.SearchResponse;
import com.flightbooking.search.service.FlightSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for flight search operations.
 * Handles search requests and returns flight options.
 */
@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Configure appropriately for production
public class SearchController {

    private final FlightSearchService flightSearchService;

    /**
     * Search for flights based on criteria.
     * 
     * @param searchRequest The search criteria
     * @return SearchResponse with flight options
     */
    @GetMapping("/flights")
    public ResponseEntity<SearchResponse> searchFlights(@Valid @ModelAttribute SearchRequest searchRequest) {
        log.info("Received flight search request: {} to {} on {} for {} passengers",
                searchRequest.getSource(), searchRequest.getDestination(),
                searchRequest.getDepartureDate(), searchRequest.getPassengerCount());

        try {
            // Validate search request
            if (searchRequest.getSource() == null || searchRequest.getDestination() == null || 
                searchRequest.getDepartureDate() == null || searchRequest.getPassengerCount() == null) {
                log.warn("Invalid search request received: {}", searchRequest);
                return ResponseEntity.badRequest()
                        .body(SearchResponse.builder()
                                .source(searchRequest.getSource())
                                .destination(searchRequest.getDestination())
                                .departureDate(searchRequest.getDepartureDate())
                                .passengerCount(searchRequest.getPassengerCount())
                                .totalResults(0)
                                .options(java.util.Collections.emptyList())
                                .build());
            }

            // Perform search
            SearchResponse searchResponse = flightSearchService.searchFlights(searchRequest);

            log.info("Search completed successfully. Found {} flight options",
                    searchResponse.getTotalResults());

            return ResponseEntity.ok(searchResponse);

        } catch (Exception e) {
            log.error("Error processing flight search request: {}", e.getMessage(), e);
            
            // Return error response
            return ResponseEntity.internalServerError()
                    .body(SearchResponse.builder()
                            .source(searchRequest.getSource())
                            .destination(searchRequest.getDestination())
                            .departureDate(searchRequest.getDepartureDate())
                            .passengerCount(searchRequest.getPassengerCount())
                            .totalResults(0)
                            .options(java.util.Collections.emptyList())
                            .build());
        }
    }

    /**
     * Health check endpoint for search service.
     */
    @GetMapping("/health")
    public ResponseEntity<java.util.Map<String, String>> health() {
        return ResponseEntity.ok(java.util.Map.of(
                "status", "UP",
                "service", "search-service",
                "timestamp", java.time.OffsetDateTime.now().toString()
        ));
    }
}
