package com.flightbooking.search.repository;

import com.flightbooking.search.entity.FlightInstanceNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Repository for FlightInstanceNode entity operations in Neo4j.
 * Provides essential queries for flight search and graph traversal.
 */
@Repository
public interface FlightInstanceNodeRepository extends Neo4jRepository<FlightInstanceNode, String> {


    /**
     * Find direct flights between two airports.
     */
    @Query("MATCH (f:FlightInstance) WHERE f.source = $source AND f.destination = $destination AND f.status = 'ACTIVE' " +
           "AND date(f.departureTime) = date($departureDate) " +
           "RETURN f ORDER BY f.departureTime")
    List<FlightInstanceNode> findDirectFlights(@Param("source") String source, @Param("destination") String destination, @Param("departureDate") OffsetDateTime departureDate);



            @Query("MATCH (f1:FlightInstance)-[:ARRIVES_AT]->(a:Airport)-[:DEPARTS_WITH]->(f2:FlightInstance) " +
       "WHERE f1.source = $source AND f2.destination = $destination " +
       "AND f1.status = 'ACTIVE' AND f2.status = 'ACTIVE' " +
       "AND date(f1.departureTime) = date($departureDate) " +
       "AND f1.arrivalTime + duration('PT45M') <= f2.departureTime " +
       "AND f1.arrivalTime + duration('P1D') >= f2.departureTime " +
       "RETURN {flights: [f1, f2]} AS row")
List<Map<String,Object>> findConnectingFlightsWithOneStop(
        @Param("source") String source,
        @Param("destination") String destination,
        @Param("departureDate") OffsetDateTime departureDate);


}
