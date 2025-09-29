package com.flightbooking.booking.service;

import com.flightbooking.booking.dto.BookingRequest;
import com.flightbooking.booking.dto.BookingResponse;
import com.flightbooking.booking.dto.PaymentRequest;
import com.flightbooking.booking.dto.PaymentResponse;
import com.flightbooking.booking.entity.*;
import com.flightbooking.booking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing booking operations including seat holds and ticket creation.
 * Handles the complete booking flow from initiation to confirmation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final FlightInstanceRepository flightInstanceRepository;
    private final SeatHoldService seatHoldService;
    private final TicketService ticketService;

    /**
     * Initiates a booking by creating seat holds for multiple flights sequentially.
     * If any flight fails, cleans up all previously held seats.
     * 
     * @param bookingRequest The booking request containing flight IDs and seat details
     * @return BookingResponse with booking details and payment information
     * @throws RuntimeException if booking cannot be initiated
     */
    public BookingResponse initiateBooking(BookingRequest bookingRequest) {
        // Generate unique session ID for this booking
        String sessionId = generateSessionId();
        
        log.info("Initiating booking for {} flights with {} seats for session {}", 
                bookingRequest.getFlightIds().size(), bookingRequest.getSeatCount(), sessionId);
        
        // Validate booking request
        if (!bookingRequest.isValid()) {
            throw new RuntimeException("Invalid booking request");
        }
        
        List<Long> flightIds = bookingRequest.getFlightIds();
        int seatCount = bookingRequest.getSeatCount();
        
        // Verify all flight instances exist
        List<FlightInstance> flightInstances = flightIds.stream()
                .map(flightId -> flightInstanceRepository.findById(flightId)
                        .orElseThrow(() -> new RuntimeException("Flight instance not found: " + flightId)))
                .toList();
        
        List<SeatHold> allSeatHolds = new ArrayList<>();
        
        try {
            // Create seat holds sequentially for each flight
            for (int i = 0; i < flightIds.size(); i++) {
                Long flightId = flightIds.get(i);
                log.debug("Creating seat holds for flight {} (leg {}/{})", flightId, i + 1, flightIds.size());
                
                List<SeatHold> seatHolds = seatHoldService.createSeatHolds(flightId, seatCount, sessionId);
                allSeatHolds.addAll(seatHolds);
                
                log.debug("Successfully created {} seat holds for flight {}", seatHolds.size(), flightId);
            }
            
            // Generate payment ID for this booking
            String paymentId = ticketService.generatePaymentId();
            
            // Create ticket with IN_PROGRESS status (no TicketSeat entries yet)
            Ticket ticket = ticketService.createTicketInProgress(
                    flightInstances, paymentId, sessionId, seatCount);
            
            // Calculate total amount for all flights
            Long totalAmount = flightInstances.stream()
                    .mapToLong(flight -> flight.getPriceMoney() * seatCount)
                    .sum();
            
            // Extract seat numbers for response
            List<String> seatNumbers = allSeatHolds.stream()
                    .map(seatHold -> seatHold.getSeat().getSeatNo())
                    .toList();
            
            log.info("Successfully initiated booking for {} flights with {} seat holds, ticket {} and payment ID {}", 
                    flightIds.size(), allSeatHolds.size(), ticket.getId(), paymentId);
            
            // Return booking response with ticket reference
            return BookingResponse.success(
                    paymentId, // Payment ID for confirmation
                    flightIds.get(0), // Primary flight ID for backward compatibility
                    seatCount,
                    sessionId,
                    allSeatHolds.get(0).getExpiresAt(), // All holds have same expiration
                    totalAmount, // Total amount for payment
                    seatNumbers
            );
            
        } catch (Exception e) {
            // Cleanup: Delete all seat holds for this session if any flight failed
            log.error("Failed to create booking for session {}, cleaning up any existing holds", sessionId);
            seatHoldService.cancelSeatHolds(sessionId);
            return BookingResponse.failure(
                    e.getMessage(), // Payment ID for confirmation
                    flightIds.get(0), // Primary flight ID for backward compatibility
                    seatCount,
                    sessionId
            );        }
    }

    /**
     * Handles payment confirmation or failure.
     * Updates ticket status and manages seat holds accordingly.
     * 
     * @param paymentRequest Payment request with status and details
     * @return PaymentResponse with confirmation details
     * @throws RuntimeException if payment processing fails
     */
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        log.info("Processing payment {} with status {} for session {}", 
                paymentRequest.getPaymentId(), paymentRequest.getPaymentStatus());
        
        // Validate payment request
        if (!paymentRequest.isValid()) {
            throw new RuntimeException("Invalid payment request");
        }
        
        String paymentId = paymentRequest.getPaymentId();
        
        try {
            if (paymentRequest.isPaymentSuccessful()) {
                return handlePaymentSuccess(paymentRequest);
            } else {
                return handlePaymentFailure(paymentRequest);
            }
        } catch (Exception e) {
            log.error("Failed to process payment {}: {}", 
                    paymentId, e.getMessage());
            throw e;
        }
    }

    /**
     * Handles successful payment confirmation.
     * Delegates to appropriate services for ticket and seat hold management.
     */
    private PaymentResponse handlePaymentSuccess(PaymentRequest paymentRequest) {
        String paymentId = paymentRequest.getPaymentId();
        
        log.info("Processing successful payment {}", paymentId);
        
        // Find existing ticket by payment ID
        Ticket ticket = ticketService.findByPaymentId(paymentId);
        if (ticket == null) {
            throw new RuntimeException("No ticket found for payment ID: " + paymentId);
        }
        String sessionId = ticket.getSessionId();

        
        // Get all active seat holds for this session
        List<SeatHold> seatHolds = seatHoldService.findActiveHoldsBySession(sessionId);
        
        if (seatHolds.isEmpty()) {
            throw new IllegalArgumentException("No active seat holds found for session: Payment will be refunded to account  " + sessionId);
        }
        
        // Confirm ticket and create TicketSeat relationships
        Ticket confirmedTicket = ticketService.confirmTicketWithSeats(
                ticket, seatHolds);
        
        // Confirm seat holds (mark seats as unavailable and delete holds)
        List<Seat> bookedSeats = seatHoldService.confirmSeatHolds(sessionId);
        
        // Extract seat numbers for response
        List<String> seatNumbers = bookedSeats.stream()
                .map(Seat::getSeatNo)
                .toList();
        
        log.info("Successfully confirmed payment {} for session {} with ticket {} and {} seats", 
                paymentId, sessionId, confirmedTicket.getId(), seatNumbers.size());
        
        return PaymentResponse.success(
                paymentId,
                sessionId,
                confirmedTicket.getId(),
                confirmedTicket.getFlightInstance() != null ? confirmedTicket.getFlightInstance().getFlightNo() : "MULTI",
                seatNumbers,
                confirmedTicket.getTotalAmount(),
                "TKT-" + confirmedTicket.getId()
        );
    }

    /**
     * Handles payment failure.
     * Delegates to appropriate services for cleanup.
     */
    private PaymentResponse handlePaymentFailure(PaymentRequest paymentRequest) {
        String paymentId = paymentRequest.getPaymentId();
        
        log.info("Processing failed payment {}", paymentId);
        
        try {
            // Find ticket by payment ID using TicketService
            Ticket ticket = ticketService.findByPaymentId(paymentId);
            String sessionId = ticket.getSessionId();

            
            // Cancel ticket using TicketService
            ticketService.cancelTicket(ticket);
            
            // Cancel seat holds using SeatHoldService
            seatHoldService.cancelSeatHolds(sessionId);
            
            log.info("Successfully cleaned up failed payment {} for session {}", paymentId, sessionId);
            
            return PaymentResponse.failure(
                    paymentId,
                    "Payment failed - booking cancelled",
                    paymentRequest.getPaymentStatus()
            );
            
        } catch (Exception e) {
            log.error("Error during payment failure cleanup : {}", e.getMessage());

            throw e;
        }
    }

    /**
     * Generates a unique session ID for booking operations.
     * Uses UUID with timestamp prefix for better traceability.
     * 
     * @return Unique session ID string
     */
    private String generateSessionId() {
        return "sess_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }

}
