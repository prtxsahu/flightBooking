package com.flightbooking.booking.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * TicketSeat entity representing the relationship between tickets and seats.
 * This is a junction table that maps which seats belong to which ticket.
 */
@Entity
@Table(name = "ticket_seat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TicketSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Include
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Include
    private Seat seat;

    // Helper method to get seat number
    public String getSeatNumber() {
        return seat != null ? seat.getSeatNo() : null;
    }

    // Helper method to get cabin class
    public String getCabinClass() {
        return seat != null ? seat.getCabinClass() : null;
    }
}
