package com.flightbooking.search.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * SearchResponse DTO for flight search API.
 * Contains the search results with available flight options.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {

    private String source;
    private String destination;
    private LocalDate departureDate;
    private Integer passengerCount;
    private Integer totalResults;
    private List<FlightOption> options;

    // Helper method to check if search has results
    public boolean hasResults() {
        return options != null && !options.isEmpty();
    }

    // Helper method to get cheapest option
    public FlightOption getCheapestOption() {
        if (!hasResults()) {
            return null;
        }
        return options.stream()
                .min((a, b) -> Long.compare(a.getTotalPrice(), b.getTotalPrice()))
                .orElse(null);
    }

    // Helper method to get fastest option
    public FlightOption getFastestOption() {
        if (!hasResults()) {
            return null;
        }
        return options.stream()
                .min((a, b) -> Long.compare(a.getTotalDuration(), b.getTotalDuration()))
                .orElse(null);
    }
}
