package com.flightbooking.booking.repository;

import com.flightbooking.booking.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for OutboxEvent entity operations.
 * Provides CRUD operations and custom queries for outbox event management.
 */
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    /**
     * Find events by status.
     */
    List<OutboxEvent> findByStatus(OutboxEvent.EventStatus status);

    /**
     * Find events by topic.
     */
    List<OutboxEvent> findByTopic(String topic);

    /**
     * Find events by status and topic.
     */
    List<OutboxEvent> findByStatusAndTopic(OutboxEvent.EventStatus status, String topic);

    /**
     * Find pending events ordered by creation time.
     */
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = 'PENDING' ORDER BY e.createdAt")
    List<OutboxEvent> findPendingEventsOrderedByCreatedAt();

    /**
     * Find pending events with limit for processing.
     * Uses FOR UPDATE SKIP LOCKED to prevent multiple processors from picking the same events.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT * FROM outbox_event WHERE status = 'PENDING' ORDER BY id LIMIT :limit FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxEvent> findPendingEventsForProcessing(@Param("limit") int limit);

    /**
     * Find pending events by topic with limit.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT * FROM outbox_event WHERE status = 'PENDING' AND topic = :topic ORDER BY id LIMIT :limit FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxEvent> findPendingEventsByTopicForProcessing(@Param("topic") String topic, @Param("limit") int limit);

    /**
     * Find events by event ID (UUID).
     */
    Optional<OutboxEvent> findByEventId(UUID eventId);

    /**
     * Find events created within a time range.
     */
    @Query("SELECT e FROM OutboxEvent e WHERE e.createdAt >= :startTime AND e.createdAt <= :endTime ORDER BY e.createdAt")
    List<OutboxEvent> findEventsCreatedBetween(
            @Param("startTime") OffsetDateTime startTime, 
            @Param("endTime") OffsetDateTime endTime);

    /**
     * Find events by status created within a time range.
     */
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = :status AND e.createdAt >= :startTime AND e.createdAt <= :endTime ORDER BY e.createdAt")
    List<OutboxEvent> findEventsByStatusAndDateRange(
            @Param("status") OutboxEvent.EventStatus status,
            @Param("startTime") OffsetDateTime startTime, 
            @Param("endTime") OffsetDateTime endTime);

    /**
     * Find failed events that can be retried.
     */
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = 'PENDING' AND e.attempts < e.maxRetries ORDER BY e.createdAt")
    List<OutboxEvent> findRetryableEvents();

    /**
     * Find failed events that have exceeded max retries.
     */
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = 'PENDING' AND e.attempts >= e.maxRetries ORDER BY e.createdAt")
    List<OutboxEvent> findFailedEventsExceededRetries();

    /**
     * Find dispatched events within a time range.
     */
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = 'DISPATCHED' AND e.dispatchedAt >= :startTime AND e.dispatchedAt <= :endTime ORDER BY e.dispatchedAt")
    List<OutboxEvent> findDispatchedEventsBetween(
            @Param("startTime") OffsetDateTime startTime, 
            @Param("endTime") OffsetDateTime endTime);

    /**
     * Count events by status.
     */
    long countByStatus(OutboxEvent.EventStatus status);

    /**
     * Count events by topic.
     */
    long countByTopic(String topic);

    /**
     * Count pending events.
     */
    @Query("SELECT COUNT(e) FROM OutboxEvent e WHERE e.status = 'PENDING'")
    long countPendingEvents();

    /**
     * Count pending events by topic.
     */
    @Query("SELECT COUNT(e) FROM OutboxEvent e WHERE e.status = 'PENDING' AND e.topic = :topic")
    long countPendingEventsByTopic(@Param("topic") String topic);

    /**
     * Count events that can be retried.
     */
    @Query("SELECT COUNT(e) FROM OutboxEvent e WHERE e.status = 'PENDING' AND e.attempts < e.maxRetries")
    long countRetryableEvents();

    /**
     * Count events that have exceeded max retries.
     */
    @Query("SELECT COUNT(e) FROM OutboxEvent e WHERE e.status = 'PENDING' AND e.attempts >= e.maxRetries")
    long countFailedEventsExceededRetries();

    /**
     * Mark events as dispatched.
     */
    @Modifying
    @Query("UPDATE OutboxEvent e SET e.status = 'DISPATCHED', e.dispatchedAt = :dispatchedAt WHERE e.id IN :eventIds")
    int markEventsAsDispatched(@Param("eventIds") List<Long> eventIds, @Param("dispatchedAt") OffsetDateTime dispatchedAt);

    /**
     * Increment attempt count for events.
     */
    @Modifying
    @Query("UPDATE OutboxEvent e SET e.attempts = e.attempts + 1 WHERE e.id IN :eventIds")
    int incrementAttemptCount(@Param("eventIds") List<Long> eventIds);

    /**
     * Mark events as failed.
     */
    @Modifying
    @Query("UPDATE OutboxEvent e SET e.status = 'FAILED' WHERE e.id IN :eventIds")
    int markEventsAsFailed(@Param("eventIds") List<Long> eventIds);

    /**
     * Delete dispatched events older than a specific time.
     */
    @Modifying
    @Query("DELETE FROM OutboxEvent e WHERE e.status = 'DISPATCHED' AND e.dispatchedAt < :cutoffTime")
    int deleteDispatchedEventsOlderThan(@Param("cutoffTime") OffsetDateTime cutoffTime);

    /**
     * Delete failed events older than a specific time.
     */
    @Modifying
    @Query("DELETE FROM OutboxEvent e WHERE e.status = 'FAILED' AND e.createdAt < :cutoffTime")
    int deleteFailedEventsOlderThan(@Param("cutoffTime") OffsetDateTime cutoffTime);

    /**
     * Check if an event ID already exists.
     */
    boolean existsByEventId(UUID eventId);

    /**
     * Find events by topic and status ordered by creation time.
     */
    @Query("SELECT e FROM OutboxEvent e WHERE e.topic = :topic AND e.status = :status ORDER BY e.createdAt")
    List<OutboxEvent> findByTopicAndStatusOrderedByCreatedAt(
            @Param("topic") String topic, 
            @Param("status") OutboxEvent.EventStatus status);
}
