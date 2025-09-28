package com.flightbooking.search.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;
import com.flightbooking.search.entity.relationship.IncludesLeg;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Itinerary node for Neo4j representing cached search results.
 * These are pre-computed flight combinations for fast search performance.
 */
@Node("Itinerary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Itinerary {

    @Id
    @Property("id")
    private String id; // UUID for the itinerary

    @Property("source")
    private String source; // Source airport code

    @Property("destination")
    private String destination; // Destination airport code

    @Property("departure_date")
    private LocalDate departureDate;

    @Property("legs")
    private Integer legs; // Number of flight legs (1 = direct, 2 = 1 stop, 3 = 2 stops)

    @Property("total_price")
    private Long totalPrice; // Total price in cents

    @Property("total_duration")
    private Long totalDuration; // Total duration in seconds

    @Property("minAvailableSeats")
    private Integer minAvailableSeats; // Minimum available seats across all legs

    @Property("created_at")
    private OffsetDateTime createdAt;

    @Property("search_key")
    private String searchKey; // Composite key for fast lookup

    @Property("flight_legs")
    private String flightLegsJson; // JSON array of flight leg details for fast reads

    @Property("flightLegsHash")
    private String flightLegsHash; // Hash of flight leg IDs for uniqueness constraint

    // Relationships
    @Relationship(type = "INCLUDES_LEG", direction = Relationship.Direction.OUTGOING)
    private List<IncludesLeg> flightLegs;

    @Relationship(type = "FROM", direction = Relationship.Direction.OUTGOING)
    private Airport sourceAirport;

    @Relationship(type = "TO", direction = Relationship.Direction.OUTGOING)
    private Airport destinationAirport;

    // Helper method to get total price as BigDecimal
    public java.math.BigDecimal getTotalPriceAsBigDecimal() {
        return new java.math.BigDecimal(totalPrice).divide(new java.math.BigDecimal(100));
    }

    // Helper method to get total duration in hours
    public BigDecimal getTotalDurationInHours() {
        return new BigDecimal(totalDuration).divide(new BigDecimal(3600), 2, java.math.RoundingMode.HALF_UP);
    }

    // Helper method to generate search key (passenger-agnostic)
    public static String generateSearchKey(String source, String destination, LocalDate departureDate) {
        return source + "-" + destination + "-" + departureDate;
    }

    // Helper method to check if itinerary is for direct flight
    public boolean isDirectFlight() {
        return legs != null && legs == 1;
    }

    // Helper method to check if itinerary is for connecting flights
    public boolean isConnectingFlight() {
        return legs != null && legs > 1;
    }

    // Helper method to get stops count
    public int getStopsCount() {
        return legs != null ? Math.max(0, legs - 1) : 0;
    }
}
