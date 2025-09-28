package com.flightbooking.search.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * SearchRequest DTO for flight search API.
 * Contains all parameters needed to search for available flights.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {

    @NotBlank(message = "Source airport code is required")
    @Size(min = 3, max = 3, message = "Source airport code must be exactly 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Source airport code must be uppercase letters")
    private String source;

    @NotBlank(message = "Destination airport code is required")
    @Size(min = 3, max = 3, message = "Destination airport code must be exactly 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Destination airport code must be uppercase letters")
    private String destination;

    @NotNull(message = "Departure date is required")
    @Future(message = "Departure date must be in the future")
    private LocalDate departureDate;

    @NotNull(message = "Number of passengers is required")
    @Min(value = 1, message = "At least 1 passenger is required")
    @Max(value = 9, message = "Maximum 9 passengers allowed")
    private Integer passengerCount;

    private Long minPrice; // Optional minimum price filter in cents

    private Long maxPrice; // Optional maximum price filter in cents

    private Integer maxStops; // Optional maximum number of stops (0, 1, or 2)

    private Integer limit = 50; // Maximum number of results to return

    // Helper method to validate source and destination are different
    public boolean isValidRoute() {
        return source != null && destination != null && !source.equals(destination);
    }

    // Helper method to get search key
    public String getSearchKey() {
        return source + "-" + destination + "-" + departureDate + "-" + passengerCount;
    }

    // Helper method to check if price filters are valid
    public boolean hasValidPriceRange() {
        return (minPrice == null || maxPrice == null || minPrice <= maxPrice);
    }
}
