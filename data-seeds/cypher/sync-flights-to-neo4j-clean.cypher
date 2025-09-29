CREATE (f1:FlightInstance {
  id: "1",
  flightNo: "AA100",
  source: "JFK",
  destination: "LAX",
  departureTime: datetime("2024-09-29T08:00:00Z"),
  arrivalTime: datetime("2024-09-29T11:00:00Z"),
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
  departureTime: datetime("2024-09-29T14:00:00Z"),
  arrivalTime: datetime("2024-09-29T17:00:00Z"),
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
  departureTime: datetime("2024-09-29T20:00:00Z"),
  arrivalTime: datetime("2024-09-29T23:00:00Z"),
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
  departureTime: datetime("2024-09-29T09:00:00Z"),
  arrivalTime: datetime("2024-09-29T18:00:00Z"),
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
  departureTime: datetime("2024-09-29T15:00:00Z"),
  arrivalTime: datetime("2024-09-29T00:00:00Z"),
  priceMoney: 32900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f6:FlightInstance {
  id: "6",
  flightNo: "AA102",
  source: "JFK",
  destination: "ORD",
  departureTime: datetime("2024-09-29T07:00:00Z"),
  arrivalTime: datetime("2024-09-29T09:00:00Z"),
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
  departureTime: datetime("2024-09-29T13:00:00Z"),
  arrivalTime: datetime("2024-09-29T15:00:00Z"),
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
  departureTime: datetime("2024-09-29T10:00:00Z"),
  arrivalTime: datetime("2024-09-29T12:00:00Z"),
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
  departureTime: datetime("2024-09-29T16:00:00Z"),
  arrivalTime: datetime("2024-09-29T18:00:00Z"),
  priceMoney: 22900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f10:FlightInstance {
  id: "10",
  flightNo: "AA104",
  source: "JFK",
  destination: "DFW",
  departureTime: datetime("2024-09-29T06:00:00Z"),
  arrivalTime: datetime("2024-09-29T09:00:00Z"),
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
  departureTime: datetime("2024-09-29T11:00:00Z"),
  arrivalTime: datetime("2024-09-29T13:00:00Z"),
  priceMoney: 19900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});

CREATE (f12:FlightInstance {
  id: "12",
  flightNo: "DL301",
  source: "ATL",
  destination: "LAX",
  departureTime: datetime("2024-09-29T12:00:00Z"),
  arrivalTime: datetime("2024-09-29T14:00:00Z"),
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
  departureTime: datetime("2024-09-29T18:00:00Z"),
  arrivalTime: datetime("2024-09-29T20:00:00Z"),
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
  departureTime: datetime("2024-09-29T08:00:00Z"),
  arrivalTime: datetime("2024-09-29T10:00:00Z"),
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
  departureTime: datetime("2024-09-29T14:00:00Z"),
  arrivalTime: datetime("2024-09-29T16:00:00Z"),
  priceMoney: 20900,
  status: "ACTIVE",
  totalAvailableSeats: 50,
  totalSeats: 50,
  createdAt: datetime()
});
