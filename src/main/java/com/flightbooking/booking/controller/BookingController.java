package com.flightbooking.booking.controller;

import com.flightbooking.booking.dto.BookingRequest;
import com.flightbooking.booking.dto.BookingResponse;
import com.flightbooking.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for booking operations.
 * Handles seat holds and booking initiation.
 */
@RestController
@RequestMapping("/v1/booking")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Configure appropriately for production
public class BookingController {

    private final BookingService bookingService;

    /**
     * Initiate a booking by creating seat holds.
     * 
     * @param bookingRequest The booking request with flight details
     * @return BookingResponse with hold details and payment information
     */
    @PostMapping("/initiate")
    public ResponseEntity<BookingResponse> initiateBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        log.info("Received booking initiation request: {} flights for {} passengers",
                bookingRequest.getFlightIds().size(), bookingRequest.getSeatCount());

        try {
            // Validate booking request
            if (!bookingRequest.isValid()) {
                log.warn("Invalid booking request received: {}", bookingRequest);
                return ResponseEntity.badRequest()
                        .body(BookingResponse.builder()
                                .status("FAILED")
                                .message("Invalid booking request")
                                .build());
            }

            // Process booking initiation
            BookingResponse bookingResponse = bookingService.initiateBooking(bookingRequest);

            if ("success".equalsIgnoreCase(bookingResponse.getStatus())) {
                log.info("Booking initiated successfully for session: {}. payment ID: {}",
                        bookingResponse.getSessionId(), bookingResponse.getPaymentId());
                return ResponseEntity.ok(bookingResponse);
            } else {
                log.warn("Booking initiation failed for session: {}. Reason: {}",
                        bookingResponse.getSessionId(), bookingResponse.getMessage());
                return ResponseEntity.badRequest().body(bookingResponse);
            }

        } catch (Exception e) {
            log.error("Error processing booking initiation request: {}", e.getMessage(), e);
            
            return ResponseEntity.internalServerError()
                    .body(BookingResponse.builder()
                            .status("FAILED")
                            .message("Internal server error occurred during booking initiation")
                            .build());
        }
    }

    /**
     * Get booking status by session ID.
     * 
     * @param sessionId The session ID
     * @return BookingResponse with current status
     */
    @GetMapping("/status/{sessionId}")
    public ResponseEntity<BookingResponse> getBookingStatus(@PathVariable String sessionId) {
        log.info("Received booking status request for session: {}", sessionId);

        try {
            // For now, return a placeholder response
            // TODO: Implement actual status lookup
            return ResponseEntity.ok(BookingResponse.builder()
                    .sessionId(sessionId)
                    .status("PENDING")
                    .message("Booking status lookup not yet implemented")
                    .build());

        } catch (Exception e) {
            log.error("Error retrieving booking status for session {}: {}", sessionId, e.getMessage(), e);
            
            return ResponseEntity.internalServerError()
                    .body(BookingResponse.builder()
                            .sessionId(sessionId)
                            .status("FAILED")
                            .message("Error retrieving booking status")
                            .build());
        }
    }

    /**
     * Health check endpoint for booking service.
     */
    @GetMapping("/health")
    public ResponseEntity<java.util.Map<String, String>> health() {
        return ResponseEntity.ok(java.util.Map.of(
                "status", "UP",
                "service", "booking-service",
                "timestamp", java.time.OffsetDateTime.now().toString()
        ));
    }
}
