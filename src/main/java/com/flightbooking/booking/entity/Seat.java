package com.flightbooking.booking.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Seat entity representing individual seats within a flight instance.
 * Each seat belongs to a specific flight and can be held or booked.
 */
@Entity
@Table(name = "seat", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"flight_instance_id", "seat_no"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_instance_id", nullable = false)
    private FlightInstance flightInstance;

    @Column(name = "seat_no", nullable = false)
    private String seatNo;

    @Column(name = "cabin_class")
    private String cabinClass;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Version
    @Column(nullable = false)
    private Integer version = 0;


    // Helper method to check if seat is available
    public boolean isSeatAvailable() {
        return isAvailable != null && isAvailable;
    }

    // Helper method to make seat unavailable
    public void markAsUnavailable() {
        this.isAvailable = false;
    }

    // Helper method to make seat available
    public void markAsAvailable() {
        this.isAvailable = true;
    }

}
