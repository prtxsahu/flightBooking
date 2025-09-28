// Sync FlightInstance data from PostgreSQL to Neo4j for 2025
// This creates FlightInstance nodes and relationships for graph traversal

// Clear existing flight instances
MATCH (f:FlightInstance) DETACH DELETE f;

// Create FlightInstance nodes for September 29, 2025
CREATE (f1:FlightInstance {
  id: "1",
  flightNo: "AA100",
  source: "JFK",
  destination: "LAX",
  departureTime: datetime("2025-09-29T08:00:00Z"),
  arrivalTime: datetime("2025-09-29T11:00:00Z"),
  priceMoney: 29900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f2:FlightInstance {
  id: "2",
  flightNo: "UA200",
  source: "JFK",
  destination: "LAX",
  departureTime: datetime("2025-09-29T14:00:00Z"),
  arrivalTime: datetime("2025-09-29T17:00:00Z"),
  priceMoney: 32900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f3:FlightInstance {
  id: "3",
  flightNo: "DL300",
  source: "JFK",
  destination: "LAX",
  departureTime: datetime("2025-09-29T20:00:00Z"),
  arrivalTime: datetime("2025-09-29T23:00:00Z"),
  priceMoney: 27900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f4:FlightInstance {
  id: "4",
  flightNo: "AA101",
  source: "LAX",
  destination: "JFK",
  departureTime: datetime("2025-09-29T09:00:00Z"),
  arrivalTime: datetime("2025-09-29T18:00:00Z"),
  priceMoney: 29900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f5:FlightInstance {
  id: "5",
  flightNo: "UA201",
  source: "LAX",
  destination: "JFK",
  departureTime: datetime("2025-09-29T15:00:00Z"),
  arrivalTime: datetime("2025-09-30T00:00:00Z"),
  priceMoney: 32900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

// Connection flights for JFK->ORD->LAX routes (September 29, 2025)
CREATE (f6:FlightInstance {
  id: "6",
  flightNo: "AA102",
  source: "JFK",
  destination: "ORD",
  departureTime: datetime("2025-09-29T07:00:00Z"),
  arrivalTime: datetime("2025-09-29T09:00:00Z"),
  priceMoney: 19900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f7:FlightInstance {
  id: "7",
  flightNo: "UA202",
  source: "JFK",
  destination: "ORD",
  departureTime: datetime("2025-09-29T13:00:00Z"),
  arrivalTime: datetime("2025-09-29T15:00:00Z"),
  priceMoney: 17900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f8:FlightInstance {
  id: "8",
  flightNo: "AA103",
  source: "ORD",
  destination: "LAX",
  departureTime: datetime("2025-09-29T10:00:00Z"),
  arrivalTime: datetime("2025-09-29T12:00:00Z"),
  priceMoney: 24900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f9:FlightInstance {
  id: "9",
  flightNo: "UA203",
  source: "ORD",
  destination: "LAX",
  departureTime: datetime("2025-09-29T16:00:00Z"),
  arrivalTime: datetime("2025-09-29T18:00:00Z"),
  priceMoney: 22900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

// JFK->DFW->LAX routes (September 29, 2025)
CREATE (f10:FlightInstance {
  id: "10",
  flightNo: "AA104",
  source: "JFK",
  destination: "DFW",
  departureTime: datetime("2025-09-29T06:00:00Z"),
  arrivalTime: datetime("2025-09-29T09:00:00Z"),
  priceMoney: 21900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f11:FlightInstance {
  id: "11",
  flightNo: "AA105",
  source: "DFW",
  destination: "LAX",
  departureTime: datetime("2025-09-29T11:00:00Z"),
  arrivalTime: datetime("2025-09-29T13:00:00Z"),
  priceMoney: 19900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

// Additional flights (September 29, 2025)
CREATE (f12:FlightInstance {
  id: "12",
  flightNo: "DL301",
  source: "ATL",
  destination: "LAX",
  departureTime: datetime("2025-09-29T12:00:00Z"),
  arrivalTime: datetime("2025-09-29T14:00:00Z"),
  priceMoney: 25900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f13:FlightInstance {
  id: "13",
  flightNo: "DL302",
  source: "LAX",
  destination: "ATL",
  departureTime: datetime("2025-09-29T18:00:00Z"),
  arrivalTime: datetime("2025-09-29T20:00:00Z"),
  priceMoney: 25900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f14:FlightInstance {
  id: "14",
  flightNo: "UA204",
  source: "DEN",
  destination: "LAX",
  departureTime: datetime("2025-09-29T08:00:00Z"),
  arrivalTime: datetime("2025-09-29T10:00:00Z"),
  priceMoney: 20900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f15:FlightInstance {
  id: "15",
  flightNo: "UA205",
  source: "LAX",
  destination: "DEN",
  departureTime: datetime("2025-09-29T14:00:00Z"),
  arrivalTime: datetime("2025-09-29T16:00:00Z"),
  priceMoney: 20900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

// September 30, 2025 flights
CREATE (f16:FlightInstance {
  id: "16",
  flightNo: "AA106",
  source: "JFK",
  destination: "LAX",
  departureTime: datetime("2025-09-30T08:00:00Z"),
  arrivalTime: datetime("2025-09-30T11:00:00Z"),
  priceMoney: 29900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f17:FlightInstance {
  id: "17",
  flightNo: "UA206",
  source: "JFK",
  destination: "LAX",
  departureTime: datetime("2025-09-30T14:00:00Z"),
  arrivalTime: datetime("2025-09-30T17:00:00Z"),
  priceMoney: 32900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f18:FlightInstance {
  id: "18",
  flightNo: "DL306",
  source: "JFK",
  destination: "LAX",
  departureTime: datetime("2025-09-30T20:00:00Z"),
  arrivalTime: datetime("2025-09-30T23:00:00Z"),
  priceMoney: 27900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f19:FlightInstance {
  id: "19",
  flightNo: "AA107",
  source: "LAX",
  destination: "JFK",
  departureTime: datetime("2025-09-30T09:00:00Z"),
  arrivalTime: datetime("2025-09-30T18:00:00Z"),
  priceMoney: 29900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f20:FlightInstance {
  id: "20",
  flightNo: "UA207",
  source: "LAX",
  destination: "JFK",
  departureTime: datetime("2025-09-30T15:00:00Z"),
  arrivalTime: datetime("2025-10-01T00:00:00Z"),
  priceMoney: 32900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

// Connection flights for September 30, 2025
CREATE (f21:FlightInstance {
  id: "21",
  flightNo: "AA108",
  source: "JFK",
  destination: "ORD",
  departureTime: datetime("2025-09-30T07:00:00Z"),
  arrivalTime: datetime("2025-09-30T09:00:00Z"),
  priceMoney: 19900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f22:FlightInstance {
  id: "22",
  flightNo: "UA208",
  source: "JFK",
  destination: "ORD",
  departureTime: datetime("2025-09-30T13:00:00Z"),
  arrivalTime: datetime("2025-09-30T15:00:00Z"),
  priceMoney: 17900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f23:FlightInstance {
  id: "23",
  flightNo: "AA109",
  source: "ORD",
  destination: "LAX",
  departureTime: datetime("2025-09-30T10:00:00Z"),
  arrivalTime: datetime("2025-09-30T12:00:00Z"),
  priceMoney: 24900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f24:FlightInstance {
  id: "24",
  flightNo: "UA209",
  source: "ORD",
  destination: "LAX",
  departureTime: datetime("2025-09-30T16:00:00Z"),
  arrivalTime: datetime("2025-09-30T18:00:00Z"),
  priceMoney: 22900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f25:FlightInstance {
  id: "25",
  flightNo: "AA110",
  source: "JFK",
  destination: "DFW",
  departureTime: datetime("2025-09-30T06:00:00Z"),
  arrivalTime: datetime("2025-09-30T09:00:00Z"),
  priceMoney: 21900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f26:FlightInstance {
  id: "26",
  flightNo: "AA111",
  source: "DFW",
  destination: "LAX",
  departureTime: datetime("2025-09-30T11:00:00Z"),
  arrivalTime: datetime("2025-09-30T13:00:00Z"),
  priceMoney: 19900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f27:FlightInstance {
  id: "27",
  flightNo: "DL307",
  source: "ATL",
  destination: "LAX",
  departureTime: datetime("2025-09-30T12:00:00Z"),
  arrivalTime: datetime("2025-09-30T14:00:00Z"),
  priceMoney: 25900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f28:FlightInstance {
  id: "28",
  flightNo: "DL308",
  source: "LAX",
  destination: "ATL",
  departureTime: datetime("2025-09-30T18:00:00Z"),
  arrivalTime: datetime("2025-09-30T20:00:00Z"),
  priceMoney: 25900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f29:FlightInstance {
  id: "29",
  flightNo: "UA210",
  source: "DEN",
  destination: "LAX",
  departureTime: datetime("2025-09-30T08:00:00Z"),
  arrivalTime: datetime("2025-09-30T10:00:00Z"),
  priceMoney: 20900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f30:FlightInstance {
  id: "30",
  flightNo: "UA211",
  source: "LAX",
  destination: "DEN",
  departureTime: datetime("2025-09-30T14:00:00Z"),
  arrivalTime: datetime("2025-09-30T16:00:00Z"),
  priceMoney: 20900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});
