package com.flightbooking.booking.dto;

import java.util.List;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * BookingRequest DTO for creating seat holds.
 * Contains the information needed to hold seats for a specific flight.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    @NotNull(message = "Flight IDs are required")
    @Size(min = 1, max = 3, message = "Must have between 1 and 3 flight legs")
    private List<Long> flightIds;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "At least 1 seat is required")
    @Max(value = 9, message = "Maximum 9 seats allowed")
    private Integer seatCount;

    // Helper method to validate booking request
    public boolean isValid() {
        return flightIds != null && !flightIds.isEmpty() && 
               flightIds.stream().allMatch(id -> id > 0) &&
               seatCount != null && seatCount > 0;
    }

    // Helper method to get number of flight legs
    public int getFlightLegCount() {
        return flightIds != null ? flightIds.size() : 0;
    }

    // Helper method to check if it's a connecting flight
    public boolean isConnectingFlight() {
        return getFlightLegCount() > 1;
    }
}
