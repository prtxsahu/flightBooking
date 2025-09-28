package com.flightbooking.booking.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * PaymentRequest DTO for payment confirmation API.
 * Contains the information needed to confirm payment and convert seat holds to tickets.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotBlank(message = "Payment ID is required")
    private String paymentId;


    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    // Optional: Additional payment details
    private String paymentMethod;
    private String paymentProvider;
    private String transactionId;

    // Helper method to validate payment request
    public boolean isValid() {
        return paymentId != null && !paymentId.trim().isEmpty() &&
               paymentStatus != null;
    }

    // Helper method to check if payment was successful
    public boolean isPaymentSuccessful() {
        return paymentStatus == PaymentStatus.SUCCESS;
    }

    // Helper method to check if payment failed
    public boolean isPaymentFailed() {
        return paymentStatus == PaymentStatus.FAILED;
    }

    public enum PaymentStatus {
        SUCCESS,
        FAILED,
        PENDING,
        CANCELLED,
        REFUNDED
    }
}
