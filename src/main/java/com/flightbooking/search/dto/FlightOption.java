package com.flightbooking.search.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * FlightOption DTO representing a flight option in search results.
 * Contains information about a specific flight combination (direct or connecting).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightOption {
    private String itineraryId;
    private String source;
    private String destination;
    private LocalDate departureDate;
    private Integer legs;
    private Integer stops;
    private Long totalPrice; // Price in cents
    private Long totalDuration; // Duration in seconds
    private List<FlightLeg> flightLegs;

    // Helper method to get total price as BigDecimal
    public BigDecimal getTotalPriceAsBigDecimal() {
        return new BigDecimal(totalPrice).divide(new BigDecimal(100));
    }

    // Helper method to get total duration in hours
    public BigDecimal getTotalDurationInHours() {
        return new BigDecimal(totalDuration).divide(new BigDecimal(3600), 2, java.math.RoundingMode.HALF_UP);
    }
}
