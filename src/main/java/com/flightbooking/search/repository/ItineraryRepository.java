package com.flightbooking.search.repository;

import com.flightbooking.search.entity.Itinerary;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Itinerary entity operations in Neo4j.
 * Provides essential queries for cached itinerary management and search.
 */
@Repository
public interface ItineraryRepository extends Neo4jRepository<Itinerary, String> {

    /**
     * Find itineraries by search key with seat availability filter.
     */
    @Query("MATCH (it:Itinerary) WHERE it.search_key = $searchKey AND it.minAvailableSeats >= $passengerCount " +
           "RETURN it ORDER BY it.total_price LIMIT 100")
    List<Itinerary> findBySearchKeyWithSeatFilter(@Param("searchKey") String searchKey, @Param("passengerCount") Integer passengerCount);
}