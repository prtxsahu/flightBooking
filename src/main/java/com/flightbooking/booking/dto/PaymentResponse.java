package com.flightbooking.booking.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * PaymentResponse DTO for payment confirmation API.
 * Contains the response after processing payment and creating tickets.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private String status; // "success" or "failed"
    private String message; // Descriptive message
    private String paymentId;
    private String sessionId;
    private Long userId;
    private Long flightId;
    private Long ticketId; // Created ticket ID (if successful)
    private String flightNumber;
    private List<String> seatNumbers; // Confirmed seat numbers
    private Long totalAmount; // Total amount in cents
    private PaymentRequest.PaymentStatus paymentStatus;
    private OffsetDateTime bookingConfirmedAt;
    private String ticketReference; // Human-readable ticket reference

    // Helper method to check if payment was successful
    public boolean isSuccess() {
        return "success".equals(status) && paymentStatus == PaymentRequest.PaymentStatus.SUCCESS;
    }

    // Helper method to get total amount as BigDecimal
    public BigDecimal getTotalAmountAsBigDecimal() {
        if (totalAmount == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(totalAmount).divide(new BigDecimal(100));
    }

    // Helper method to get seat count
    public int getSeatCount() {
        return seatNumbers != null ? seatNumbers.size() : 0;
    }

    // Helper method to get formatted seat numbers
    public String getFormattedSeatNumbers() {
        if (seatNumbers == null || seatNumbers.isEmpty()) {
            return "";
        }
        return String.join(", ", seatNumbers);
    }

    // Static factory method for success response
    public static PaymentResponse success(String paymentId, String sessionId, 
                                     Long ticketId, String flightNumber,
                                        List<String> seatNumbers, Long totalAmount, 
                                        String ticketReference) {
        return PaymentResponse.builder()
                .status("success")
                .message("Booking confirmed successfully")
                .paymentId(paymentId)
                .sessionId(sessionId)
                .ticketId(ticketId)
                .flightNumber(flightNumber)
                .seatNumbers(seatNumbers)
                .totalAmount(totalAmount)
                .paymentStatus(PaymentRequest.PaymentStatus.SUCCESS)
                .bookingConfirmedAt(OffsetDateTime.now())
                .ticketReference(ticketReference)
                .build();
    }

    // Static factory method for failure response
    public static PaymentResponse failure(String paymentId, String message, 
                                        PaymentRequest.PaymentStatus paymentStatus) {
        return PaymentResponse.builder()
                .status("failed")
                .message(message)
                .paymentId(paymentId)
                .paymentStatus(paymentStatus)
                .bookingConfirmedAt(OffsetDateTime.now())
                .build();
    }
}
