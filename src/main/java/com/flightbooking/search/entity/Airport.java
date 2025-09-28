package com.flightbooking.search.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;
import com.flightbooking.search.entity.relationship.DepartsWith;
import com.flightbooking.search.entity.relationship.ArrivesAt;

import java.math.BigDecimal;
import java.util.List;

/**
 * Airport entity for Neo4j representing airport information and connections.
 * Used for graph traversal in flight search operations.
 */
@Node("Airport")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "code")
public class Airport {

    @Id
    @Property("code")
    private String code; // IATA code (unique identifier)

    @Property("name")
    private String name;

    @Property("city")
    private String city;

    @Property("country")
    private String country;

    @Property("lat")
    private BigDecimal latitude;

    @Property("lon")
    private BigDecimal longitude;

    // Relationships
    @Relationship(type = "DEPARTS_WITH", direction = Relationship.Direction.OUTGOING)
    private List<DepartsWith> departingFlights;

    @Relationship(type = "ARRIVES_AT", direction = Relationship.Direction.INCOMING)
    private List<ArrivesAt> arrivingFlights;

    // Helper method to get full location description
    public String getFullLocation() {
        return city + ", " + country;
    }

    // Helper method to check if coordinates are valid
    public boolean hasValidCoordinates() {
        return latitude != null && longitude != null;
    }
}
