package com.flightbooking.booking.service;

import com.flightbooking.booking.entity.*;
import com.flightbooking.booking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing ticket creation and ticket-seat relationships.
 * Handles ticket operations in focused transactions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketSeatRepository ticketSeatRepository;

    /**
     * Creates a ticket and associated ticket-seat relationships in a single transaction.
     * 
     * @param flightInstances List of flight instances for the ticket
     * @param seatHolds List of seat holds to convert to ticket seats
     * @param paymentId Payment ID for the ticket
     * @return Created ticket
     * @throws RuntimeException if ticket creation fails
     */
    /**
     * Creates a ticket with IN_PROGRESS status during booking initiation.
     * No TicketSeat relationships are created yet - they will be created during payment confirmation.
     */
    @Transactional
    public Ticket createTicketInProgress(List<FlightInstance> flightInstances, String paymentId, 
                                       String sessionId, int seatCount) {
        log.info("Creating 1 ticket with IN_PROGRESS status for payment ID {} across {} flights for session {}", 
                paymentId, flightInstances.size(), sessionId);
        
        // Calculate total amount across all flights
        Long totalAmount = calculateTotalAmount(flightInstances, seatCount);
        
        // Create ticket with IN_PROGRESS status (no TicketSeat entries yet)
        Ticket ticket = Ticket.builder()
                .flightInstance(flightInstances.get(0)) // 
                .paymentId(paymentId)
                .sessionId(sessionId)
                .holderUserId(null) // Will be set during payment confirmation
                .totalAmount(totalAmount)
                .status(Ticket.TicketStatus.IN_PROGRESS) // IN_PROGRESS until payment confirmation
                .build();
        
        Ticket savedTicket = ticketRepository.save(ticket);
        
        log.info("Successfully created ticket {} with IN_PROGRESS status for session {}", 
                savedTicket.getId(), sessionId);
        
        return savedTicket;
    }

    /**
     * Confirms an existing ticket and creates TicketSeat relationships.
     * This is called during payment confirmation.
     */
    @Transactional
    public Ticket confirmTicketWithSeats(Ticket ticket, List<SeatHold> seatHolds) {
        log.info("Confirming ticket {} and creating {} seat relationships", 
                ticket.getId(), seatHolds.size());
        
        // Update ticket status and user ID
        ticket.setStatus(Ticket.TicketStatus.CONFIRMED);
        Ticket savedTicket = ticketRepository.save(ticket);
        
        // Create ticket-seat relationships
        List<TicketSeat> ticketSeats = seatHolds.stream()
                .map(seatHold -> TicketSeat.builder()
                        .ticket(savedTicket)
                        .seat(seatHold.getSeat())
                        .build())
                .toList();
        
        ticketSeatRepository.saveAll(ticketSeats);
        
        log.info("Successfully confirmed ticket {} with {} seat relationships", 
                savedTicket.getId(), ticketSeats.size());
        
        return savedTicket;
    }

    /**
     * Calculates the total amount for the ticket across all flights.
     */
    private Long calculateTotalAmount(List<FlightInstance> flightInstances, int seatCountPerFlight) {
        return flightInstances.stream()
                .mapToLong(FlightInstance::getPriceMoney)
                .sum() * seatCountPerFlight;
    }

    /**
     * Finds ticket by payment ID.
     */
    public Ticket findByPaymentId(String paymentId) {
        return ticketRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Ticket not found for payment ID: " + paymentId));
    }

    /**
     * Confirms ticket after successful payment.
     * Updates ticket with user ID and confirmed status.
     */
    @Transactional
    public Ticket confirmTicket(Ticket ticket, Long userId) {
        log.info("Confirming ticket {} for user {}", ticket.getId(), userId);
        
        ticket.setHolderUserId(userId);
        ticket.setStatus(Ticket.TicketStatus.CONFIRMED);
        
        Ticket savedTicket = ticketRepository.save(ticket);
        
        log.info("Successfully confirmed ticket {} for user {}", savedTicket.getId(), userId);
        return savedTicket;
    }

    /**
     * Cancels ticket after payment failure.
     * Updates ticket status and deletes ticket-seat relationships.
     */
    @Transactional
    public void cancelTicket(Ticket ticket) {
        log.info("Cancelling ticket {} due to payment failure", ticket.getId());
        
        // Delete ticket-seat relationships
        ticketSeatRepository.deleteByTicketId(ticket.getId());
        
        // Update ticket status to cancelled
        ticket.setStatus(Ticket.TicketStatus.CANCELLED);
        ticketRepository.save(ticket);
        
        log.info("Successfully cancelled ticket {}", ticket.getId());
    }

    /**
     * Generates a unique payment ID for tracking payments.
     */
    public String generatePaymentId() {
        return "pay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
