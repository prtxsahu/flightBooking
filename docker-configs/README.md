# Docker Configuration Directory

This directory contains Docker-related configuration files for the flight booking system.

## Files

- `docker-compose.yml` - Main Docker Compose configuration
- `Dockerfile` - Application container configuration
- `DOCKER-README.md` - Docker setup documentation

## Usage

To run the application with Docker:

```bash
cd docker-configs
docker-compose up --build
```

## Services

The Docker Compose setup includes:
- **app**: Spring Boot application
- **postgres**: PostgreSQL database
- **neo4j**: Neo4j graph database
- **redpanda**: Message broker
- **neo4j-loader**: Data seeding service

## Data Seeding

The system automatically seeds both PostgreSQL and Neo4j databases with comprehensive test data:
- 100 airports worldwide
- 8,820 flights for October 2025
- 441,000 seats
- Flight relationships in Neo4j graph
