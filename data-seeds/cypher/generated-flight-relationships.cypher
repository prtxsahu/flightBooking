// Create relationships between airports and flights
// This creates DEPARTS_WITH and ARRIVES_AT relationships

// Create DEPARTS_WITH relationships for all flights
MATCH (a:Airport), (f:FlightInstance {source: a.code})
CREATE (a)-[:DEPARTS_WITH {departure_time: f.departureTime, created_at: datetime()}]->(f);

// Create ARRIVES_AT relationships for all flights
MATCH (a:Airport), (f:FlightInstance {destination: a.code})
CREATE (f)-[:ARRIVES_AT {arrival_time: f.arrivalTime, created_at: datetime()}]->(a);
