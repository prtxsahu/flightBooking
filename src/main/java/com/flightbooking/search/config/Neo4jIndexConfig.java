package com.flightbooking.search.config;

import org.springframework.context.annotation.Configuration;

/**
 * Neo4j configuration for indexes and constraints.
 * This ensures proper indexing for search performance.
 */
@Configuration
public class Neo4jIndexConfig {

    /**
     * UPDATED INDEXING STRATEGY based on actual search implementation analysis.
     * These indexes are critical for search performance and must be created.
     * 
     * EXECUTION: Run these commands during application startup or via database migration scripts
     */
    
    /*
    // ============================================================================
    // ITINERARY INDEXES (MOST CRITICAL - Used 90% of the time)
    // ============================================================================
    
    -- Primary search path (cached itineraries)
    CREATE CONSTRAINT itinerary_id_unique ON (it:Itinerary) ASSERT it.id IS UNIQUE;
    CREATE INDEX itinerary_search_key ON (it:Itinerary) FOR (it.search_key);
    CREATE INDEX itinerary_min_seats ON (it:Itinerary) FOR (it.minAvailableSeats);
    
    -- COMPOSITE INDEX for the main search query (CRITICAL)
    CREATE INDEX itinerary_search_seats ON (it:Itinerary) FOR (it.search_key, it.minAvailableSeats);
    
    -- Price sorting optimization
    CREATE INDEX itinerary_price ON (it:Itinerary) FOR (it.total_price);
    
    // ============================================================================
    // FLIGHTINSTANCE INDEXES (For runtime generation - Fallback 10%)
    // ============================================================================
    
    -- Direct flight searches (0 stops)
    CREATE CONSTRAINT flight_instance_id_unique ON (f:FlightInstance) ASSERT f.id IS UNIQUE;
    CREATE INDEX flight_source_destination ON (f:FlightInstance) FOR (f.source, f.destination);
    
    -- Individual airport searches (1-stop connections)
    CREATE INDEX flight_source ON (f:FlightInstance) FOR (f.source);
    CREATE INDEX flight_destination ON (f:FlightInstance) FOR (f.destination);
    
    -- Status filtering (ACTIVE flights only)
    CREATE INDEX flight_status ON (f:FlightInstance) FOR (f.status);
    
    -- COMPOSITE INDEXES for optimized queries
    CREATE INDEX flight_source_status ON (f:FlightInstance) FOR (f.source, f.status);
    CREATE INDEX flight_destination_status ON (f:FlightInstance) FOR (f.destination, f.status);
    CREATE INDEX flight_source_dest_status ON (f:FlightInstance) FOR (f.source, f.destination, f.status);
    
    -- Time-based searches (for layover validation)
    CREATE INDEX flight_departure_time ON (f:FlightInstance) FOR (f.departure_time);
    CREATE INDEX flight_arrival_time ON (f:FlightInstance) FOR (f.arrival_time);
    
    // ============================================================================
    // AIRPORT INDEXES (For reference data)
    // ============================================================================
    
    CREATE CONSTRAINT airport_code_unique ON (a:Airport) ASSERT a.code IS UNIQUE;
    CREATE INDEX airport_city ON (a:Airport) FOR (a.city);
    CREATE INDEX airport_country ON (a:Airport) FOR (a.country);
    
    // ============================================================================
    // PERFORMANCE NOTES:
    // ============================================================================
    // 1. itinerary_search_seats is the MOST CRITICAL index - used in primary search path
    // 2. flight_source_dest_status optimizes direct flight searches
    // 3. Two-stop generation (findAll()) is a performance bottleneck - needs optimization
    // 4. Consider adding relationship indexes if we implement proper graph traversal
    */
}
