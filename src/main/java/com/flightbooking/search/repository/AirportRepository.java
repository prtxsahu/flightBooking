package com.flightbooking.search.repository;

import com.flightbooking.search.entity.Airport;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Airport entity operations in Neo4j.
 * Currently only provides basic CRUD operations through Neo4jRepository.
 */
@Repository
public interface AirportRepository extends Neo4jRepository<Airport, String> {
    // Basic CRUD operations are inherited from Neo4jRepository
    // Additional custom queries can be added here as needed
}