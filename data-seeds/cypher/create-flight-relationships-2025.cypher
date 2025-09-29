// Create relationships between airports and flights for 2025 data

// JFK departing flights
MATCH (jfk:Airport {code: "JFK"}), (f:FlightInstance {source: "JFK"})
CREATE (jfk)-[:DEPARTS_WITH {departure_time: f.departureTime, created_at: datetime()}]->(f);

// LAX arriving flights
MATCH (lax:Airport {code: "LAX"}), (f:FlightInstance {destination: "LAX"})
CREATE (f)-[:ARRIVES_AT {arrival_time: f.arrivalTime, created_at: datetime()}]->(lax);

// LAX departing flights
MATCH (lax:Airport {code: "LAX"}), (f:FlightInstance {source: "LAX"})
CREATE (lax)-[:DEPARTS_WITH {departure_time: f.departureTime, created_at: datetime()}]->(f);

// JFK arriving flights
MATCH (jfk:Airport {code: "JFK"}), (f:FlightInstance {destination: "JFK"})
CREATE (f)-[:ARRIVES_AT {arrival_time: f.arrivalTime, created_at: datetime()}]->(jfk);

// ORD departing flights
MATCH (ord:Airport {code: "ORD"}), (f:FlightInstance {source: "ORD"})
CREATE (ord)-[:DEPARTS_WITH {departure_time: f.departureTime, created_at: datetime()}]->(f);

// ORD arriving flights
MATCH (ord:Airport {code: "ORD"}), (f:FlightInstance {destination: "ORD"})
CREATE (f)-[:ARRIVES_AT {arrival_time: f.arrivalTime, created_at: datetime()}]->(ord);

// DFW departing flights
MATCH (dfw:Airport {code: "DFW"}), (f:FlightInstance {source: "DFW"})
CREATE (dfw)-[:DEPARTS_WITH {departure_time: f.departureTime, created_at: datetime()}]->(f);

// DFW arriving flights
MATCH (dfw:Airport {code: "DFW"}), (f:FlightInstance {destination: "DFW"})
CREATE (f)-[:ARRIVES_AT {arrival_time: f.arrivalTime, created_at: datetime()}]->(dfw);

// ATL departing flights
MATCH (atl:Airport {code: "ATL"}), (f:FlightInstance {source: "ATL"})
CREATE (atl)-[:DEPARTS_WITH {departure_time: f.departureTime, created_at: datetime()}]->(f);

// ATL arriving flights
MATCH (atl:Airport {code: "ATL"}), (f:FlightInstance {destination: "ATL"})
CREATE (f)-[:ARRIVES_AT {arrival_time: f.arrivalTime, created_at: datetime()}]->(atl);

// DEN departing flights
MATCH (den:Airport {code: "DEN"}), (f:FlightInstance {source: "DEN"})
CREATE (den)-[:DEPARTS_WITH {departure_time: f.departureTime, created_at: datetime()}]->(f);

// DEN arriving flights
MATCH (den:Airport {code: "DEN"}), (f:FlightInstance {destination: "DEN"})
CREATE (f)-[:ARRIVES_AT {arrival_time: f.arrivalTime, created_at: datetime()}]->(den);
