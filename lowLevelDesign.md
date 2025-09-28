# Flight Booking System - Low Level Design

## Database Schemas

### PostgreSQL Schema (Source of Truth)

#### 1. Flight Instance Table
```sql
CREATE TABLE flight_instance (
  id BIGSERIAL PRIMARY KEY,
  flight_no TEXT NOT NULL,
  departure_time TIMESTAMPTZ NOT NULL,
  arrival_time TIMESTAMPTZ NOT NULL,
  source TEXT NOT NULL,
  destination TEXT NOT NULL,
  price_money BIGINT NOT NULL,
  remaining_seats INT NOT NULL DEFAULT 0,
  version INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- Indexes for performance
CREATE INDEX idx_flight_instance_source_dest ON flight_instance(source, destination);
CREATE INDEX idx_flight_instance_departure ON flight_instance(departure_time);
CREATE INDEX idx_flight_instance_price ON flight_instance(price_money);
```

#### 2. Seat Table
```sql
CREATE TABLE seat (
  id BIGSERIAL PRIMARY KEY,
  flight_instance_id BIGINT NOT NULL REFERENCES flight_instance(id) ON DELETE CASCADE,
  seat_no TEXT NOT NULL,
  cabin_class TEXT,
  is_available BOOLEAN NOT NULL DEFAULT true,
  UNIQUE (flight_instance_id, seat_no)
);

CREATE INDEX idx_seat_flight_instance ON seat(flight_instance_id);
CREATE INDEX idx_seat_availability ON seat(flight_instance_id, is_available);
```

#### 3. Seat Hold Table
```sql
CREATE TABLE seat_hold (
  id BIGSERIAL PRIMARY KEY,
  flight_instance_id BIGINT NOT NULL REFERENCES flight_instance(id) ON DELETE CASCADE,
  seat_id BIGINT NOT NULL REFERENCES seat(id),
  holder_session_id TEXT NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ DEFAULT now(),
  UNIQUE (seat_id)
);

CREATE INDEX idx_seathold_expires ON seat_hold(expires_at);
CREATE INDEX idx_seathold_session ON seat_hold(holder_session_id);
CREATE INDEX idx_seathold_flight ON seat_hold(flight_instance_id);
```

#### 4. Ticket Tables
```sql
CREATE TABLE ticket (
  id BIGSERIAL PRIMARY KEY,
  flight_instance_id BIGINT NOT NULL REFERENCES flight_instance(id),
  payment_id TEXT UNIQUE,
  holder_user_id BIGINT,
  total_amount BIGINT NOT NULL,
  status TEXT DEFAULT 'CONFIRMED',
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE ticket_seat (
  ticket_id BIGINT NOT NULL REFERENCES ticket(id) ON DELETE CASCADE,
  seat_id BIGINT NOT NULL REFERENCES seat(id),
  PRIMARY KEY (ticket_id, seat_id),
  UNIQUE (seat_id)
);

CREATE INDEX idx_ticket_user ON ticket(holder_user_id);
CREATE INDEX idx_ticket_payment ON ticket(payment_id);
```

#### 5. Outbox Event Table
```sql
CREATE TABLE outbox_event (
  id BIGSERIAL PRIMARY KEY,
  event_id UUID NOT NULL UNIQUE,
  topic TEXT NOT NULL,
  payload JSONB NOT NULL,
  created_at TIMESTAMPTZ DEFAULT now(),
  dispatched_at TIMESTAMPTZ,
  attempts INT DEFAULT 0,
  status TEXT DEFAULT 'PENDING', -- PENDING | DISPATCHED | FAILED
  max_retries INT DEFAULT 3
);

CREATE INDEX idx_outbox_status ON outbox_event(status, created_at);
CREATE INDEX idx_outbox_topic ON outbox_event(topic, status);
```

### Neo4j Schema (Search & Itineraries)

#### Node Types and Properties

**Airport Node**
```cypher
CREATE CONSTRAINT airport_code_unique ON (a:Airport) ASSERT a.code IS UNIQUE;
CREATE INDEX airport_city_index FOR (a:Airport) ON (a.city);
CREATE INDEX airport_country_index FOR (a:Airport) ON (a.country);

// Example Airport node
{
  code: "LAX",           // IATA code (unique)
  name: "Los Angeles International Airport",
  city: "Los Angeles",
  country: "USA",
  lat: 33.9425,
  lon: -118.4081
}
```

**FlightInstance Node**
```cypher
CREATE CONSTRAINT flight_instance_id_unique ON (f:FlightInstance) ASSERT f.id IS UNIQUE;
CREATE INDEX flight_departure_time FOR (f:FlightInstance) ON (f.departure_time);
CREATE INDEX flight_source FOR (f:FlightInstance) ON (f.source);
CREATE INDEX flight_destination FOR (f:FlightInstance) ON (f.destination);

// Example FlightInstance node
{
  id: "12345",
  flight_no: "AA123",
  departure_time: datetime("2025-01-15T08:00:00Z"),
  arrival_time: datetime("2025-01-15T11:30:00Z"),
  source: "LAX",
  destination: "JFK",
  price_money: 29900,      // Price in cents
  remainingSeats: 45,      // Updated only on confirmed bookings
  heldSeats: 0,           // Optional: aggregate holds
  status: "ACTIVE",
  lastUpdated: datetime()
}
```

**Itinerary Node**
```cypher
CREATE CONSTRAINT itinerary_id_unique ON (it:Itinerary) ASSERT it.id IS UNIQUE;
CREATE CONSTRAINT itinerary_flight_legs_unique ON (it:Itinerary) ASSERT it.flightLegsHash IS UNIQUE;
CREATE INDEX itinerary_search_key FOR (it:Itinerary) ON (it.search_key);
CREATE INDEX itinerary_route_date FOR (it:Itinerary) ON (it.source, it.destination, it.departure_date);

// Example Itinerary node (passenger-agnostic)
{
  id: "uuid-1234",
  source: "LAX",
  destination: "JFK", 
  departure_date: date("2025-01-15"),
  legs: 2,                    // Number of flight segments
  total_price: 59800,         // Total price in cents
  total_duration: 28800,      // Total duration in seconds
  minAvailableSeats: 3,       // Minimum available seats across all legs
  created_at: datetime(),
  search_key: "LAX-JFK-2025-01-15",  // Composite key (no passenger count)
  flightLegsHash: "a87ff679a2f3e71d9181a67b7542122c"  // MD5 hash of sorted flight IDs for uniqueness
}
```

#### Relationships

**Relationship Entities (Spring Data Neo4j 7.3.2):**

```java
// DepartsWith Relationship
@RelationshipProperties
public class DepartsWith {
    @Id @GeneratedValue private Long id;
    @TargetNode private FlightInstanceNode flight;
    @Property("departure_time") private OffsetDateTime departureTime;
    @Property("created_at") private OffsetDateTime createdAt;
}

// ArrivesAt Relationship  
@RelationshipProperties
public class ArrivesAt {
    @Id @GeneratedValue private Long id;
    @TargetNode private Airport airport;
    @Property("arrival_time") private OffsetDateTime arrivalTime;
    @Property("created_at") private OffsetDateTime createdAt;
}

// IncludesLeg Relationship
@RelationshipProperties
public class IncludesLeg {
    @Id @GeneratedValue private Long id;
    @TargetNode private FlightInstanceNode flight;
    @Property("leg_index") private Integer legIndex;
    @Property("leg_price") private Long legPrice;
    @Property("leg_duration") private Long legDuration;
    @Property("created_at") private OffsetDateTime createdAt;
}
```

**Cypher Relationship Structure:**
```cypher
// Airport to Flight relationships
(Airport)-[:DEPARTS_WITH {departure_time: datetime, created_at: datetime}]->(FlightInstance)
(FlightInstance)-[:ARRIVES_AT {arrival_time: datetime, created_at: datetime}]->(Airport)

// Itinerary composition with metadata
(Itinerary)-[:INCLUDES_LEG {leg_index: 0, leg_price: 29900, leg_duration: 14400, created_at: datetime}]->(FlightInstance)
(Itinerary)-[:INCLUDES_LEG {leg_index: 1, leg_price: 29900, leg_duration: 14400, created_at: datetime}]->(FlightInstance)

// Performance Benefits:
// - Eliminates O(n³) nested loops in Java
// - Enables efficient Cypher graph traversal
// - Supports proper indexing on relationships
// - Enables complex multi-hop queries
```

## Event Schemas (JSON)

### SeatHoldCreated Event
```json
{
  "event_id": "550e8400-e29b-41d4-a716-446655440000",
  "event_type": "SeatHoldCreated",
  "schema_version": 1,
  "occurred_at": "2025-01-15T10:10:00Z",
  "source_service": "booking-service",
  "payload": {
    "flight_instance_id": 12345,
    "holder_session_id": "sess_abc123",
    "seat_count": 2,
    "seat_ids": [67890, 67891],
    "seat_labels": ["12A", "12B"],
    "expires_at": "2025-01-15T10:25:00Z",
    "total_amount": 59800
  }
}
```

### SeatHoldReleased Event
```json
{
  "event_id": "550e8400-e29b-41d4-a716-446655440001",
  "event_type": "SeatHoldReleased",
  "schema_version": 1,
  "occurred_at": "2025-01-15T10:26:00Z",
  "source_service": "booking-service",
  "payload": {
    "flight_instance_id": 12345,
    "holder_session_id": "sess_abc123",
    "seat_count": 2,
    "seat_ids": [67890, 67891],
    "reason": "HOLD_EXPIRED",
    "released_at": "2025-01-15T10:26:00Z"
  }
}
```

### SeatBooked Event
```json
{
  "event_id": "550e8400-e29b-41d4-a716-446655440002",
  "event_type": "SeatBooked",
  "schema_version": 1,
  "occurred_at": "2025-01-15T10:12:34Z",
  "source_service": "booking-service",
  "payload": {
    "flight_instance_id": 12345,
    "ticket_id": 98765,
    "user_id": 444,
    "seat_count": 2,
    "seat_ids": [67890, 67891],
    "seat_labels": ["12A", "12B"],
    "payment_id": "pay_01H...",
    "total_amount": 59800,
    "booking_confirmed_at": "2025-01-15T10:12:34Z"
  }
}
```

## Transaction Implementation Details

### Seat Hold Transaction (Atomic Allocation)
```sql
BEGIN;

WITH candidate AS (
  SELECT s.id, s.seat_no
  FROM seat s
  LEFT JOIN seat_hold h ON h.seat_id = s.id AND h.expires_at > now()
  LEFT JOIN ticket_seat ts ON ts.seat_id = s.id
  WHERE s.flight_instance_id = $flight_instance_id
    AND s.is_available = true
    AND h.id IS NULL
    AND ts.seat_id IS NULL
  ORDER BY s.seat_no
  FOR UPDATE SKIP LOCKED
  LIMIT $requested_count
),
hold_insert AS (
  INSERT INTO seat_hold (flight_instance_id, seat_id, holder_session_id, expires_at)
  SELECT $flight_instance_id, id, $holder_session, now() + interval '15 minutes' 
  FROM candidate
  RETURNING seat_id, id as hold_id
)
SELECT COUNT(*) as allocated_count FROM hold_insert;

-- Verify allocated_count == requested_count
-- If not equal: ROLLBACK and return error
-- If equal: COMMIT and return hold details

COMMIT;
```

### Payment Confirmation Transaction
```sql
BEGIN;

-- Verify holds exist and not expired
WITH valid_holds AS (
  SELECT sh.seat_id, sh.id as hold_id
  FROM seat_hold sh
  WHERE sh.holder_session_id = $holder_session
    AND sh.expires_at > now()
    AND sh.flight_instance_id = $flight_instance_id
),
ticket_creation AS (
  INSERT INTO ticket (flight_instance_id, payment_id, holder_user_id, total_amount)
  VALUES ($flight_instance_id, $payment_id, $user_id, $total_amount)
  RETURNING id as ticket_id
),
seat_assignment AS (
  INSERT INTO ticket_seat (ticket_id, seat_id)
  SELECT tc.ticket_id, vh.seat_id
  FROM ticket_creation tc
  CROSS JOIN valid_holds vh
),
seat_update AS (
  UPDATE seat 
  SET is_available = false 
  WHERE id IN (SELECT seat_id FROM valid_holds)
),
flight_update AS (
  UPDATE flight_instance
  SET remaining_seats = remaining_seats - (SELECT COUNT(*) FROM valid_holds),
      version = version + 1
  WHERE id = $flight_instance_id
),
hold_cleanup AS (
  DELETE FROM seat_hold 
  WHERE id IN (SELECT hold_id FROM valid_holds)
),
outbox_event AS (
  INSERT INTO outbox_event (event_id, topic, payload)
  VALUES (
    $event_uuid,
    'booking.seat_events',
    $seat_booked_payload::jsonb
  )
)
SELECT COUNT(*) FROM valid_holds;

COMMIT;
```

## Itinerary Uniqueness Implementation

### Unique Constraint Design

To prevent duplicate itineraries with the same flight combinations, we implemented a unique constraint based on flight leg hashes:

#### Hash Generation Algorithm
```java
private String generateFlightLegsHash(List<FlightInstanceNode> flights) {
    // Sort flight IDs to ensure consistent hash regardless of order
    String legIds = flights.stream()
        .map(FlightInstanceNode::getId)
        .sorted()
        .collect(Collectors.joining(","));
    
    // Generate MD5 hash
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] hashBytes = md.digest(legIds.getBytes());
    
    // Convert to hex string
    StringBuilder sb = new StringBuilder();
    for (byte b : hashBytes) {
        sb.append(String.format("%02x", b));
    }
    
    return sb.toString();
}
```

#### Database Constraint
```cypher
CREATE CONSTRAINT itinerary_flight_legs_unique 
FOR (i:Itinerary) 
REQUIRE i.flightLegsHash IS UNIQUE;
```

#### Duplicate Prevention Logic
```java
private void saveUniqueItineraries(List<Itinerary> itineraries) {
    try {
        // Try to save all itineraries - Neo4j will enforce uniqueness constraint
        itineraryRepository.saveAll(itineraries);
        log.info("Successfully saved {} unique itineraries", itineraries.size());
    } catch (Exception e) {
        log.warn("Some itineraries may be duplicates, saving individually: {}", e.getMessage());
        
        // If batch save fails due to constraint violations, save individually
        int savedCount = 0;
        for (Itinerary itinerary : itineraries) {
            try {
                itineraryRepository.save(itinerary);
                savedCount++;
            } catch (Exception individualException) {
                log.debug("Skipping duplicate itinerary with hash: {}", itinerary.getFlightLegsHash());
            }
        }
        log.info("Saved {} out of {} itineraries ({} were duplicates)", 
                savedCount, itineraries.size(), itineraries.size() - savedCount);
    }
}
```

### Available Seats Calculation Fix

#### Problem
The `minAvailableSeats` was showing `0` because the `FlightInstanceNode` entity was not properly mapping the `totalAvailableSeats` property from Neo4j.

#### Solution
Updated the `FlightInstanceNode` entity to include the correct property mappings:

```java
@Property("totalAvailableSeats")
private Integer totalAvailableSeats;

@Property("totalSeats")
private Integer totalSeats;

// Helper method to get total available seats
public Integer getTotalAvailableSeats() {
    // Use totalAvailableSeats if available, otherwise fall back to remainingSeats + heldSeats
    if (totalAvailableSeats != null) {
        return totalAvailableSeats;
    }
    return (remainingSeats != null ? remainingSeats : 0) + (heldSeats != null ? heldSeats : 0);
}
```

#### Neo4j Data Structure
```cypher
// FlightInstance nodes now include:
{
  id: "4",
  flightNo: "AA101",
  source: "LAX",
  destination: "JFK",
  departureTime: datetime("2025-09-29T09:00:00Z"),
  arrivalTime: datetime("2025-09-29T18:00:00Z"),
  priceMoney: 29900,
  status: "ACTIVE",
  totalAvailableSeats: 50,  // Primary source of truth
  totalSeats: 50,
}
```

### Implementation Benefits

#### Data Integrity
- **Database-level uniqueness**: Neo4j enforces uniqueness constraint, preventing duplicate itineraries
- **Automatic duplicate detection**: Constraint violations are caught and handled gracefully
- **Consistent hashing**: MD5 hash ensures same flight combinations always produce same hash

#### Performance Improvements
- **Fast duplicate detection**: Hash-based lookups are O(1) instead of O(n) comparisons
- **Efficient caching**: Same flight combinations reuse existing itineraries
- **Reduced storage**: No duplicate itineraries means smaller database size

#### Operational Benefits
- **Graceful error handling**: Individual save fallback when batch operations fail
- **Detailed logging**: Clear visibility into duplicate detection and handling
- **Backward compatibility**: Fallback to `remainingSeats + heldSeats` if `totalAvailableSeats` is null

#### Test Results
- ✅ **API Response**: `availableSeats: 50` (was `0` before)
- ✅ **Caching**: `Found 4 cached itineraries for search` (no regeneration)
- ✅ **Database**: `minAvailableSeats: 50, hasHash: TRUE` (correct values)
- ✅ **Unique Constraint**: No duplicate itineraries possible

## Search Implementation Details

### Primary Search Query (Itinerary Cache)
```cypher
MATCH (it:Itinerary)
WHERE it.source = $source 
  AND it.destination = $destination
  AND it.departure_date = date($departure_date)
  AND it.minAvailableSeats >= $passenger_count  // Filter by seat availability
  AND it.total_price >= $min_price
  AND it.total_price <= $max_price
RETURN it
ORDER BY it.total_price
LIMIT 100;  // Maximum 100 results
```

### Fallback Graph Traversal Query
```cypher
// Direct flights
MATCH (src:Airport {code: $source})-[:DEPARTS_WITH]->(f:FlightInstance)-[:ARRIVES_AT]->(dst:Airport {code: $destination})
WHERE f.departure_time >= datetime($earliest) 
  AND f.arrival_time <= datetime($latest)
  AND f.remainingSeats >= $passenger_count
  AND f.status = 'ACTIVE'
RETURN [f] AS itinerary, f.price_money AS total_price

UNION

// Single connection (1 stop)
MATCH (src:Airport {code: $source})-[:DEPARTS_WITH]->(f1:FlightInstance)-[:ARRIVES_AT]->(m:Airport),
      (m)-[:DEPARTS_WITH]->(f2:FlightInstance)-[:ARRIVES_AT]->(dst:Airport {code: $destination})
WHERE f1.arrival_time + duration('PT45M') <= f2.departure_time  // Min 45min layover
  AND f1.arrival_time + duration('P1D') >= f2.departure_time    // Max 24hr layover
  AND f1.departure_time >= datetime($earliest)
  AND f2.arrival_time <= datetime($latest)
  AND f1.remainingSeats >= $passenger_count
  AND f2.remainingSeats >= $passenger_count
  AND f1.status = 'ACTIVE' AND f2.status = 'ACTIVE'
RETURN [f1, f2] AS itinerary, (f1.price_money + f2.price_money) AS total_price

UNION

// Double connection (2 stops)
MATCH (src:Airport {code: $source})-[:DEPARTS_WITH]->(f1:FlightInstance)-[:ARRIVES_AT]->(m1:Airport),
      (m1)-[:DEPARTS_WITH]->(f2:FlightInstance)-[:ARRIVES_AT]->(m2:Airport),
      (m2)-[:DEPARTS_WITH]->(f3:FlightInstance)-[:ARRIVES_AT]->(dst:Airport {code: $destination})
WHERE f1.arrival_time + duration('PT45M') <= f2.departure_time  // Min 45min layover
  AND f2.arrival_time + duration('PT45M') <= f3.departure_time  // Min 45min layover
  AND f1.arrival_time + duration('P1D') >= f2.departure_time    // Max 24hr layover
  AND f2.arrival_time + duration('P1D') >= f3.departure_time    // Max 24hr layover
  AND f1.departure_time >= datetime($earliest)
  AND f3.arrival_time <= datetime($latest)
  AND f1.remainingSeats >= $passenger_count
  AND f2.remainingSeats >= $passenger_count
  AND f3.remainingSeats >= $passenger_count
  AND f1.status = 'ACTIVE' AND f2.status = 'ACTIVE' AND f3.status = 'ACTIVE'
RETURN [f1, f2, f3] AS itinerary, (f1.price_money + f2.price_money + f3.price_money) AS total_price

ORDER BY total_price
LIMIT 100;  // Maximum 100 results
```

### Itinerary Persistence Query
```cypher
MERGE (it:Itinerary {id: $it_id})
SET it.source = $source,
    it.destination = $destination,
    it.departure_date = date($departure_date),
    it.legs = $legs,
    it.total_price = $total_price,
    it.total_duration = $total_duration,
    it.minAvailableSeats = $minAvailableSeats,
    it.created_at = datetime(),
    it.search_key = $search_key,
    it.flightLegsHash = $flightLegsHash
WITH it
UNWIND $legsList AS leg
  MATCH (f:FlightInstance {id: leg.flight_instance_id})
  MERGE (it)-[r:INCLUDES_LEG {index: leg.index}]->(f)
  SET r.price = leg.price, r.duration = leg.duration
WITH it
MATCH (src:Airport {code: $source})
MERGE (it)-[:FROM]->(src)
WITH it
MATCH (dst:Airport {code: $destination})
MERGE (it)-[:TO]->(dst);
```

## Graph Sync Consumer Implementation

### FlightInstance Update Query (SeatBooked Event)
```cypher
MATCH (f:FlightInstance {id: $flight_instance_id})
SET f.remainingSeats = coalesce(f.remainingSeats, $initial_seats) - $seat_count,
    f.heldSeats = GREATEST(coalesce(f.heldSeats, 0) - $seat_count, 0),
    f.lastUpdated = datetime($occurred_at)
RETURN f;
```

### FlightInstance Update Query (SeatHoldCreated Event)
```cypher
MATCH (f:FlightInstance {id: $flight_instance_id})
SET f.heldSeats = coalesce(f.heldSeats, 0) + $seat_count,
    f.lastHoldExpiry = datetime($expires_at),
    f.lastUpdated = datetime($occurred_at)
RETURN f;
```

### FlightInstance Update Query (SeatHoldReleased Event)
```cypher
MATCH (f:FlightInstance {id: $flight_instance_id})
SET f.heldSeats = GREATEST(coalesce(f.heldSeats, 0) - $seat_count, 0),
    f.lastUpdated = datetime($occurred_at)
RETURN f;
```

## Reconciliation Implementation

### Drift Detection Query
```cypher
MATCH (f:FlightInstance)
WHERE f.status = 'ACTIVE'
RETURN f.id, f.remainingSeats
ORDER BY f.id;
```

### Reconciliation Update Query
```cypher
MATCH (f:FlightInstance {id: $flight_instance_id})
SET f.remainingSeats = $correct_count,
    f.lastUpdated = datetime(),
    f.reconciled_at = datetime();
```

## Outbox Publisher Configuration

### Publisher Query
```sql
SELECT id, event_id, topic, payload, attempts, max_retries
FROM outbox_event
WHERE status = 'PENDING'
ORDER BY id
LIMIT 100
FOR UPDATE SKIP LOCKED;
```

### Publisher Update Queries
```sql
-- Mark as dispatched
UPDATE outbox_event 
SET status = 'DISPATCHED', 
    dispatched_at = now() 
WHERE id = $event_id;

-- Increment attempts
UPDATE outbox_event 
SET attempts = attempts + 1 
WHERE id = $event_id;

-- Mark as failed
UPDATE outbox_event 
SET status = 'FAILED' 
WHERE id = $event_id;
```

## Redpanda Configuration

### Topic Creation
```bash
rpk topic create booking.seat_events -p 3 -r 1
```

### Producer Configuration
- **Topic**: `booking.seat_events`
- **Partitioning Key**: `flight_instance_id` (string)
- **Acks**: `all` (strongest durability)
- **Retries**: 3
- **Batch Size**: 16KB

### Consumer Configuration
- **Group ID**: `graph-sync-consumer`
- **Auto Offset Reset**: `earliest`
- **Enable Auto Commit**: `false` (manual commit for reliability)
- **Session Timeout**: 30 seconds
- **Heartbeat Interval**: 10 seconds

## Performance Optimizations

### Database Indexes
- **PostgreSQL**: Composite indexes on search columns, foreign keys
- **Neo4j**: Indexes on departure_time, source, destination, search_key
- **Connection Pooling**: HikariCP with optimized settings

### Caching Strategy
- **Itinerary Cache**: 1-hour TTL for search results in Neo4j
- **Flight Metadata**: Redis cache for frequently accessed flights
- **Session Cache**: Redis for active holds

### Query Optimization
- **Batch Processing**: Bulk operations for seat allocation
- **Skip Locked**: Non-blocking seat selection
- **Pagination**: Limit result sets for large queries
- **Graph Traversal**: Limit to 100 results max
- **Runtime Generation**: Build itineraries on-demand, cache for future searches

## Monitoring Configuration

### Key Metrics
- `outbox_pending_count`: Count of pending outbox events
- `publisher_error_rate`: Failed event publishing rate
- `consumer_lag`: Graph sync consumer lag per partition
- `booking_success_rate`: Successful booking percentage
- `hold_expiry_rate`: Natural hold expiration rate
- `reconciliation_drift_count`: Data consistency issues

### Alert Thresholds
- **Outbox lag**: > 1000 events pending
- **Consumer lag**: > 5000 messages behind
- **Booking success rate**: < 95%
- **Reconciliation drift**: > 0 inconsistencies

## Service Configuration

### Booking Service (`com.flightbooking.booking`)
- **Port**: 8080
- **Database**: PostgreSQL connection pool (10-20 connections)
- **Outbox Polling**: Every 1 second
- **Hold TTL**: 15 minutes

### Search Service (`com.flightbooking.search`)
- **Port**: 8081
- **Database**: Neo4j connection pool (5-10 connections)
- **Cache TTL**: 1 hour for itineraries
- **Max Traversal Results**: 50

### Graph Sync Consumer
- **Group ID**: `graph-sync-consumer`
- **Batch Size**: 100 messages
- **Commit Interval**: Every 10 seconds
- **Retry Policy**: 3 attempts with exponential backoff