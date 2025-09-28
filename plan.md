# Flight Booking System - Implementation Plan

## API Requirements

### Customer APIs
1. **GET /search** - Search for flights
   - Parameters: `date`, `start`, `destination`, `no_of_bookings`
   - Response: List of available flights/itineraries

2. **POST /book** - Create seat hold
   - Parameters: `flight_id`, `no_of_bookings`
   - Response: `{status: "ok/failed", hold_id: "uuid", expires_at: "timestamp"}`

3. **POST /payment** - Confirm booking
   - Parameters: `payment_id`, `success/fail`
   - Response: `{seat_numbers: ["12A", "12B"], flight_number: "AA123"}`

## Implementation Steps

### Phase 1: Project Setup & Dependencies

#### Step 1.1: Update Maven Dependencies
- [x] Add Kafka client dependencies for Redpanda
- [x] Add validation dependencies (Bean Validation)
- [x] Add JSON processing dependencies
- [x] Add monitoring dependencies (Micrometer)

#### Step 1.2: Application Configuration
- [x] Create `application.yml` with database configurations
- [x] Configure PostgreSQL connection settings
- [x] Configure Neo4j connection settings
- [ ] Configure Redpanda/Kafka producer settings
- [ ] Add logging configuration

#### Step 1.3: Database Setup Scripts
- [x] spring bpt does automatically

### Phase 2: Core Domain Models

#### Step 2.1: PostgreSQL Entities
- [x] Create `FlightInstance` entity
- [x] Create `Seat` entity
- [x] Create `SeatHold` entity
- [x] Create `Ticket` entity
- [x] Create `TicketSeat` entity
- [x] Create `OutboxEvent` entity

#### Step 2.2: Neo4j Entities
- [x] Create `Airport` entity
- [x] Create `FlightInstance` entity (Neo4j version)
- [x] Create `Itinerary` entity

#### Step 2.3: DTOs and Request/Response Models
- [x] Create `SearchRequest` DTO
- [x] Create `SearchResponse` DTO
- [x] Create `FlightOption` DTO
- [x] Create `FlightLeg` DTO
- [x] Create `BookingRequest` DTO
- [x] Create `BookingResponse` DTO
- [x] Create `PaymentRequest` DTO
- [x] Create `PaymentResponse` DTO

### Phase 3: Database Repositories

#### Step 3.1: PostgreSQL Repositories
- [x] Create `FlightInstanceRepository`
- [x] Create `SeatRepository`
- [x] Create `SeatHoldRepository`
- [x] Create `TicketRepository`
- [x] Create `OutboxEventRepository`

#### Step 3.2: Neo4j Repositories
- [x] Create `AirportRepository`
- [x] Create `FlightInstanceNodeRepository` (Neo4j)
- [x] Create `ItineraryRepository`

### Phase 4: Core Business Logic

#### ✅ **COMPLETED: Step 4.1 - Booking Service Implementation**

**Key Accomplishments:**
- ✅ **SeatHoldService**: Atomic seat allocation with `FOR UPDATE SKIP LOCKED` for concurrency
- ✅ **TicketService**: Complete ticket lifecycle management with proper transactions
- ✅ **BookingService**: Multi-flight booking orchestration with proper cleanup
- ✅ **Service Delegation**: Clean separation of concerns between services
- ✅ **Error Handling**: Comprehensive cleanup on failures
- ✅ **Multi-Flight Support**: Sequential booking with session-based cleanup

#### Step 4.1: Booking Service Implementation
- [x] Create `SeatHoldService`
  - [x] Implement atomic seat allocation logic with FOR UPDATE SKIP LOCKED
  - [x] Implement seat hold confirmation and cancellation
  - [x] Add validation for seat availability
  - [x] Add session-based hold management
- [x] Create `TicketService`
  - [x] Implement ticket creation with seat relationships
  - [x] Implement ticket confirmation and cancellation
  - [x] Implement payment ID management
- [x] Create `BookingService`
  - [x] Implement `initiateBooking()` - Multi-flight seat hold creation
  - [x] Implement `processPayment()` - Payment success/failure handling
  - [x] Implement proper service delegation and cleanup

#### Step 4.2: Search Service Implementation
- [x] Create `FlightSearchService`
  - [x] Implement itinerary cache lookup
  - [x] Implement graph traversal for missing itineraries
  - [x] Implement itinerary persistence
- [x] Create `ItineraryService`
  - [x] Implement itinerary generation logic
  - [x] Implement price calculation
  - [x] Implement duration calculation

### Phase 5: REST API Controllers

#### ✅ **COMPLETED: Step 5 - REST API Controllers**

**Key Accomplishments:**
- ✅ **SearchController**: `GET /api/v1/search/flights` endpoint with validation
- ✅ **BookingController**: `POST /api/v1/booking/initiate` endpoint with error handling
- ✅ **PaymentController**: `POST /api/v1/payment/process` endpoint with status management
- ✅ **GlobalExceptionHandler**: Centralized error handling across all controllers
- ✅ **Health Check Endpoints**: Service health monitoring
- ✅ **CORS Configuration**: Cross-origin support for frontend integration

#### Step 5.1: Search Controller
- [x] Create `SearchController`
- [x] Implement `GET /api/v1/search/flights` endpoint
- [x] Add request validation
- [x] Add response formatting
- [x] Add error handling

#### Step 5.2: Booking Controller
- [x] Create `BookingController`
- [x] Implement `POST /api/v1/booking/initiate` endpoint
- [x] Add request validation
- [x] Add response formatting
- [x] Add error handling

#### Step 5.3: Payment Controller
- [x] Create `PaymentController`
- [x] Implement `POST /api/v1/payment/process` endpoint
- [x] Add request validation
- [x] Add response formatting
- [x] Add error handling

### Phase 6: Configuration & Infrastructure

#### Step 6.1: Database Configuration
- [ ] Configure PostgreSQL connection pool
- [ ] Configure Neo4j connection pool
- [ ] Add database health checks
- [ ] Configure transaction management

#### Step 6.2: Neo4j Relationships & Performance Optimization
- [x] **Upgrade Spring Data Neo4j to 7.3.2** - Latest stable version with enhanced relationship support
- [x] **Create Relationship Entities** - DepartsWith, ArrivesAt, IncludesLeg with @TargetNode annotations
- [ ] **Add Relationships to Main Entities** - Connect Airport, FlightInstance, Itinerary nodes
- [ ] **Implement Neo4j Indexes** - Critical indexes for search performance (itinerary_search_seats, flight_source_dest_status)
- [ ] **Replace In-Memory Traversals** - Use Cypher graph queries instead of nested loops for connecting flights
- [ ] **Optimize Two-Stop Generation** - Eliminate O(n³) complexity with proper graph traversal

#### Step 6.3: Application Configuration
- [ ] Configure service ports
- [ ] Configure timeouts and retries
- [ ] Configure monitoring endpoints
- [ ] Add CORS configuration

### Phase 7: Event Processing & Advanced Features

#### Step 7.1: Event Models
- [ ] Create `SeatHoldCreated` event
- [ ] Create `SeatHoldReleased` event
- [ ] Create `SeatBooked` event
- [ ] Create base `SeatEvent` class

#### Step 7.2: Outbox Publisher
- [ ] Create `OutboxPublisher` component
- [ ] Implement scheduled polling mechanism
- [ ] Implement Kafka producer integration
- [ ] Add error handling and retry logic
- [ ] Add idempotency checks

#### Step 7.3: Graph Sync Consumer
- [ ] Create `GraphSyncConsumer` component
- [ ] Implement Kafka consumer integration
- [ ] Implement event processing logic
- [ ] Add Neo4j update operations
- [ ] Add idempotency tracking

#### Step 7.4: Kafka/Redpanda Configuration
- [ ] Configure Kafka producer properties
- [ ] Configure Kafka consumer properties
- [ ] Create topic management
- [ ] Add Kafka health checks

### Phase 8: Testing & Validation

#### Step 8.1: Unit Tests
- [ ] Test booking service logic
- [ ] Test search service logic
- [ ] Test repository operations

#### Step 8.2: Integration Tests
- [ ] Test API endpoints
- [ ] Test database operations
- [ ] Test cross-service communication

#### Step 8.3: End-to-End Tests
- [ ] Test complete booking flow
- [ ] Test search functionality
- [ ] Test payment confirmation
- [ ] Test error scenarios

#### Step 8.4: Event Processing Tests
- [ ] Test event publishing/consuming
- [ ] Test outbox pattern
- [ ] Test graph sync consumer

### Phase 9: Monitoring & Observability

#### Step 9.1: Metrics Implementation
- [ ] Add booking success rate metrics
- [ ] Add search latency metrics
- [ ] Add outbox lag metrics
- [ ] Add consumer lag metrics

#### Step 9.2: Logging Implementation
- [ ] Add structured logging
- [ ] Add correlation IDs
- [ ] Add audit trails
- [ ] Add error tracking

#### Step 9.3: Health Checks
- [ ] Add database health checks
- [ ] Add Kafka health checks
- [ ] Add service health checks
- [ ] Add dependency health checks

### Phase 10: Data Seeding & Setup

#### Step 10.1: Sample Data Creation
- [ ] Create sample airports in Neo4j
- [ ] Create sample flight instances in PostgreSQL
- [ ] Create sample seats for each flight
- [ ] Create sample itineraries in Neo4j

#### Step 10.2: Database Initialization
- [ ] Create database setup scripts
- [ ] Create schema migration scripts
- [ ] Create data seeding scripts
- [ ] Create cleanup scripts

## Success Criteria

### Functional Requirements
- [ ] Search API returns flights in 300-500ms
- [ ] Booking API creates holds atomically
- [ ] Payment API confirms bookings correctly
- [ ] Event processing maintains consistency

### Non-Functional Requirements
- [ ] Strong consistency for booking operations
- [ ] Eventual consistency for search operations
- [ ] Proper error handling and validation
- [ ] Comprehensive logging and monitoring

### Technical Requirements
- [ ] All APIs follow RESTful conventions
- [ ] Proper input validation and sanitization
- [ ] Comprehensive error responses
- [ ] Health check endpoints
- [ ] Metrics and observability

## Risk Mitigation

### Technical Risks
- **Database Deadlocks**: Use FOR UPDATE SKIP LOCKED
- **Event Processing Failures**: Implement retry logic and DLQ
- **Search Performance**: Implement proper indexing and caching
- **Data Consistency**: Implement reconciliation jobs

### Operational Risks
- **Service Dependencies**: Implement circuit breakers
- **Resource Exhaustion**: Implement rate limiting
- **Data Loss**: Implement proper backup strategies
- **Monitoring Gaps**: Implement comprehensive observability

## Current Progress

### ✅ **COMPLETED PHASES**
- **Phase 1**: Project Setup & Dependencies ✅
- **Phase 2**: Domain Models & DTOs ✅
- **Phase 3**: Database Repositories ✅
- **Phase 4**: Core Business Logic Services ✅
- **Phase 5**: REST API Controllers ✅

### 🔄 **IN PROGRESS**
- **Phase 6**: Configuration & Infrastructure

### 📋 **NEXT PHASES**
- **Phase 6**: Configuration & Infrastructure
- **Phase 7**: Event Processing & Advanced Features
- **Phase 8**: Testing & Validation
- **Phase 9**: Monitoring & Observability
- **Phase 10**: Data Seeding & Setup

## Next Steps

1. **Continue with Search Services**: Implement `FlightSearchService` and `ItineraryService`
2. **Create REST Controllers**: Build API endpoints for search and booking
3. **Add Configuration**: Set up database connections and application config
4. **Implement Event Processing**: Add outbox pattern and Kafka integration
5. **Add Testing**: Unit tests, integration tests, and end-to-end tests
6. **Production Readiness**: Monitoring, logging, and deployment

This plan provides a structured approach to implementing the flight booking system with clear milestones, success criteria, and risk mitigation strategies. Each step builds upon the previous ones, ensuring a solid foundation for the complete system.
