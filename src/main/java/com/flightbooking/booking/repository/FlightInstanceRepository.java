package com.flightbooking.booking.repository;

import com.flightbooking.booking.entity.FlightInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for FlightInstance entity operations.
 * Provides CRUD operations and custom queries for flight management.
 */
@Repository
public interface FlightInstanceRepository extends JpaRepository<FlightInstance, Long> {

    /**
     * Find flight instances by source and destination airports.
     */
    List<FlightInstance> findBySourceAndDestination(String source, String destination);

    /**
     * Find flight instances by source, destination, and departure date range.
     */
    @Query("SELECT f FROM FlightInstance f WHERE f.source = :source AND f.destination = :destination " +
           "AND f.departureTime >= :startDate AND f.departureTime < :endDate ORDER BY f.departureTime")
    List<FlightInstance> findByRouteAndDateRange(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    /**
     * Find flight instances by flight number.
     */
    List<FlightInstance> findByFlightNo(String flightNo);

    /**
     * Find flight instances departing from a specific airport.
     */
    List<FlightInstance> findBySource(String source);

    /**
     * Find flight instances arriving at a specific airport.
     */
    List<FlightInstance> findByDestination(String destination);

    /**
     * Find flight instances by departure time range.
     */
    @Query("SELECT f FROM FlightInstance f WHERE f.departureTime >= :startTime AND f.departureTime <= :endTime ORDER BY f.departureTime")
    List<FlightInstance> findByDepartureTimeRange(
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime);

    /**
     * Find flight instances by price range.
     */
    @Query("SELECT f FROM FlightInstance f WHERE f.priceMoney >= :minPrice AND f.priceMoney <= :maxPrice ORDER BY f.priceMoney")
    List<FlightInstance> findByPriceRange(
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice);

    /**
     * Check if a flight instance exists by flight number and departure time.
     */
    boolean existsByFlightNoAndDepartureTime(String flightNo, OffsetDateTime departureTime);

    /**
     * Find flight instance by flight number and departure time.
     */
    Optional<FlightInstance> findByFlightNoAndDepartureTime(String flightNo, OffsetDateTime departureTime);

    /**
     * Count flight instances by route.
     */
    long countBySourceAndDestination(String source, String destination);

    /**
     * Find flight instances with custom search criteria.
     */
    @Query("SELECT f FROM FlightInstance f WHERE " +
           "(:source IS NULL OR f.source = :source) AND " +
           "(:destination IS NULL OR f.destination = :destination) AND " +
           "(:minPrice IS NULL OR f.priceMoney >= :minPrice) AND " +
           "(:maxPrice IS NULL OR f.priceMoney <= :maxPrice) AND " +
           "(:startDate IS NULL OR f.departureTime >= :startDate) AND " +
           "(:endDate IS NULL OR f.departureTime <= :endDate) " +
           "ORDER BY f.departureTime")
    List<FlightInstance> findWithCustomCriteria(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);
}
