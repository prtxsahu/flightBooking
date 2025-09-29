# Data Seeds Directory

This directory contains all the data seeding files for the flight booking system.

## Structure

- **cypher/**: Neo4j Cypher scripts for seeding graph database
- **sql/**: PostgreSQL SQL scripts for seeding relational database  
- **scripts/**: Utility scripts for data seeding operations

## Files

### Cypher Files (Neo4j)
- `seed-100-airports.cypher` - 100 airports worldwide
- `seed-10000-flights-neo4j.cypher` - 10,000 flights for Neo4j
- `generated-flights-neo4j.cypher` - Generated flight data
- `generated-flight-relationships.cypher` - Flight relationships
- `create-flight-relationships-*.cypher` - Relationship creation scripts
- `sync-flights-to-neo4j-*.cypher` - Data synchronization scripts
- `setup-neo4j-indexes-v5.cypher` - Neo4j index setup

### SQL Files (PostgreSQL)
- `seed-10000-flights-sql.sql` - 10,000 flights for PostgreSQL
- `seed-500000-seats-sql.sql` - 500,000 seats
- `generated-flights-sql.sql` - Generated flight data
- `generated-seats-sql.sql` - Generated seat data
- `seed-data-flights.sql` - Main flight data seeding
- `seed-flights-2025.sql` - 2025 flight data

## Usage

These files are used during Docker container initialization to populate the databases with sample data.
