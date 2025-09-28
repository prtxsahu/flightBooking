package com.flightbooking.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Ticket entity representing confirmed bookings.
 * A ticket is created when payment is confirmed and seat holds are converted to permanent bookings.
 */
@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_instance_id", nullable = true) // Nullable for multi-flight tickets
    private FlightInstance flightInstance; // Primary flight for backward compatibility

    @Column(name = "payment_id", unique = true)
    private String paymentId;

    @Column(name = "holder_user_id")
    private Long holderUserId;

    @Column(name = "session_id", nullable = false)
    private String sessionId; // Session ID from the original booking request

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount; // Amount in cents

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TicketStatus status = TicketStatus.CONFIRMED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<TicketSeat> ticketSeats = new ArrayList<>();

    // Helper method to get total amount as BigDecimal
    public java.math.BigDecimal getTotalAmountAsBigDecimal() {
        return new java.math.BigDecimal(totalAmount).divide(new java.math.BigDecimal(100));
    }

    // Helper method to add a seat to this ticket
    public void addSeat(Seat seat) {
        TicketSeat ticketSeat = TicketSeat.builder()
                .ticket(this)
                .seat(seat)
                .build();
        this.ticketSeats.add(ticketSeat);
    }

    // Helper method to get seat count
    public int getSeatCount() {
        return ticketSeats != null ? ticketSeats.size() : 0;
    }

    public enum TicketStatus {
        CONFIRMED,
        IN_PROGRESS,
        CANCELLED,
        REFUNDED
    }
}
