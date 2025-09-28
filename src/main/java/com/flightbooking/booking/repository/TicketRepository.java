package com.flightbooking.booking.repository;

import com.flightbooking.booking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Ticket entity operations.
 * Provides CRUD operations and custom queries for ticket management.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Find tickets by user ID.
     */
    List<Ticket> findByHolderUserId(Long holderUserId);

    /**
     * Find tickets by flight instance.
     */
    List<Ticket> findByFlightInstanceId(Long flightInstanceId);

    /**
     * Find tickets by payment ID.
     */
    Optional<Ticket> findByPaymentId(String paymentId);

    /**
     * Find tickets by status.
     */
    List<Ticket> findByStatus(Ticket.TicketStatus status);

    /**
     * Find tickets by user ID and status.
     */
    List<Ticket> findByHolderUserIdAndStatus(Long holderUserId, Ticket.TicketStatus status);

    /**
     * Find tickets by flight instance and status.
     */
    List<Ticket> findByFlightInstanceIdAndStatus(Long flightInstanceId, Ticket.TicketStatus status);

    /**
     * Find tickets created within a time range.
     */
    @Query("SELECT t FROM Ticket t WHERE t.createdAt >= :startTime AND t.createdAt <= :endTime ORDER BY t.createdAt")
    List<Ticket> findTicketsCreatedBetween(
            @Param("startTime") OffsetDateTime startTime, 
            @Param("endTime") OffsetDateTime endTime);

    /**
     * Find tickets by user ID created within a time range.
     */
    @Query("SELECT t FROM Ticket t WHERE t.holderUserId = :userId AND t.createdAt >= :startTime AND t.createdAt <= :endTime ORDER BY t.createdAt")
    List<Ticket> findTicketsByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startTime") OffsetDateTime startTime, 
            @Param("endTime") OffsetDateTime endTime);

    /**
     * Find tickets by flight instance created within a time range.
     */
    @Query("SELECT t FROM Ticket t WHERE t.flightInstance.id = :flightInstanceId AND t.createdAt >= :startTime AND t.createdAt <= :endTime ORDER BY t.createdAt")
    List<Ticket> findTicketsByFlightAndDateRange(
            @Param("flightInstanceId") Long flightInstanceId,
            @Param("startTime") OffsetDateTime startTime, 
            @Param("endTime") OffsetDateTime endTime);

    /**
     * Count tickets by user ID.
     */
    long countByHolderUserId(Long holderUserId);

    /**
     * Count tickets by flight instance.
     */
    long countByFlightInstanceId(Long flightInstanceId);

    /**
     * Count tickets by status.
     */
    long countByStatus(Ticket.TicketStatus status);

    /**
     * Count tickets by user ID and status.
     */
    long countByHolderUserIdAndStatus(Long holderUserId, Ticket.TicketStatus status);

    /**
     * Count tickets by flight instance and status.
     */
    long countByFlightInstanceIdAndStatus(Long flightInstanceId, Ticket.TicketStatus status);

    /**
     * Check if a payment ID already exists.
     */
    boolean existsByPaymentId(String paymentId);

    /**
     * Find tickets with total amount greater than a specified value.
     */
    @Query("SELECT t FROM Ticket t WHERE t.totalAmount >= :minAmount ORDER BY t.totalAmount")
    List<Ticket> findTicketsWithMinAmount(@Param("minAmount") Long minAmount);

    /**
     * Find tickets with total amount within a range.
     */
    @Query("SELECT t FROM Ticket t WHERE t.totalAmount >= :minAmount AND t.totalAmount <= :maxAmount ORDER BY t.totalAmount")
    List<Ticket> findTicketsByAmountRange(
            @Param("minAmount") Long minAmount, 
            @Param("maxAmount") Long maxAmount);

    /**
     * Find tickets by user ID with total amount within a range.
     */
    @Query("SELECT t FROM Ticket t WHERE t.holderUserId = :userId AND t.totalAmount >= :minAmount AND t.totalAmount <= :maxAmount ORDER BY t.totalAmount")
    List<Ticket> findTicketsByUserAndAmountRange(
            @Param("userId") Long userId,
            @Param("minAmount") Long minAmount, 
            @Param("maxAmount") Long maxAmount);

    /**
     * Get total revenue for a flight instance.
     */
    @Query("SELECT SUM(t.totalAmount) FROM Ticket t WHERE t.flightInstance.id = :flightInstanceId AND t.status = 'CONFIRMED'")
    Optional<Long> getTotalRevenueByFlight(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Get total revenue for a user.
     */
    @Query("SELECT SUM(t.totalAmount) FROM Ticket t WHERE t.holderUserId = :userId AND t.status = 'CONFIRMED'")
    Optional<Long> getTotalRevenueByUser(@Param("userId") Long userId);

    /**
     * Find tickets created today.
     */
    @Query("SELECT t FROM Ticket t WHERE t.createdAt >= :startOfDay AND t.createdAt < :endOfDay ORDER BY t.createdAt")
    List<Ticket> findTicketsCreatedToday(@Param("startOfDay") OffsetDateTime startOfDay, @Param("endOfDay") OffsetDateTime endOfDay);

    /**
     * Find tickets created today using current date range.
     * This method uses a more database-agnostic approach.
     */
    @Query("SELECT t FROM Ticket t WHERE t.createdAt >= :startOfDay AND t.createdAt < :endOfDay ORDER BY t.createdAt")
    List<Ticket> findTicketsCreatedTodaySimple(@Param("startOfDay") OffsetDateTime startOfDay, @Param("endOfDay") OffsetDateTime endOfDay);

    /**
     * Find tickets by flight instance with seat information.
     */
    @Query("SELECT DISTINCT t FROM Ticket t LEFT JOIN FETCH t.ticketSeats ts LEFT JOIN FETCH ts.seat s WHERE t.flightInstance.id = :flightInstanceId")
    List<Ticket> findTicketsWithSeatsByFlight(@Param("flightInstanceId") Long flightInstanceId);

    /**
     * Find tickets by user ID with seat information.
     */
    @Query("SELECT DISTINCT t FROM Ticket t LEFT JOIN FETCH t.ticketSeats ts LEFT JOIN FETCH ts.seat s WHERE t.holderUserId = :userId")
    List<Ticket> findTicketsWithSeatsByUser(@Param("userId") Long userId);
}
