package com.flightbooking.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * OutboxEvent entity implementing the transactional outbox pattern.
 * Events are published to Kafka/Redpanda for eventual consistency between services.
 */
@Entity
@Table(name = "outbox_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false)
    private String topic;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private String payload;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "dispatched_at")
    private OffsetDateTime dispatchedAt;

    @Builder.Default
    @Column(nullable = false)
    private Integer attempts = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private EventStatus status = EventStatus.PENDING;

    @Builder.Default
    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 3;

    // Helper method to check if event can be retried
    public boolean canRetry() {
        return attempts < maxRetries && status == EventStatus.PENDING;
    }

    // Helper method to increment attempts
    public void incrementAttempts() {
        this.attempts++;
    }

    // Helper method to mark as dispatched
    public void markAsDispatched() {
        this.status = EventStatus.DISPATCHED;
        this.dispatchedAt = OffsetDateTime.now();
    }

    // Helper method to mark as failed
    public void markAsFailed() {
        this.status = EventStatus.FAILED;
    }

    public enum EventStatus {
        PENDING,
        DISPATCHED,
        FAILED
    }
}
