// Simplified Neo4j initialization for testing
// Clear existing data
MATCH (n) DETACH DELETE n;

// Create indexes
CREATE INDEX airport_code_index IF NOT EXISTS FOR (a:Airport) ON (a.code);
CREATE INDEX flight_instance_id_index IF NOT EXISTS FOR (f:FlightInstance) ON (f.id);

// Create sample airports
CREATE (jfk:Airport {code: "JFK", name: "John F. Kennedy International Airport", city: "New York", country: "USA", lat: 40.6413, lon: -73.7781});
CREATE (lax:Airport {code: "LAX", name: "Los Angeles International Airport", city: "Los Angeles", country: "USA", lat: 33.9425, lon: -118.4081});
CREATE (ord:Airport {code: "ORD", name: "O'Hare International Airport", city: "Chicago", country: "USA", lat: 41.9786, lon: -87.9048});

// Create sample flights
CREATE (f1:FlightInstance {id: "1", flightNo: "UA0001", source: "JFK", destination: "LAX", departureTime: datetime("2025-10-01T08:00:00Z"), arrivalTime: datetime("2025-10-01T11:30:00Z"), priceMoney: 45000, status: "ACTIVE"});
CREATE (f2:FlightInstance {id: "2", flightNo: "UA0002", source: "LAX", destination: "JFK", departureTime: datetime("2025-10-01T14:00:00Z"), arrivalTime: datetime("2025-10-01T22:30:00Z"), priceMoney: 45000, status: "ACTIVE"});

// Create relationships
MATCH (a:Airport {code: "JFK"}), (f:FlightInstance {id: "1"}) CREATE (a)-[:DEPARTS_WITH]->(f);
MATCH (a:Airport {code: "LAX"}), (f:FlightInstance {id: "1"}) CREATE (f)-[:ARRIVES_AT]->(a);
MATCH (a:Airport {code: "LAX"}), (f:FlightInstance {id: "2"}) CREATE (a)-[:DEPARTS_WITH]->(f);
MATCH (a:Airport {code: "JFK"}), (f:FlightInstance {id: "2"}) CREATE (f)-[:ARRIVES_AT]->(a);

// Log completion
RETURN 'Neo4j simple dataset loaded successfully!' as message, count(*) as totalNodes;
