package com.flightbooking.search.entity.relationship;

import com.flightbooking.search.entity.Airport;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.OffsetDateTime;

/**
 * Relationship entity representing flights arriving at an airport.
 * This enables efficient graph traversal for flight searches.
 */
@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArrivesAt {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private Airport airport;

    @Property("arrival_time")
    private OffsetDateTime arrivalTime;

    @Property("created_at")
    private OffsetDateTime createdAt;

    // Helper method to check if this is an active arrival
    public boolean isActiveArrival() {
        return airport != null; // Airport is always "active" - flights are the ones that can be inactive
    }
}
