package com.flightbooking.search.repository;

import com.flightbooking.search.entity.FlightInstanceNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for FlightInstanceNode entity operations in Neo4j.
 * Provides essential queries for flight search and graph traversal.
 */
@Repository
public interface FlightInstanceNodeRepository extends Neo4jRepository<FlightInstanceNode, String> {

    /**
     * Find flight instance by ID.
     */
    Optional<FlightInstanceNode> findById(String id);

    /**
     * Find flights by source airport.
     */
    @Query("MATCH (f:FlightInstance) WHERE f.source = $source AND f.status = 'ACTIVE' RETURN f")
    List<FlightInstanceNode> findBySource(@Param("source") String source);

    /**
     * Find flights by destination airport.
     */
    @Query("MATCH (f:FlightInstance) WHERE f.destination = $destination AND f.status = 'ACTIVE' RETURN f")
    List<FlightInstanceNode> findByDestination(@Param("destination") String destination);

    /**
     * Find direct flights between two airports.
     */
    @Query("MATCH (f:FlightInstance) WHERE f.source = $source AND f.destination = $destination AND f.status = 'ACTIVE' RETURN f ORDER BY f.departureTime")
    List<FlightInstanceNode> findDirectFlights(@Param("source") String source, @Param("destination") String destination);

    /**
     * Find flights with available seats.
     */
    @Query("MATCH (f:FlightInstance) WHERE f.source = $source AND f.destination = $destination AND f.status = 'ACTIVE' " +
           "AND (f.remainingSeats + coalesce(f.heldSeats, 0)) >= $passengerCount " +
           "RETURN f ORDER BY f.priceMoney")
    List<FlightInstanceNode> findAvailableFlights(
            @Param("source") String source, 
            @Param("destination") String destination, 
            @Param("passengerCount") int passengerCount);

    /**
     * Find flights within time range.
     */
    @Query("MATCH (f:FlightInstance) WHERE f.source = $source AND f.destination = $destination AND f.status = 'ACTIVE' " +
           "AND f.departureTime >= $startTime AND f.departureTime <= $endTime " +
           "RETURN f ORDER BY f.departureTime")
    List<FlightInstanceNode> findFlightsInTimeRange(
            @Param("source") String source, 
            @Param("destination") String destination,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime);

    /**
     * Update seat counts for a flight instance.
     */
    @Query("MATCH (f:FlightInstance {id: $flightId}) " +
           "SET f.remainingSeats = $remainingSeats, f.heldSeats = $heldSeats, f.lastUpdated = $lastUpdated " +
           "RETURN f")
    FlightInstanceNode updateSeatCounts(
            @Param("flightId") String flightId,
            @Param("remainingSeats") Integer remainingSeats,
            @Param("heldSeats") Integer heldSeats,
            @Param("lastUpdated") OffsetDateTime lastUpdated);

    /**
     * Find all paths from source to destination using graph traversal with max depth.
     * Returns structured data for hybrid approach: flights list and path length.
     */
    @Query("MATCH path = (src:Airport {code: $source})-[:DEPARTS_WITH]->(f1:FlightInstance)-[:ARRIVES_AT]->(hub:Airport)-[:DEPARTS_WITH]->(f2:FlightInstance)-[:ARRIVES_AT]->(dst:Airport {code: $destination}) " +
           "WHERE f1.status = 'ACTIVE' AND f2.status = 'ACTIVE' " +
           "AND date(f1.departureTime) = date($departureDate) " +
           "AND f1.arrivalTime + duration('PT45M') <= f2.departureTime " +
           "AND f1.arrivalTime + duration('P1D') >= f2.departureTime " +
           "RETURN [f1, f2] as flights, 2 as pathLength " +
           "ORDER BY (f1.priceMoney + f2.priceMoney)")
    List<Object[]> findAllPathsWithValidation(
            @Param("source") String source,
            @Param("destination") String destination, 
            @Param("maxHops") int maxHops,
            @Param("departureDate") OffsetDateTime departureDate);


    /**
     * Find connecting flights with one stop.
     */
    @Query("MATCH (f1:FlightInstance)-[:ARRIVES_AT]->(a:Airport)-[:DEPARTS_WITH]->(f2:FlightInstance) " +
           "WHERE f1.source = $source AND f2.destination = $destination " +
           "AND f1.status = 'ACTIVE' AND f2.status = 'ACTIVE' " +
           "AND date(f1.departureTime) = date($departureDate) " +
           "AND f1.arrivalTime + duration('PT45M') <= f2.departureTime " +
           "AND f1.arrivalTime + duration('P1D') >= f2.departureTime " +
           "RETURN f1 " +
           "ORDER BY (f1.priceMoney + f2.priceMoney)")
    List<FlightInstanceNode> findConnectingFlightsFirstLeg(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("departureDate") OffsetDateTime departureDate);

    /**
     * Find connecting flights with one stop - second leg.
     */
    @Query("MATCH (f1:FlightInstance)-[:ARRIVES_AT]->(a:Airport)-[:DEPARTS_WITH]->(f2:FlightInstance) " +
           "WHERE f1.source = $source AND f2.destination = $destination " +
           "AND f1.status = 'ACTIVE' AND f2.status = 'ACTIVE' " +
           "AND date(f1.departureTime) = date($departureDate) " +
           "AND f1.arrivalTime + duration('PT45M') <= f2.departureTime " +
           "AND f1.arrivalTime + duration('P1D') >= f2.departureTime " +
           "RETURN f2 " +
           "ORDER BY (f1.priceMoney + f2.priceMoney)")
    List<FlightInstanceNode> findConnectingFlightsSecondLeg(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("departureDate") OffsetDateTime departureDate);

    /**
     * Find connecting flights with two stops (3 flights).
     */
    @Query("MATCH (f1:FlightInstance)-[:ARRIVES_AT]->(hub1:Airport)-[:DEPARTS_WITH]->(f2:FlightInstance)-[:ARRIVES_AT]->(hub2:Airport)-[:DEPARTS_WITH]->(f3:FlightInstance) " +
           "WHERE f1.source = $source AND f3.destination = $destination " +
           "AND f1.status = 'ACTIVE' AND f2.status = 'ACTIVE' AND f3.status = 'ACTIVE' " +
           "AND date(f1.departureTime) = date($departureDate) " +
           "AND f1.arrivalTime + duration('PT45M') <= f2.departureTime " +
           "AND f1.arrivalTime + duration('P1D') >= f2.departureTime " +
           "AND f2.arrivalTime + duration('PT45M') <= f3.departureTime " +
           "AND f2.arrivalTime + duration('P1D') >= f3.departureTime " +
           "RETURN f1, f2, f3 " +
           "ORDER BY (f1.priceMoney + f2.priceMoney + f3.priceMoney)")
    List<Object[]> findConnectingFlightsWithTwoStops(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("departureDate") OffsetDateTime departureDate);

    /**
     * Check if flight exists.
     */
    boolean existsById(String id);
}
