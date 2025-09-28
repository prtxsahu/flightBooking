package com.flightbooking.search.repository;

import com.flightbooking.search.entity.Itinerary;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Itinerary entity operations in Neo4j.
 * Provides essential queries for cached itinerary management and search.
 */
@Repository
public interface ItineraryRepository extends Neo4jRepository<Itinerary, String> {

    /**
     * Find itinerary by ID.
     */
    Optional<Itinerary> findById(String id);

    /**
     * Find cached itineraries for search criteria.
     * Filters by minAvailableSeats >= passengerCount at search time.
     */
    @Query("MATCH (it:Itinerary) WHERE it.source = $source AND it.destination = $destination " +
           "AND it.departure_date = $departureDate AND it.minAvailableSeats >= $passengerCount " +
           "AND it.total_price >= $minPrice AND it.total_price <= $maxPrice " +
           "RETURN it ORDER BY it.total_price LIMIT 100")
    List<Itinerary> findCachedItineraries(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("departureDate") LocalDate departureDate,
            @Param("passengerCount") Integer passengerCount,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice);

    /**
     * Find itineraries by search key (passenger-agnostic).
     */
    @Query("MATCH (it:Itinerary) WHERE it.search_key = $searchKey RETURN it ORDER BY it.total_price")
    List<Itinerary> findBySearchKey(@Param("searchKey") String searchKey);

    /**
     * Find itineraries by search key with seat availability filter.
     */
    @Query("MATCH (it:Itinerary) WHERE it.search_key = $searchKey AND it.minAvailableSeats >= $passengerCount " +
           "RETURN it ORDER BY it.total_price LIMIT 100")
    List<Itinerary> findBySearchKeyWithSeatFilter(@Param("searchKey") String searchKey, @Param("passengerCount") Integer passengerCount);

    /**
     * Find itineraries by route and date.
     */
    @Query("MATCH (it:Itinerary) WHERE it.source = $source AND it.destination = $destination " +
           "AND it.departure_date = $departureDate RETURN it ORDER BY it.total_price")
    List<Itinerary> findByRouteAndDate(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("departureDate") LocalDate departureDate);

    /**
     * Check if itinerary exists by search key.
     */
    @Query("RETURN EXISTS((it:Itinerary {search_key: $searchKey}))")
    boolean existsBySearchKey(@Param("searchKey") String searchKey);

    /**
     * Find itineraries with seat availability.
     * Simplified to use minAvailableSeats field for filtering.
     */
    @Query("MATCH (it:Itinerary) WHERE it.source = $source AND it.destination = $destination " +
           "AND it.departure_date = $departureDate AND it.minAvailableSeats >= $passengerCount " +
           "RETURN it ORDER BY it.total_price LIMIT 100")
    List<Itinerary> findAvailableItineraries(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("departureDate") LocalDate departureDate,
            @Param("passengerCount") Integer passengerCount);

    /**
     * Delete expired itineraries (older than specified days).
     */
    @Query("MATCH (it:Itinerary) WHERE it.created_at < datetime() - duration('P$daysD') DETACH DELETE it")
    void deleteExpiredItineraries(@Param("days") int days);

    /**
     * Count itineraries by route.
     */
    @Query("MATCH (it:Itinerary) WHERE it.source = $source AND it.destination = $destination RETURN COUNT(it)")
    long countByRoute(@Param("source") String source, @Param("destination") String destination);
}
