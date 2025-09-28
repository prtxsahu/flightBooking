package com.flightbooking.booking.repository;

import com.flightbooking.booking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Seat entity operations.
 * Provides CRUD operations and custom queries for seat management.
 */
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Find all seats for a specific flight instance.
     */
    List<Seat> findByFlightInstanceId(Long flightInstanceId);

    /**
     * Find available seats for a specific flight instance.
     */
    List<Seat> findByFlightInstanceIdAndIsAvailableTrue(Long flightInstanceId);

    /**
     * Find seats by flight instance and cabin class.
     */
    List<Seat> findByFlightInstanceIdAndCabinClass(Long flightInstanceId, String cabinClass);

    /**
     * Find available seats by flight instance and cabin class.
     */
    List<Seat> findByFlightInstanceIdAndCabinClassAndIsAvailableTrue(Long flightInstanceId, String cabinClass);

    /**
     * Find seat by flight instance and seat number.
     */
    Optional<Seat> findByFlightInstanceIdAndSeatNo(Long flightInstanceId, String seatNo);

    /**
     * Count total seats for a flight instance.
     */
    long countByFlightInstanceId(Long flightInstanceId);

    /**
     * Count available seats for a flight instance.
     */
    long countByFlightInstanceIdAndIsAvailableTrue(Long flightInstanceId);

    /**
     * Count seats by cabin class for a flight instance.
     */
    long countByFlightInstanceIdAndCabinClass(Long flightInstanceId, String cabinClass);

    /**
     * Count available seats by cabin class for a flight instance.
     */
    long countByFlightInstanceIdAndCabinClassAndIsAvailableTrue(Long flightInstanceId, String cabinClass);

    /**
     * Find available seats for locking (FOR UPDATE SKIP LOCKED).
     * This is used for atomic seat allocation to prevent race conditions.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.flightInstance.id = :flightInstanceId AND s.isAvailable = true " +
           "AND NOT EXISTS (SELECT 1 FROM SeatHold h WHERE h.seat.id = s.id AND h.expiresAt > CURRENT_TIMESTAMP) " +
           "AND NOT EXISTS (SELECT 1 FROM TicketSeat ts WHERE ts.seat.id = s.id) " +
           "ORDER BY s.seatNo")
    List<Seat> findAvailableSeatsForLocking(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Find and lock available seats with limit for atomic allocation.
     * Uses FOR UPDATE SKIP LOCKED to enable concurrent processing while preventing race conditions.
     * Locks are held until the transaction commits.
     * Note: Uses subqueries instead of LEFT JOIN to avoid PostgreSQL FOR UPDATE restrictions.
     */
    @Query(value = "SELECT s.* FROM seat s " +
           "WHERE s.flight_instance_id = :flightInstanceId " +
           "AND s.is_available = true " +
           "AND NOT EXISTS (SELECT 1 FROM seat_hold h WHERE h.seat_id = s.id AND h.expires_at > NOW()) " +
           "AND NOT EXISTS (SELECT 1 FROM ticket_seat ts WHERE ts.seat_id = s.id) " +
           "ORDER BY s.seat_no " +
           "LIMIT :limit FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<Seat> findAndLockAvailableSeats(
            @Param("flightInstanceId") Long flightInstanceId,
            @Param("limit") int limit);

    /**
     * Find seats that are currently held (not expired).
     */
    @Query("SELECT s FROM Seat s JOIN SeatHold h ON h.seat.id = s.id " +
           "WHERE h.expiresAt > CURRENT_TIMESTAMP AND s.flightInstance.id = :flightInstanceId")
    List<Seat> findHeldSeats(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Find seats that are booked (have associated tickets).
     */
    @Query("SELECT s FROM Seat s JOIN TicketSeat ts ON ts.seat.id = s.id " +
           "WHERE s.flightInstance.id = :flightInstanceId")
    List<Seat> findBookedSeats(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Find seats that are truly available (not held and not booked).
     */
    @Query("SELECT s FROM Seat s WHERE s.flightInstance.id = :flightInstanceId AND s.isAvailable = true " +
           "AND NOT EXISTS (SELECT 1 FROM SeatHold h WHERE h.seat.id = s.id AND h.expiresAt > CURRENT_TIMESTAMP) " +
           "AND NOT EXISTS (SELECT 1 FROM TicketSeat ts WHERE ts.seat.id = s.id)")
    List<Seat> findTrulyAvailableSeats(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Count truly available seats for a flight instance.
     */
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.flightInstance.id = :flightInstanceId AND s.isAvailable = true " +
           "AND NOT EXISTS (SELECT 1 FROM SeatHold h WHERE h.seat.id = s.id AND h.expiresAt > CURRENT_TIMESTAMP) " +
           "AND NOT EXISTS (SELECT 1 FROM TicketSeat ts WHERE ts.seat.id = s.id)")
    long countTrulyAvailableSeats(@Param("flightInstanceId") Long flightInstanceId);
}
