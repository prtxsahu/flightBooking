# Flight Booking System - Docker Setup

This document explains how to run the complete Flight Booking System using Docker Compose.

## 🐳 What's Included

The Docker setup includes:
- **Spring Boot Application** - Flight booking and search service
- **PostgreSQL Database** - Transactional data (flights, seats, bookings)
- **Neo4j Graph Database** - Graph data (airports, flight relationships)
- **Redpanda** - Kafka-compatible message broker for events

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose installed
- At least 4GB of available RAM
- Ports 8080, 5432, 7474, 7687, 9092 available

### Run the Complete System

```bash
# Clone the repository (if not already done)
git clone https://github.com/prtxsahu/flightBooking.git
cd flightBooking

# Build and start all services
docker-compose up --build

# Or run in detached mode
docker-compose up --build -d
```

### Access the Services

- **Flight Booking API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **Neo4j Browser**: http://localhost:7474 (neo4j/neo4j_pass)
- **PostgreSQL**: localhost:5432 (demo/demo_pass/bookingdb)
- **Redpanda Console**: http://localhost:9644

## 📊 Comprehensive Dataset

The system comes pre-loaded with:
- **100 airports worldwide** (US + International: Europe, Asia, Middle East, Africa, Americas)
- **8,820 flights** for October 1-30, 2025
- **441,000 seats** (50 per flight)
- **Flight relationships** connecting airports and flights in Neo4j graph

## 🔧 Configuration

### Environment Variables

The application uses these environment variables (set in docker-compose.yml):

```yaml
SPRING_PROFILES_ACTIVE: docker
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/bookingdb
SPRING_DATASOURCE_USERNAME: demo
SPRING_DATASOURCE_PASSWORD: demo_pass
SPRING_NEO4J_URI: bolt://neo4j:7687
SPRING_NEO4J_AUTHENTICATION_USERNAME: neo4j
SPRING_NEO4J_AUTHENTICATION_PASSWORD: neo4j_pass
SPRING_KAFKA_BOOTSTRAP_SERVERS: redpanda:9092
```

### Database Initialization

- **PostgreSQL**: Automatically initializes with schema and comprehensive flight data (8,820 flights + 441,000 seats)
- **Neo4j**: Loads 100 airports, 8,820 flights, and relationships on startup

## 🧪 Testing the API

### Search for Flights
```bash
curl -X POST http://localhost:8080/api/search \
  -H "Content-Type: application/json" \
  -d '{
    "source": "JFK",
    "destination": "LAX",
    "departureDate": "2025-10-01",
    "passengerCount": 1,
    "maxStops": 2
  }'
```

### Check Available Flights
```bash
curl http://localhost:8080/api/search/direct/JFK/LAX/2025-10-01
```

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

## 🗂️ File Structure

```
flightBooking/
├── Dockerfile                          # Multi-stage build for Spring Boot app
├── docker-compose.yml                  # Complete system orchestration
├── init-scripts/
│   ├── 01-init-database.sql           # PostgreSQL initialization
│   └── neo4j/
│       ├── 01-init-neo4j.cypher       # Neo4j initialization
│       └── neo4j-init.sh              # Neo4j startup script
├── src/main/resources/
│   └── application-docker.yml         # Docker-specific configuration
└── DOCKER-README.md                   # This file
```

## 🔍 Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 8080, 5432, 7474, 7687, 9092 are available
2. **Memory issues**: Increase Docker memory allocation to at least 4GB
3. **Database connection errors**: Wait for databases to be healthy before app starts

### Check Service Status
```bash
# View logs
docker-compose logs -f

# Check specific service
docker-compose logs -f app
docker-compose logs -f postgres
docker-compose logs -f neo4j

# Check service health
docker-compose ps
```

### Reset Everything
```bash
# Stop and remove all containers, networks, and volumes
docker-compose down -v

# Remove all images
docker-compose down --rmi all

# Start fresh
docker-compose up --build
```

## 📈 Scaling

### Add More Data
To load the complete dataset (100 airports, 8,820 flights, 441,000 seats):

1. Copy the full seed files to `init-scripts/`
2. Update the initialization scripts
3. Rebuild and restart

### Production Considerations
- Use external databases for production
- Configure proper secrets management
- Set up monitoring and logging
- Use reverse proxy (nginx/traefik)
- Configure SSL/TLS certificates

## 🛠️ Development

### Rebuild Application Only
```bash
docker-compose build app
docker-compose up app
```

### Access Container Shells
```bash
# Spring Boot app
docker-compose exec app bash

# PostgreSQL
docker-compose exec postgres psql -U demo -d bookingdb

# Neo4j
docker-compose exec neo4j cypher-shell -u neo4j -p neo4j_pass
```

### View Database Data
```bash
# PostgreSQL
docker-compose exec postgres psql -U demo -d bookingdb -c "SELECT COUNT(*) FROM flight_instance;"

# Neo4j
docker-compose exec neo4j cypher-shell -u neo4j -p neo4j_pass "MATCH (a:Airport) RETURN count(a);"
```

## 📝 Notes

- The system uses health checks to ensure proper startup order
- All data is persisted in Docker volumes
- The application automatically retries database connections
- Neo4j includes APOC plugin for advanced operations

For more information, see the main README.md file.
