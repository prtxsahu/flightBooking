package com.flightbooking.search.entity.relationship;

import com.flightbooking.search.entity.FlightInstanceNode;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.OffsetDateTime;

/**
 * Relationship entity representing flights departing from an airport.
 * This enables efficient graph traversal for flight searches.
 */
@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartsWith {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private FlightInstanceNode flight;

    @Property("departure_time")
    private OffsetDateTime departureTime;

    @Property("created_at")
    private OffsetDateTime createdAt;

    // Helper method to check if this is an active departure
    public boolean isActiveDeparture() {
        return flight != null && flight.isActive();
    }
}
