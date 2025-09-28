// Neo4j Database Initialization Script
// This script sets up the graph database with airports, flights, and relationships

// Clear existing data
MATCH (n) DETACH DELETE n;

// Create indexes for better performance
CREATE INDEX airport_code_index IF NOT EXISTS FOR (a:Airport) ON (a.code);
CREATE INDEX flight_instance_id_index IF NOT EXISTS FOR (f:FlightInstance) ON (f.id);
CREATE INDEX flight_instance_source_index IF NOT EXISTS FOR (f:FlightInstance) ON (f.source);
CREATE INDEX flight_instance_destination_index IF NOT EXISTS FOR (f:FlightInstance) ON (f.destination);
CREATE INDEX flight_instance_departure_time_index IF NOT EXISTS FOR (f:FlightInstance) ON (f.departureTime);

// Create airports (sample of 20 major airports)
CREATE (a1:Airport {code: 'JFK', name: 'John F. Kennedy International Airport', city: 'New York', country: 'USA', latitude: 40.6413, longitude: -73.7781})
CREATE (a2:Airport {code: 'LAX', name: 'Los Angeles International Airport', city: 'Los Angeles', country: 'USA', latitude: 33.9416, longitude: -118.4085})
CREATE (a3:Airport {code: 'ORD', name: 'O\'Hare International Airport', city: 'Chicago', country: 'USA', latitude: 41.9786, longitude: -87.9048})
CREATE (a4:Airport {code: 'SFO', name: 'San Francisco International Airport', city: 'San Francisco', country: 'USA', latitude: 37.6213, longitude: -122.3790})
CREATE (a5:Airport {code: 'ATL', name: 'Hartsfield-Jackson Atlanta International Airport', city: 'Atlanta', country: 'USA', latitude: 33.6407, longitude: -84.4277})
CREATE (a6:Airport {code: 'SEA', name: 'Seattle-Tacoma International Airport', city: 'Seattle', country: 'USA', latitude: 47.4502, longitude: -122.3088})
CREATE (a7:Airport {code: 'DEN', name: 'Denver International Airport', city: 'Denver', country: 'USA', latitude: 39.8561, longitude: -104.6737})
CREATE (a8:Airport {code: 'LAS', name: 'Harry Reid International Airport', city: 'Las Vegas', country: 'USA', latitude: 36.0840, longitude: -115.1537})
CREATE (a9:Airport {code: 'MIA', name: 'Miami International Airport', city: 'Miami', country: 'USA', latitude: 25.7959, longitude: -80.2870})
CREATE (a10:Airport {code: 'BOS', name: 'Logan International Airport', city: 'Boston', country: 'USA', latitude: 42.3656, longitude: -71.0096})
CREATE (a11:Airport {code: 'DFW', name: 'Dallas/Fort Worth International Airport', city: 'Dallas', country: 'USA', latitude: 32.8968, longitude: -97.0380})
CREATE (a12:Airport {code: 'PHX', name: 'Phoenix Sky Harbor International Airport', city: 'Phoenix', country: 'USA', latitude: 33.4342, longitude: -112.0116})
CREATE (a13:Airport {code: 'IAH', name: 'George Bush Intercontinental Airport', city: 'Houston', country: 'USA', latitude: 29.9844, longitude: -95.3414})
CREATE (a14:Airport {code: 'MCO', name: 'Orlando International Airport', city: 'Orlando', country: 'USA', latitude: 28.4312, longitude: -81.3081})
CREATE (a15:Airport {code: 'DTW', name: 'Detroit Metropolitan Wayne County Airport', city: 'Detroit', country: 'USA', latitude: 42.2162, longitude: -83.3554})
CREATE (a16:Airport {code: 'MSP', name: 'Minneapolis-Saint Paul International Airport', city: 'Minneapolis', country: 'USA', latitude: 44.8848, longitude: -93.2223})
CREATE (a17:Airport {code: 'PHL', name: 'Philadelphia International Airport', city: 'Philadelphia', country: 'USA', latitude: 39.8729, longitude: -75.2437})
CREATE (a18:Airport {code: 'LGA', name: 'LaGuardia Airport', city: 'New York', country: 'USA', latitude: 40.7769, longitude: -73.8740})
CREATE (a19:Airport {code: 'BWI', name: 'Baltimore/Washington International Thurgood Marshall Airport', city: 'Baltimore', country: 'USA', latitude: 39.1774, longitude: -76.6684})
CREATE (a20:Airport {code: 'RIC', name: 'Richmond International Airport', city: 'Richmond', country: 'USA', latitude: 37.5052, longitude: -77.3197});

// Create flight instances (sample flights for October 1, 2025)
CREATE (f1:FlightInstance {id: '1', flightNo: 'AA100', source: 'JFK', destination: 'LAX', departureTime: datetime('2025-10-01T08:00:00Z'), arrivalTime: datetime('2025-10-01T11:30:00Z'), priceMoney: 45000, totalAvailableSeats: 50, status: 'ACTIVE'})
CREATE (f2:FlightInstance {id: '2', flightNo: 'AA101', source: 'LAX', destination: 'JFK', departureTime: datetime('2025-10-01T14:00:00Z'), arrivalTime: datetime('2025-10-01T22:30:00Z'), priceMoney: 45000, totalAvailableSeats: 50, status: 'ACTIVE'})
CREATE (f3:FlightInstance {id: '3', flightNo: 'UA200', source: 'ORD', destination: 'SFO', departureTime: datetime('2025-10-01T09:15:00Z'), arrivalTime: datetime('2025-10-01T12:45:00Z'), priceMoney: 38000, totalAvailableSeats: 50, status: 'ACTIVE'})
CREATE (f4:FlightInstance {id: '4', flightNo: 'DL300', source: 'ATL', destination: 'SEA', departureTime: datetime('2025-10-01T10:30:00Z'), arrivalTime: datetime('2025-10-01T14:15:00Z'), priceMoney: 42000, totalAvailableSeats: 50, status: 'ACTIVE'})
CREATE (f5:FlightInstance {id: '5', flightNo: 'WN400', source: 'DEN', destination: 'LAS', departureTime: datetime('2025-10-01T11:45:00Z'), arrivalTime: datetime('2025-10-01T13:30:00Z'), priceMoney: 25000, totalAvailableSeats: 50, status: 'ACTIVE'})
CREATE (f6:FlightInstance {id: '6', flightNo: 'AA102', source: 'JFK', destination: 'RIC', departureTime: datetime('2025-10-01T12:00:00Z'), arrivalTime: datetime('2025-10-01T13:30:00Z'), priceMoney: 35000, totalAvailableSeats: 50, status: 'ACTIVE'})
CREATE (f7:FlightInstance {id: '7', flightNo: 'DL301', source: 'ATL', destination: 'RIC', departureTime: datetime('2025-10-01T15:00:00Z'), arrivalTime: datetime('2025-10-01T16:30:00Z'), priceMoney: 32000, totalAvailableSeats: 50, status: 'ACTIVE'})
CREATE (f8:FlightInstance {id: '8', flightNo: 'UA201', source: 'ORD', destination: 'ATL', departureTime: datetime('2025-10-01T13:00:00Z'), arrivalTime: datetime('2025-10-01T15:30:00Z'), priceMoney: 40000, totalAvailableSeats: 50, status: 'ACTIVE'});

// Create relationships between airports and flights
MATCH (a:Airport {code: 'JFK'}), (f:FlightInstance {id: '1'})
CREATE (a)-[:DEPARTS_WITH]->(f);

MATCH (a:Airport {code: 'LAX'}), (f:FlightInstance {id: '1'})
CREATE (f)-[:ARRIVES_AT]->(a);

MATCH (a:Airport {code: 'LAX'}), (f:FlightInstance {id: '2'})
CREATE (a)-[:DEPARTS_WITH]->(f);

MATCH (a:Airport {code: 'JFK'}), (f:FlightInstance {id: '2'})
CREATE (f)-[:ARRIVES_AT]->(a);

MATCH (a:Airport {code: 'ORD'}), (f:FlightInstance {id: '3'})
CREATE (a)-[:DEPARTS_WITH]->(f);

MATCH (a:Airport {code: 'SFO'}), (f:FlightInstance {id: '3'})
CREATE (f)-[:ARRIVES_AT]->(a);

MATCH (a:Airport {code: 'ATL'}), (f:FlightInstance {id: '4'})
CREATE (a)-[:DEPARTS_WITH]->(f);

MATCH (a:Airport {code: 'SEA'}), (f:FlightInstance {id: '4'})
CREATE (f)-[:ARRIVES_AT]->(a);

MATCH (a:Airport {code: 'DEN'}), (f:FlightInstance {id: '5'})
CREATE (a)-[:DEPARTS_WITH]->(f);

MATCH (a:Airport {code: 'LAS'}), (f:FlightInstance {id: '5'})
CREATE (f)-[:ARRIVES_AT]->(a);

MATCH (a:Airport {code: 'JFK'}), (f:FlightInstance {id: '6'})
CREATE (a)-[:DEPARTS_WITH]->(f);

MATCH (a:Airport {code: 'RIC'}), (f:FlightInstance {id: '6'})
CREATE (f)-[:ARRIVES_AT]->(a);

MATCH (a:Airport {code: 'ATL'}), (f:FlightInstance {id: '7'})
CREATE (a)-[:DEPARTS_WITH]->(f);

MATCH (a:Airport {code: 'RIC'}), (f:FlightInstance {id: '7'})
CREATE (f)-[:ARRIVES_AT]->(a);

MATCH (a:Airport {code: 'ORD'}), (f:FlightInstance {id: '8'})
CREATE (a)-[:DEPARTS_WITH]->(f);

MATCH (a:Airport {code: 'ATL'}), (f:FlightInstance {id: '8'})
CREATE (f)-[:ARRIVES_AT]->(a);

// Log completion
RETURN 'Neo4j database initialization completed successfully!' as message,
       'Sample data loaded: 20 airports, 8 flights with relationships' as details;
