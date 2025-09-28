package com.flightbooking.search.entity.relationship;

import com.flightbooking.search.entity.FlightInstanceNode;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.OffsetDateTime;

/**
 * Relationship entity representing flight legs included in an itinerary.
 * This enables efficient graph traversal and stores leg-specific metadata.
 */
@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncludesLeg {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private FlightInstanceNode flight;

    @Property("leg_index")
    private Integer legIndex; // 0, 1, 2... for order of flights in itinerary

    @Property("leg_price")
    private Long legPrice; // Price for this specific leg in cents

    @Property("leg_duration")
    private Long legDuration; // Duration of this leg in seconds

    @Property("created_at")
    private OffsetDateTime createdAt;

    // Helper method to check if this is a valid leg
    public boolean isValidLeg() {
        return legIndex != null && legIndex >= 0 && flight != null && flight.isActive();
    }

    // Helper method to get leg price as BigDecimal
    public java.math.BigDecimal getLegPriceAsBigDecimal() {
        return new java.math.BigDecimal(legPrice).divide(new java.math.BigDecimal(100));
    }

    // Helper method to get leg duration in hours
    public java.math.BigDecimal getLegDurationInHours() {
        return new java.math.BigDecimal(legDuration).divide(new java.math.BigDecimal(3600), 2, java.math.RoundingMode.HALF_UP);
    }
}
