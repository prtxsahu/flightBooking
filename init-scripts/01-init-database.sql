-- PostgreSQL Database Initialization Script
-- This script runs during PostgreSQL container startup, BEFORE Spring Boot creates tables
-- We'll just ensure the database is ready and let Spring Boot handle table creation
-- Data seeding will happen via Spring Boot's @PostConstruct or data.sql approach

-- Create any extensions we might need
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Log that PostgreSQL is ready
DO $$
BEGIN
    RAISE NOTICE 'PostgreSQL database initialization completed!';
    RAISE NOTICE 'Database is ready for Spring Boot application to create tables and seed data';
END $$;