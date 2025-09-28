package com.flightbooking.booking.repository;

import com.flightbooking.booking.entity.TicketSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for TicketSeat entity operations.
 * Provides CRUD operations and custom queries for ticket-seat relationship management.
 */
@Repository
public interface TicketSeatRepository extends JpaRepository<TicketSeat, Long> {

    /**
     * Find all seat assignments for a specific ticket.
     */
    List<TicketSeat> findByTicketId(Long ticketId);

    /**
     * Find ticket seat by seat ID (should be unique).
     */
    Optional<TicketSeat> findBySeatId(Long seatId);

    /**
     * Find all seat assignments for a specific flight instance.
     */
    @Query("SELECT ts FROM TicketSeat ts JOIN ts.seat s WHERE s.flightInstance.id = :flightInstanceId")
    List<TicketSeat> findByFlightInstanceId(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Find seat assignments for tickets with a specific status.
     */
    @Query("SELECT ts FROM TicketSeat ts JOIN ts.ticket t WHERE t.status = :status")
    List<TicketSeat> findByTicketStatus(@Param("status") com.flightbooking.booking.entity.Ticket.TicketStatus status);

    /**
     * Find seat assignments for tickets belonging to a specific user.
     */
    @Query("SELECT ts FROM TicketSeat ts JOIN ts.ticket t WHERE t.holderUserId = :userId")
    List<TicketSeat> findByUserId(@Param("userId") Long userId);

    /**
     * Find seat assignments for a specific user and flight instance.
     */
    @Query("SELECT ts FROM TicketSeat ts JOIN ts.ticket t JOIN ts.seat s " +
           "WHERE t.holderUserId = :userId AND s.flightInstance.id = :flightInstanceId")
    List<TicketSeat> findByUserIdAndFlightInstanceId(
            @Param("userId") Long userId, 
            @Param("flightInstanceId") Long flightInstanceId);

    /**
     * Find seat assignments for confirmed tickets.
     */
    @Query("SELECT ts FROM TicketSeat ts JOIN ts.ticket t WHERE t.status = 'CONFIRMED'")
    List<TicketSeat> findConfirmedTicketSeats();

    /**
     * Find seat assignments for confirmed tickets on a specific flight.
     */
    @Query("SELECT ts FROM TicketSeat ts JOIN ts.ticket t JOIN ts.seat s " +
           "WHERE t.status = 'CONFIRMED' AND s.flightInstance.id = :flightInstanceId")
    List<TicketSeat> findConfirmedTicketSeatsByFlight(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Count seat assignments for a specific ticket.
     */
    long countByTicketId(Long ticketId);

    /**
     * Count seat assignments for a specific flight instance.
     */
    @Query("SELECT COUNT(ts) FROM TicketSeat ts JOIN ts.seat s WHERE s.flightInstance.id = :flightInstanceId")
    long countByFlightInstanceId(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Count confirmed seat assignments for a specific flight instance.
     */
    @Query("SELECT COUNT(ts) FROM TicketSeat ts JOIN ts.ticket t JOIN ts.seat s " +
           "WHERE t.status = 'CONFIRMED' AND s.flightInstance.id = :flightInstanceId")
    long countConfirmedSeatsByFlight(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Count seat assignments for a specific user.
     */
    @Query("SELECT COUNT(ts) FROM TicketSeat ts JOIN ts.ticket t WHERE t.holderUserId = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * Check if a seat is already assigned to a ticket.
     */
    boolean existsBySeatId(Long seatId);

    /**
     * Find seat assignments by cabin class for a flight instance.
     */
    @Query("SELECT ts FROM TicketSeat ts JOIN ts.seat s WHERE s.flightInstance.id = :flightInstanceId AND s.cabinClass = :cabinClass")
    List<TicketSeat> findByFlightInstanceIdAndCabinClass(
            @Param("flightInstanceId") Long flightInstanceId, 
            @Param("cabinClass") String cabinClass);

    /**
     * Count seat assignments by cabin class for a flight instance.
     */
    @Query("SELECT COUNT(ts) FROM TicketSeat ts JOIN ts.seat s WHERE s.flightInstance.id = :flightInstanceId AND s.cabinClass = :cabinClass")
    long countByFlightInstanceIdAndCabinClass(
            @Param("flightInstanceId") Long flightInstanceId, 
            @Param("cabinClass") String cabinClass);

    /**
     * Find seat assignments with seat details for a ticket.
     */
    @Query("SELECT ts FROM TicketSeat ts JOIN FETCH ts.seat s JOIN FETCH s.flightInstance f WHERE ts.ticket.id = :ticketId")
    List<TicketSeat> findWithSeatDetailsByTicket(@Param("ticketId") Long ticketId);

    /**
     * Find seat assignments with ticket details for a flight instance.
     */
    @Query("SELECT ts FROM TicketSeat ts JOIN FETCH ts.ticket t JOIN ts.seat s WHERE s.flightInstance.id = :flightInstanceId")
    List<TicketSeat> findWithTicketDetailsByFlight(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Find seat assignments for a specific seat number and flight instance.
     */
    @Query("SELECT ts FROM TicketSeat ts JOIN ts.seat s WHERE s.flightInstance.id = :flightInstanceId AND s.seatNo = :seatNo")
    Optional<TicketSeat> findByFlightInstanceIdAndSeatNo(
            @Param("flightInstanceId") Long flightInstanceId, 
            @Param("seatNo") String seatNo);

    /**
     * Delete seat assignments for a specific ticket.
     */
    void deleteByTicketId(Long ticketId);

    /**
     * Delete seat assignments for tickets with a specific status.
     */
    @Query("DELETE FROM TicketSeat ts WHERE ts.ticket.status = :status")
    void deleteByTicketStatus(@Param("status") com.flightbooking.booking.entity.Ticket.TicketStatus status);
}
