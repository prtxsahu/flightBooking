package com.flightbooking.booking.service;

import com.flightbooking.booking.entity.*;
import com.flightbooking.booking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service for managing seat holds and atomic seat allocation.
 * Handles the critical seat reservation logic with strong consistency guarantees.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeatHoldService {

    private final FlightInstanceRepository flightInstanceRepository;
    private final SeatRepository seatRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final OutboxEventRepository outboxEventRepository;

    private static final int HOLD_DURATION_MINUTES = 15;

    /**
     * Creates seat holds for a specific flight instance.
     * This is the core atomic operation for seat allocation.
     * 
     * @param flightInstanceId The flight instance ID
     * @param seatCount Number of seats to hold
     * @param sessionId Session identifier for the hold
     * @return List of created seat holds
     * @throws RuntimeException if insufficient seats available
     */
    public List<SeatHold> createSeatHolds(Long flightInstanceId, int seatCount, String sessionId) {
        log.info("Creating {} seat holds for flight {} with session {}", seatCount, flightInstanceId, sessionId);
        
        // Verify flight instance exists
        FlightInstance flightInstance = flightInstanceRepository.findById(flightInstanceId)
                .orElseThrow(() -> new RuntimeException("Flight instance not found: " + flightInstanceId));
        
        // Find and lock available seats (FOR UPDATE SKIP LOCKED)
        List<Seat> availableSeats = seatRepository.findAndLockAvailableSeats(flightInstanceId, seatCount);
        
        // Validate we have enough seats
        if (availableSeats.size() < seatCount) {
            throw new RuntimeException(String.format("Insufficient seats available. Requested: %d, Available: %d", 
                    seatCount, availableSeats.size()));
        }
        
        // Create seat holds
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);
        List<SeatHold> seatHolds = availableSeats.stream()
                .map(seat -> SeatHold.builder()
                        .flightInstance(flightInstance)
                        .seat(seat)
                        .holderSessionId(sessionId)
                        .expiresAt(expiresAt)
                        .build())
                .toList();
        
        // Save all holds in a single transaction
        List<SeatHold> savedHolds = seatHoldRepository.saveAll(seatHolds);
        
        log.info("Successfully created {} seat holds for session {} on flight {}", 
                savedHolds.size(), sessionId, flightInstanceId);
        
        return savedHolds;
    }

    /**
     * Finds all active seat holds for a session.
     */
    public List<SeatHold> findActiveHoldsBySession(String sessionId) {
        return seatHoldRepository.findActiveHoldsBySession(sessionId, OffsetDateTime.now());
    }

    /**
     * Confirms seat holds by marking seats as booked and deleting holds.
     * This is called when payment is successful.
     */
    @Transactional
    public List<Seat> confirmSeatHolds(String sessionId) {
        log.info("Confirming seat holds for session {}", sessionId);
        
        List<SeatHold> seatHolds = findActiveHoldsBySession(sessionId);
        
        if (seatHolds.isEmpty()) {
            throw new RuntimeException("No active seat holds found for session: " + sessionId);
        }
        
        // Mark seats as booked (unavailable)
        List<Seat> seats = seatHolds.stream()
                .map(SeatHold::getSeat)
                .peek(seat -> seat.markAsUnavailable())
                .toList();
        
        // Delete seat holds (seats are now permanently booked)
        seatHoldRepository.deleteAll(seatHolds);
        
        log.info("Successfully confirmed {} seat holds for session {}", seats.size(), sessionId);
        return seats;
    }

    /**
     * Cancels seat holds by deleting them.
     * This is called when payment fails.
     */
    @Transactional
    public void cancelSeatHolds(String sessionId) {
        log.info("Cancelling seat holds for session {}", sessionId);
        
        int deletedCount = seatHoldRepository.deleteHoldsBySession(sessionId);
        
        log.info("Successfully cancelled {} seat holds for session {}", deletedCount, sessionId);
    }

    /**
     * Cleans up expired seat holds to prevent unique constraint violations.
     * This method should be called before creating new seat holds.
     */


    @Scheduled(fixedRate = 60000) // Every 1 minute
    @Transactional
    public void cleanupExpiredHolds() {
        // for better performance , clean up with limits
        int deletedCount = seatHoldRepository.deleteExpiredHolds(OffsetDateTime.now());
        if (deletedCount > 0) {
            log.info("Cleaned up {} expired seat holds", deletedCount);
        }
    }
}
