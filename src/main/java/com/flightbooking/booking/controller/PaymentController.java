package com.flightbooking.booking.controller;

import com.flightbooking.booking.dto.PaymentRequest;
import com.flightbooking.booking.dto.PaymentResponse;
import com.flightbooking.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for payment operations.
 * Handles payment confirmation and failure scenarios.
 */
@RestController
@RequestMapping("/v1/payment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Configure appropriately for production
public class PaymentController {

    private final BookingService bookingService;

    /**
     * Process payment confirmation or failure.
     * 
     * @param paymentRequest The payment request with status and details
     * @return PaymentResponse with confirmation details
     */
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        log.info("Received payment processing request: payment ID {}, status {}, session {}",
                paymentRequest.getPaymentId(), paymentRequest.getPaymentStatus());

        try {
            // Validate payment request
            if (!paymentRequest.isValid()) {
                log.warn("Invalid payment request received: {}", paymentRequest);
                return ResponseEntity.badRequest()
                        .body(PaymentResponse.failure(
                                paymentRequest.getPaymentId(),
                                "Invalid payment request",
                                PaymentRequest.PaymentStatus.FAILED
                        ));
            }

            // Process payment
            PaymentResponse paymentResponse = bookingService.processPayment(paymentRequest);

            if ("SUCCESS".equalsIgnoreCase(paymentResponse.getStatus())) {
                log.info("Payment processed successfully: payment ID {}, ticket ID {}",
                        paymentRequest.getPaymentId(), paymentResponse.getTicketId());
                return ResponseEntity.ok(paymentResponse);
            } else {
                log.warn("Payment processing failed: payment ID {}, reason: {}",
                        paymentRequest.getPaymentId(), paymentResponse.getMessage());
                return ResponseEntity.badRequest().body(paymentResponse);
            }

        } catch (Exception e) {
            log.error("Error processing payment request for payment ID {}: {}",
                    paymentRequest.getPaymentId(), e.getMessage(), e);
            
            return ResponseEntity.internalServerError()
                    .body(PaymentResponse.failure(
                            paymentRequest.getPaymentId(),
                            "Internal server error occurred during payment processing",
                            PaymentRequest.PaymentStatus.FAILED
                    ));
        }
    }

    /**
     * Get payment status by payment ID.
     * 
     * @param paymentId The payment ID
     * @return PaymentResponse with current status
     */
    @GetMapping("/status/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable String paymentId) {
        log.info("Received payment status request for payment ID: {}", paymentId);

        try {
            // For now, return a placeholder response
            // TODO: Implement actual payment status lookup
            return ResponseEntity.ok(PaymentResponse.builder()
                    .paymentId(paymentId)
                    .status("PENDING")
                    .message("Payment status lookup not yet implemented")
                    .build());

        } catch (Exception e) {
            log.error("Error retrieving payment status for payment ID {}: {}", paymentId, e.getMessage(), e);
            
            return ResponseEntity.internalServerError()
                    .body(PaymentResponse.failure(paymentId, 
                            "Error retrieving payment status", PaymentRequest.PaymentStatus.FAILED));
        }
    }

    /**
     * Health check endpoint for payment service.
     */
    @GetMapping("/health")
    public ResponseEntity<java.util.Map<String, String>> health() {
        return ResponseEntity.ok(java.util.Map.of(
                "status", "UP",
                "service", "payment-service",
                "timestamp", java.time.OffsetDateTime.now().toString()
        ));
    }
}
