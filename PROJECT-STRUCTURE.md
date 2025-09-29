# Flight Booking System - Project Structure

This document describes the organized structure of the flight booking system project.

## 📁 Directory Organization

### Core Application
- **`src/`** - Main Spring Boot application source code
- **`pom.xml`** - Maven configuration
- **`mvnw`, `mvnw.cmd`** - Maven wrapper scripts

### Configuration & Documentation
- **`init-scripts/`** - Database initialization scripts used by Docker
- **`logs/`** - Application log files
- **`target/`** - Maven build output (generated)

### Documentation
- **`HELP.md`** - Spring Boot getting started guide
- **`highLevelDesign.md`** - High-level system design
- **`lowLevelDesign.md`** - Low-level system design
- **`plan.md`** - Project planning document

## 🗂️ Organized Directories

### `data-seeds/`
Contains all data seeding files for both PostgreSQL and Neo4j databases.

- **`cypher/`** - Neo4j Cypher scripts
  - Airport data, flight data, relationships
  - Index setup and synchronization scripts
- **`sql/`** - PostgreSQL SQL scripts  
  - Flight data, seat data, comprehensive datasets
- **`scripts/`** - Utility scripts for data operations

### `scripts/`
Utility scripts for development and testing.

- **`data-generation/`** - Scripts for generating test data
  - `generate-flights.py` - Flight data generator
  - `airports.txt` - Airport reference data
- **`load-testing/`** - Performance testing scripts
  - `load_test_search.py` - API load testing
  - `README_load_test.md` - Load testing documentation

### `docker-configs/`
Docker configuration and deployment files.

- **`docker-compose.yml`** - Multi-service Docker setup
- **`Dockerfile`** - Application container configuration
- **`DOCKER-README.md`** - Docker deployment guide

## 🚀 Quick Start

### Local Development
```bash
# Run with Maven
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
```bash
cd docker-configs
docker-compose up --build
```

### Data Generation
```bash
cd scripts/data-generation
python generate-flights.py
```

### Load Testing
```bash
cd scripts/load-testing
python load_test_search.py
```

## 📊 System Architecture

The system uses a hybrid approach:
- **PostgreSQL** - Relational data (bookings, tickets, seats)
- **Neo4j** - Graph data (flight routes, itineraries)
- **Spring Boot** - REST API and business logic
- **Docker** - Containerized deployment

## 🔧 Key Features

- Flight search with direct and connecting flights
- Real-time seat availability
- Booking and payment processing
- Comprehensive test data (100 airports, 8,820 flights)
- Load testing capabilities
- Docker containerization
