CREATE CONSTRAINT itinerary_id_unique FOR (it:Itinerary) REQUIRE it.id IS UNIQUE;
CREATE INDEX itinerary_search_key FOR (it:Itinerary) ON (it.search_key);
CREATE INDEX itinerary_min_seats FOR (it:Itinerary) ON (it.minAvailableSeats);
CREATE INDEX itinerary_search_seats FOR (it:Itinerary) ON (it.search_key, it.minAvailableSeats);
CREATE INDEX itinerary_price FOR (it:Itinerary) ON (it.total_price);

CREATE CONSTRAINT flight_instance_id_unique FOR (f:FlightInstance) REQUIRE f.id IS UNIQUE;
CREATE INDEX flight_source_destination FOR (f:FlightInstance) ON (f.source, f.destination);
CREATE INDEX flight_source FOR (f:FlightInstance) ON (f.source);
CREATE INDEX flight_destination FOR (f:FlightInstance) ON (f.destination);
CREATE INDEX flight_status FOR (f:FlightInstance) ON (f.status);
CREATE INDEX flight_source_status FOR (f:FlightInstance) ON (f.source, f.status);
CREATE INDEX flight_destination_status FOR (f:FlightInstance) ON (f.destination, f.status);
CREATE INDEX flight_source_dest_status FOR (f:FlightInstance) ON (f.source, f.destination, f.status);
CREATE INDEX flight_departure_time FOR (f:FlightInstance) ON (f.departure_time);
CREATE INDEX flight_arrival_time FOR (f:FlightInstance) ON (f.arrival_time);

CREATE CONSTRAINT airport_code_unique FOR (a:Airport) REQUIRE a.code IS UNIQUE;
CREATE INDEX airport_city FOR (a:Airport) ON (a.city);
CREATE INDEX airport_country FOR (a:Airport) ON (a.country);
