package com.flightbooking.search.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;
import com.flightbooking.search.entity.relationship.DepartsWith;
import com.flightbooking.search.entity.relationship.ArrivesAt;
import com.flightbooking.search.entity.relationship.IncludesLeg;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * FlightInstance node for Neo4j representing flight data for search operations.
 * This is synchronized from PostgreSQL via events and used for fast graph traversal.
 */
@Node("FlightInstance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class FlightInstanceNode {

    @Id
    @Property("id")
    private String id; // String version of PostgreSQL ID for Neo4j

    @Property("flight_no")
    private String flightNo;

    @Property("departureTime")
    private OffsetDateTime departureTime;

    @Property("arrivalTime")
    private OffsetDateTime arrivalTime;

    @Property("source")
    private String source; // Airport code

    @Property("destination")
    private String destination; // Airport code

    @Property("priceMoney")
    private Long priceMoney; // Price in cents

    @Property("remainingSeats")
    private Integer remainingSeats;

    @Property("heldSeats")
    @Builder.Default
    private Integer heldSeats = 0;

    @Property("totalAvailableSeats")
    private Integer totalAvailableSeats;

    @Property("totalSeats")
    private Integer totalSeats;

    @Property("status")
    @Builder.Default
    private String status = "ACTIVE";

    @Property("lastUpdated")
    private OffsetDateTime lastUpdated;

    @Property("lastHoldExpiry")
    private OffsetDateTime lastHoldExpiry;

    @Property("reconciled_at")
    private OffsetDateTime reconciledAt;

    // Relationships
    @Relationship(type = "DEPARTS_WITH", direction = Relationship.Direction.INCOMING)
    private List<DepartsWith> departureRelationships;

    @Relationship(type = "ARRIVES_AT", direction = Relationship.Direction.OUTGOING)
    private List<ArrivesAt> arrivalRelationships;

    @Relationship(type = "INCLUDES_LEG", direction = Relationship.Direction.INCOMING)
    private List<IncludesLeg> itineraryLegs;

    // Helper method to get total available seats (remaining + held)
    public Integer getTotalAvailableSeats() {
        // Use totalAvailableSeats if available, otherwise fall back to remainingSeats + heldSeats
        if (totalAvailableSeats != null) {
            return totalAvailableSeats;
        }
        return (remainingSeats != null ? remainingSeats : 0) + (heldSeats != null ? heldSeats : 0);
    }

    // Helper method to check if flight has available seats
    public boolean hasAvailableSeats(int requestedSeats) {
        return getTotalAvailableSeats() >= requestedSeats;
    }

    // Helper method to get price as BigDecimal
    public java.math.BigDecimal getPriceAsBigDecimal() {
        return new java.math.BigDecimal(priceMoney).divide(new java.math.BigDecimal(100));
    }

    // Helper method to check if flight is active
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}
