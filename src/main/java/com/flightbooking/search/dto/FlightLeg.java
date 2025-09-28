package com.flightbooking.search.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * FlightLeg DTO representing a single flight leg in a flight option.
 * A flight option can have multiple legs for connecting flights.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightLeg {
    private String flightId;
    private String flightNo;
    private String source;
    private String destination;
    private OffsetDateTime departureTime;
    private OffsetDateTime arrivalTime;
    private Long price; // Price in cents
    private Long duration; // Duration in seconds
    private Integer availableSeats;

    // Helper method to get price as BigDecimal
    public BigDecimal getPriceAsBigDecimal() {
        return new BigDecimal(price).divide(new BigDecimal(100));
    }

    // Helper method to get duration in hours
    public BigDecimal getDurationInHours() {
        return new BigDecimal(duration).divide(new BigDecimal(3600), 2, java.math.RoundingMode.HALF_UP);
    }
}
