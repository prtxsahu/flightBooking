package com.flightbooking.booking.repository;

import com.flightbooking.booking.entity.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for SeatHold entity operations.
 * Provides CRUD operations and custom queries for seat hold management.
 */
@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {

    /**
     * Find all holds for a specific session.
     */
    List<SeatHold> findByHolderSessionId(String holderSessionId);

    /**
     * Find holds for a specific flight instance.
     */
    List<SeatHold> findByFlightInstanceId(Long flightInstanceId);

    /**
     * Find holds for a specific session and flight instance.
     */
    List<SeatHold> findByHolderSessionIdAndFlightInstanceId(String holderSessionId, Long flightInstanceId);

    /**
     * Find active (non-expired) holds for a session.
     */
    @Query("SELECT h FROM SeatHold h WHERE h.holderSessionId = :sessionId AND h.expiresAt > :currentTime")
    List<SeatHold> findActiveHoldsBySession(@Param("sessionId") String sessionId, @Param("currentTime") OffsetDateTime currentTime);

    /**
     * Find active (non-expired) holds for a flight instance.
     */
    @Query("SELECT h FROM SeatHold h WHERE h.flightInstance.id = :flightInstanceId AND h.expiresAt > :currentTime")
    List<SeatHold> findActiveHoldsByFlight(@Param("flightInstanceId") Long flightInstanceId, @Param("currentTime") OffsetDateTime currentTime);

    /**
     * Find active (non-expired) holds for a session and flight instance.
     */
    @Query("SELECT h FROM SeatHold h WHERE h.holderSessionId = :sessionId AND h.flightInstance.id = :flightInstanceId AND h.expiresAt > :currentTime")
    List<SeatHold> findActiveHoldsBySessionAndFlight(
            @Param("sessionId") String sessionId, 
            @Param("flightInstanceId") Long flightInstanceId, 
            @Param("currentTime") OffsetDateTime currentTime);

    /**
     * Find expired holds.
     */
    @Query("SELECT h FROM SeatHold h WHERE h.expiresAt <= :currentTime")
    List<SeatHold> findExpiredHolds(@Param("currentTime") OffsetDateTime currentTime);

    /**
     * Find holds expiring within a specific time window.
     */
    @Query("SELECT h FROM SeatHold h WHERE h.expiresAt > :currentTime AND h.expiresAt <= :expiryWindow")
    List<SeatHold> findHoldsExpiringWithin(@Param("currentTime") OffsetDateTime currentTime, @Param("expiryWindow") OffsetDateTime expiryWindow);

    /**
     * Find hold by seat ID (should be unique).
     */
    Optional<SeatHold> findBySeatId(Long seatId);

    /**
     * Check if a seat is currently held (not expired).
     */
    @Query("SELECT COUNT(h) > 0 FROM SeatHold h WHERE h.seat.id = :seatId AND h.expiresAt > :currentTime")
    boolean isSeatCurrentlyHeld(@Param("seatId") Long seatId, @Param("currentTime") OffsetDateTime currentTime);

    /**
     * Count active holds for a session.
     */
    @Query("SELECT COUNT(h) FROM SeatHold h WHERE h.holderSessionId = :sessionId AND h.expiresAt > :currentTime")
    long countActiveHoldsBySession(@Param("sessionId") String sessionId, @Param("currentTime") OffsetDateTime currentTime);

    /**
     * Count active holds for a flight instance.
     */
    @Query("SELECT COUNT(h) FROM SeatHold h WHERE h.flightInstance.id = :flightInstanceId AND h.expiresAt > :currentTime")
    long countActiveHoldsByFlight(@Param("flightInstanceId") Long flightInstanceId, @Param("currentTime") OffsetDateTime currentTime);

    /**
     * Delete expired holds.
     */
    @Modifying
    @Query("DELETE FROM SeatHold h WHERE h.expiresAt <= :currentTime")
    int deleteExpiredHolds(@Param("currentTime") OffsetDateTime currentTime);

    /**
     * Delete holds for a specific session.
     */
    @Modifying
    @Query("DELETE FROM SeatHold h WHERE h.holderSessionId = :sessionId")
    int deleteHoldsBySession(@Param("sessionId") String sessionId);

    /**
     * Delete active holds for a specific session and flight instance.
     */
    @Modifying
    @Query("DELETE FROM SeatHold h WHERE h.holderSessionId = :sessionId AND h.flightInstance.id = :flightInstanceId AND h.expiresAt > :currentTime")
    int deleteActiveHoldsBySessionAndFlight(
            @Param("sessionId") String sessionId, 
            @Param("flightInstanceId") Long flightInstanceId, 
            @Param("currentTime") OffsetDateTime currentTime);

    /**
     * Find holds created within a time range.
     */
    @Query("SELECT h FROM SeatHold h WHERE h.createdAt >= :startTime AND h.createdAt <= :endTime ORDER BY h.createdAt")
    List<SeatHold> findHoldsCreatedBetween(
            @Param("startTime") OffsetDateTime startTime, 
            @Param("endTime") OffsetDateTime endTime);
}
