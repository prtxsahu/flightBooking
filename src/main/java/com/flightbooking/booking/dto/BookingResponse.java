package com.flightbooking.booking.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * BookingResponse DTO for seat hold creation API.
 * Contains the response after successfully creating a seat hold.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private String status; // "success" or "failed"
    private String message; // Descriptive message
    private String paymentId; // UUID of the created hold
    private Long flightId;
    private Integer seatCount;
    private String sessionId;
    private OffsetDateTime expiresAt;
    private Long totalAmount; // Total amount in cents
    private List<String> seatNumbers; // List of held seat numbers
    private OffsetDateTime createdAt;

    // Helper method to check if booking was successful
    public boolean isSuccess() {
        return "success".equals(status);
    }

    // Helper method to get total amount as BigDecimal
    public java.math.BigDecimal getTotalAmountAsBigDecimal() {
        if (totalAmount == null) {
            return java.math.BigDecimal.ZERO;
        }
        return new java.math.BigDecimal(totalAmount).divide(new java.math.BigDecimal(100));
    }

    // Helper method to get time remaining until expiration in seconds
    public long getSecondsUntilExpiration() {
        if (expiresAt == null) {
            return 0;
        }
        return java.time.Duration.between(OffsetDateTime.now(), expiresAt).getSeconds();
    }

    // Helper method to check if hold is expired
    public boolean isExpired() {
        return expiresAt != null && OffsetDateTime.now().isAfter(expiresAt);
    }

    // Static factory method for success response
    public static BookingResponse success(String paymentID, Long flightId, Integer seatCount, 
                                        String sessionId, OffsetDateTime expiresAt, 
                                        Long totalAmount, List<String> seatNumbers) {
        return BookingResponse.builder()
                .status("success")
                .message("Seats successfully held")
                .paymentId(paymentID)
                .flightId(flightId)
                .seatCount(seatCount)
                .sessionId(sessionId)
                .expiresAt(expiresAt)
                .totalAmount(totalAmount)
                .seatNumbers(seatNumbers)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    // Static factory method for failure response
    public static BookingResponse failure(String message, Long flightId, Integer seatCount, String sessionId) {
        return BookingResponse.builder()
                .status("failed")
                .message(message)
                .flightId(flightId)
                .seatCount(seatCount)
                .sessionId(sessionId)
                .createdAt(OffsetDateTime.now())
                .build();
    }
}
