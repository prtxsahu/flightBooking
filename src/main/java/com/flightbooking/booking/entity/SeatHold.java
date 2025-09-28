package com.flightbooking.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * SeatHold entity representing temporary seat reservations with expiration.
 * Seats are held for a limited time (15 minutes) before being released back to inventory.
 */
@Entity
@Table(name = "seat_hold")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SeatHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_instance_id", nullable = false)
    private FlightInstance flightInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false) // Removed unique constraint
    private Seat seat;

    @Column(name = "holder_session_id", nullable = false)
    private String holderSessionId;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    // Helper method to check if hold is expired
    public boolean isExpired() {
        return expiresAt != null && OffsetDateTime.now().isAfter(expiresAt);
    }

    // Helper method to check if hold is valid (not expired)
    public boolean isValid() {
        return !isExpired();
    }

    // Helper method to get time remaining until expiration
    public long getSecondsUntilExpiration() {
        if (expiresAt == null) {
            return 0;
        }
        return java.time.Duration.between(OffsetDateTime.now(), expiresAt).getSeconds();
    }
}
