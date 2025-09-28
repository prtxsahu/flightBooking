package com.flightbooking.search.repository;

import com.flightbooking.search.entity.Airport;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Airport entity operations in Neo4j.
 * Provides essential queries for airport management and flight search.
 */
@Repository
public interface AirportRepository extends Neo4jRepository<Airport, String> {

    /**
     * Find airport by IATA code.
     */
    Optional<Airport> findByCode(String code);

    /**
     * Find airports by city.
     */
    List<Airport> findByCity(String city);

    /**
     * Find airports by country.
     */
    List<Airport> findByCountry(String country);

    /**
     * Find airports by city and country.
     */
    List<Airport> findByCityAndCountry(String city, String country);

    /**
     * Check if airport exists by code.
     */
    boolean existsByCode(String code);

    /**
     * Find airports with connections from a specific airport.
     */
    @Query("MATCH (a:Airport {code: $sourceCode})-[:DEPARTS_WITH]->(f:FlightInstance)-[:ARRIVES_AT]->(dest:Airport) " +
           "RETURN DISTINCT dest")
    List<Airport> findConnectedAirports(@Param("sourceCode") String sourceCode);

    /**
     * Find airports that can be reached from a source airport within specified stops.
     */
    @Query("MATCH (src:Airport {code: $sourceCode}) " +
           "CALL apoc.path.subgraphAll(src, {maxLevel: $maxStops, relationshipFilter: 'DEPARTS_WITH>', labelFilter: '+Airport'}) " +
           "YIELD nodes " +
           "RETURN nodes")
    List<Airport> findReachableAirports(@Param("sourceCode") String sourceCode, @Param("maxStops") int maxStops);
}
