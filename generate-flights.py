#!/usr/bin/env python3
"""
Flight Generation Script
Generates 9,000 flights (100 airports × 3 flights × 30 days) for October 2025
Creates both Neo4j Cypher and SQL INSERT statements
"""

import random
import json
from datetime import datetime, timedelta
from typing import List, Dict, Tuple

# Airport data with coordinates for distance calculation
AIRPORTS = [
    {"code": "JFK", "name": "John F. Kennedy International Airport", "city": "New York", "country": "USA", "lat": 40.6413, "lon": -73.7781},
    {"code": "LAX", "name": "Los Angeles International Airport", "city": "Los Angeles", "country": "USA", "lat": 33.9425, "lon": -118.4081},
    {"code": "ORD", "name": "O'Hare International Airport", "city": "Chicago", "country": "USA", "lat": 41.9786, "lon": -87.9048},
    {"code": "DFW", "name": "Dallas/Fort Worth International Airport", "city": "Dallas", "country": "USA", "lat": 32.8968, "lon": -97.0380},
    {"code": "ATL", "name": "Hartsfield-Jackson Atlanta International Airport", "city": "Atlanta", "country": "USA", "lat": 33.6407, "lon": -84.4277},
    {"code": "DEN", "name": "Denver International Airport", "city": "Denver", "country": "USA", "lat": 39.8561, "lon": -104.6737},
    {"code": "SFO", "name": "San Francisco International Airport", "city": "San Francisco", "country": "USA", "lat": 37.6213, "lon": -122.3790},
    {"code": "SEA", "name": "Seattle-Tacoma International Airport", "city": "Seattle", "country": "USA", "lat": 47.4502, "lon": -122.3088},
    {"code": "MIA", "name": "Miami International Airport", "city": "Miami", "country": "USA", "lat": 25.7959, "lon": -80.2870},
    {"code": "BWI", "name": "Baltimore/Washington International Thurgood Marshall Airport", "city": "Baltimore", "country": "USA", "lat": 39.1774, "lon": -76.6684},
    {"code": "PHX", "name": "Phoenix Sky Harbor International Airport", "city": "Phoenix", "country": "USA", "lat": 33.4342, "lon": -112.0116},
    {"code": "LAS", "name": "Harry Reid International Airport", "city": "Las Vegas", "country": "USA", "lat": 36.0840, "lon": -115.1537},
    {"code": "MCO", "name": "Orlando International Airport", "city": "Orlando", "country": "USA", "lat": 28.4312, "lon": -81.3081},
    {"code": "DTW", "name": "Detroit Metropolitan Wayne County Airport", "city": "Detroit", "country": "USA", "lat": 42.2162, "lon": -83.3554},
    {"code": "MSP", "name": "Minneapolis-Saint Paul International Airport", "city": "Minneapolis", "country": "USA", "lat": 44.8848, "lon": -93.2223},
    {"code": "BNA", "name": "Nashville International Airport", "city": "Nashville", "country": "USA", "lat": 36.1245, "lon": -86.6782},
    {"code": "IAD", "name": "Washington Dulles International Airport", "city": "Washington", "country": "USA", "lat": 38.9531, "lon": -77.4565},
    {"code": "BOS", "name": "Logan International Airport", "city": "Boston", "country": "USA", "lat": 42.3656, "lon": -71.0096},
    {"code": "PHL", "name": "Philadelphia International Airport", "city": "Philadelphia", "country": "USA", "lat": 39.8729, "lon": -75.2437},
    {"code": "EWR", "name": "Newark Liberty International Airport", "city": "Newark", "country": "USA", "lat": 40.6895, "lon": -74.1745},
    {"code": "AUS", "name": "Austin-Bergstrom International Airport", "city": "Austin", "country": "USA", "lat": 30.1945, "lon": -97.6699},
    {"code": "PDX", "name": "Portland International Airport", "city": "Portland", "country": "USA", "lat": 45.5898, "lon": -122.5951},
    {"code": "SLC", "name": "Salt Lake City International Airport", "city": "Salt Lake City", "country": "USA", "lat": 40.7899, "lon": -111.9791},
    {"code": "TPA", "name": "Tampa International Airport", "city": "Tampa", "country": "USA", "lat": 27.9755, "lon": -82.5332},
    {"code": "RDU", "name": "Raleigh-Durham International Airport", "city": "Raleigh", "country": "USA", "lat": 35.8776, "lon": -78.7875},
    {"code": "STL", "name": "St. Louis Lambert International Airport", "city": "St. Louis", "country": "USA", "lat": 38.7487, "lon": -90.3700},
    {"code": "CLE", "name": "Cleveland Hopkins International Airport", "city": "Cleveland", "country": "USA", "lat": 41.4117, "lon": -81.8498},
    {"code": "PIT", "name": "Pittsburgh International Airport", "city": "Pittsburgh", "country": "USA", "lat": 40.4915, "lon": -80.2329},
    {"code": "CVG", "name": "Cincinnati/Northern Kentucky International Airport", "city": "Cincinnati", "country": "USA", "lat": 39.0488, "lon": -84.6678},
    {"code": "IND", "name": "Indianapolis International Airport", "city": "Indianapolis", "country": "USA", "lat": 39.7173, "lon": -86.2944},
    {"code": "LHR", "name": "London Heathrow Airport", "city": "London", "country": "UK", "lat": 51.4700, "lon": -0.4543},
    {"code": "CDG", "name": "Charles de Gaulle Airport", "city": "Paris", "country": "France", "lat": 49.0097, "lon": 2.5479},
    {"code": "FRA", "name": "Frankfurt Airport", "city": "Frankfurt", "country": "Germany", "lat": 50.0379, "lon": 8.5622},
    {"code": "AMS", "name": "Amsterdam Airport Schiphol", "city": "Amsterdam", "country": "Netherlands", "lat": 52.3105, "lon": 4.7683},
    {"code": "MAD", "name": "Adolfo Suárez Madrid–Barajas Airport", "city": "Madrid", "country": "Spain", "lat": 40.4983, "lon": -3.5676},
    {"code": "FCO", "name": "Leonardo da Vinci–Fiumicino Airport", "city": "Rome", "country": "Italy", "lat": 41.8003, "lon": 12.2389},
    {"code": "ZUR", "name": "Zurich Airport", "city": "Zurich", "country": "Switzerland", "lat": 47.4647, "lon": 8.5492},
    {"code": "VIE", "name": "Vienna International Airport", "city": "Vienna", "country": "Austria", "lat": 48.1103, "lon": 16.5697},
    {"code": "ARN", "name": "Stockholm Arlanda Airport", "city": "Stockholm", "country": "Sweden", "lat": 59.6519, "lon": 17.9186},
    {"code": "CPH", "name": "Copenhagen Airport", "city": "Copenhagen", "country": "Denmark", "lat": 55.6179, "lon": 12.6561},
    {"code": "NRT", "name": "Narita International Airport", "city": "Tokyo", "country": "Japan", "lat": 35.7720, "lon": 140.3928},
    {"code": "ICN", "name": "Incheon International Airport", "city": "Seoul", "country": "South Korea", "lat": 37.4602, "lon": 126.4407},
    {"code": "PVG", "name": "Shanghai Pudong International Airport", "city": "Shanghai", "country": "China", "lat": 31.1434, "lon": 121.8052},
    {"code": "HKG", "name": "Hong Kong International Airport", "city": "Hong Kong", "country": "Hong Kong", "lat": 22.3080, "lon": 113.9185},
    {"code": "SIN", "name": "Singapore Changi Airport", "city": "Singapore", "country": "Singapore", "lat": 1.3644, "lon": 103.9915},
    {"code": "BKK", "name": "Suvarnabhumi Airport", "city": "Bangkok", "country": "Thailand", "lat": 13.6900, "lon": 100.7501},
    {"code": "KUL", "name": "Kuala Lumpur International Airport", "city": "Kuala Lumpur", "country": "Malaysia", "lat": 2.7456, "lon": 101.7099},
    {"code": "MNL", "name": "Ninoy Aquino International Airport", "city": "Manila", "country": "Philippines", "lat": 14.5086, "lon": 121.0196},
    {"code": "BOM", "name": "Chhatrapati Shivaji Maharaj International Airport", "city": "Mumbai", "country": "India", "lat": 19.0896, "lon": 72.8656},
    {"code": "DEL", "name": "Indira Gandhi International Airport", "city": "New Delhi", "country": "India", "lat": 28.5562, "lon": 77.1000},
    {"code": "DXB", "name": "Dubai International Airport", "city": "Dubai", "country": "UAE", "lat": 25.2532, "lon": 55.3657},
    {"code": "DOH", "name": "Hamad International Airport", "city": "Doha", "country": "Qatar", "lat": 25.2611, "lon": 51.5651},
    {"code": "IST", "name": "Istanbul Airport", "city": "Istanbul", "country": "Turkey", "lat": 41.2753, "lon": 28.7519},
    {"code": "CAI", "name": "Cairo International Airport", "city": "Cairo", "country": "Egypt", "lat": 30.1127, "lon": 31.4000},
    {"code": "JNB", "name": "O. R. Tambo International Airport", "city": "Johannesburg", "country": "South Africa", "lat": -26.1367, "lon": 28.2411},
    {"code": "NBO", "name": "Jomo Kenyatta International Airport", "city": "Nairobi", "country": "Kenya", "lat": -1.3192, "lon": 36.9278},
    {"code": "LOS", "name": "Murtala Muhammed International Airport", "city": "Lagos", "country": "Nigeria", "lat": 6.5774, "lon": 3.3212},
    {"code": "CPT", "name": "Cape Town International Airport", "city": "Cape Town", "country": "South Africa", "lat": -33.9648, "lon": 18.6017},
    {"code": "YYZ", "name": "Toronto Pearson International Airport", "city": "Toronto", "country": "Canada", "lat": 43.6777, "lon": -79.6248},
    {"code": "YVR", "name": "Vancouver International Airport", "city": "Vancouver", "country": "Canada", "lat": 49.1967, "lon": -123.1815},
    {"code": "GRU", "name": "São Paulo/Guarulhos International Airport", "city": "São Paulo", "country": "Brazil", "lat": -23.4356, "lon": -46.4731},
    {"code": "EZE", "name": "Ministro Pistarini International Airport", "city": "Buenos Aires", "country": "Argentina", "lat": -34.8222, "lon": -58.5358},
    {"code": "SCL", "name": "Arturo Merino Benítez International Airport", "city": "Santiago", "country": "Chile", "lat": -33.3928, "lon": -70.7858},
    {"code": "LIM", "name": "Jorge Chávez International Airport", "city": "Lima", "country": "Peru", "lat": -12.0219, "lon": -77.1143},
    {"code": "BOG", "name": "El Dorado International Airport", "city": "Bogotá", "country": "Colombia", "lat": 4.7016, "lon": -74.1469},
    {"code": "MEX", "name": "Mexico City International Airport", "city": "Mexico City", "country": "Mexico", "lat": 19.4363, "lon": -99.0721},
    {"code": "CUN", "name": "Cancún International Airport", "city": "Cancún", "country": "Mexico", "lat": 21.0365, "lon": -86.8771},
    {"code": "PTY", "name": "Tocumen International Airport", "city": "Panama City", "country": "Panama", "lat": 9.0714, "lon": -79.3835},
    {"code": "SAN", "name": "San Diego International Airport", "city": "San Diego", "country": "USA", "lat": 32.7338, "lon": -117.1933},
    {"code": "OAK", "name": "Oakland International Airport", "city": "Oakland", "country": "USA", "lat": 37.7213, "lon": -122.2207},
    {"code": "IAH", "name": "George Bush Intercontinental Airport", "city": "Houston", "country": "USA", "lat": 29.9844, "lon": -95.3414},
    {"code": "MSY", "name": "Louis Armstrong New Orleans International Airport", "city": "New Orleans", "country": "USA", "lat": 29.9934, "lon": -90.2581},
    {"code": "MCI", "name": "Kansas City International Airport", "city": "Kansas City", "country": "USA", "lat": 39.2976, "lon": -94.7139},
    {"code": "OKC", "name": "Will Rogers World Airport", "city": "Oklahoma City", "country": "USA", "lat": 35.3931, "lon": -97.6007},
    {"code": "MEM", "name": "Memphis International Airport", "city": "Memphis", "country": "USA", "lat": 35.0424, "lon": -89.9767},
    {"code": "RIC", "name": "Richmond International Airport", "city": "Richmond", "country": "USA", "lat": 37.5052, "lon": -77.3197},
    {"code": "BUF", "name": "Buffalo Niagara International Airport", "city": "Buffalo", "country": "USA", "lat": 42.9405, "lon": -78.7322},
    {"code": "ROC", "name": "Greater Rochester International Airport", "city": "Rochester", "country": "USA", "lat": 43.1189, "lon": -77.6724},
    {"code": "HEL", "name": "Helsinki Airport", "city": "Helsinki", "country": "Finland", "lat": 59.3172, "lon": 24.9633},
    {"code": "OSL", "name": "Oslo Airport", "city": "Oslo", "country": "Norway", "lat": 60.1939, "lon": 11.1004},
    {"code": "BRU", "name": "Brussels Airport", "city": "Brussels", "country": "Belgium", "lat": 50.9014, "lon": 4.4844},
    {"code": "LIS", "name": "Humberto Delgado Airport", "city": "Lisbon", "country": "Portugal", "lat": 38.7813, "lon": -9.1359},
    {"code": "ATH", "name": "Athens International Airport", "city": "Athens", "country": "Greece", "lat": 37.9364, "lon": 23.9445},
    {"code": "PRG", "name": "Václav Havel Airport Prague", "city": "Prague", "country": "Czech Republic", "lat": 50.1008, "lon": 14.2638},
    {"code": "BUD", "name": "Budapest Ferenc Liszt International Airport", "city": "Budapest", "country": "Hungary", "lat": 47.4394, "lon": 19.2618},
    {"code": "WAW", "name": "Warsaw Chopin Airport", "city": "Warsaw", "country": "Poland", "lat": 52.1657, "lon": 20.9671},
    {"code": "KBP", "name": "Boryspil International Airport", "city": "Kyiv", "country": "Ukraine", "lat": 50.3450, "lon": 30.8947},
    {"code": "SVO", "name": "Sheremetyevo International Airport", "city": "Moscow", "country": "Russia", "lat": 55.9736, "lon": 37.4138},
    {"code": "KIX", "name": "Kansai International Airport", "city": "Osaka", "country": "Japan", "lat": 34.4342, "lon": 135.2442},
    {"code": "NKG", "name": "Nanjing Lukou International Airport", "city": "Nanjing", "country": "China", "lat": 31.7420, "lon": 118.8620},
    {"code": "CAN", "name": "Guangzhou Baiyun International Airport", "city": "Guangzhou", "country": "China", "lat": 23.3924, "lon": 113.2988},
    {"code": "TSA", "name": "Taipei Songshan Airport", "city": "Taipei", "country": "Taiwan", "lat": 25.0697, "lon": 121.5519},
    {"code": "CGK", "name": "Soekarno–Hatta International Airport", "city": "Jakarta", "country": "Indonesia", "lat": -6.1256, "lon": 106.6558},
    {"code": "DPS", "name": "Ngurah Rai International Airport", "city": "Denpasar", "country": "Indonesia", "lat": -8.7482, "lon": 115.1672},
    {"code": "HKT", "name": "Phuket International Airport", "city": "Phuket", "country": "Thailand", "lat": 8.1132, "lon": 98.3169},
    {"code": "REP", "name": "Siem Reap International Airport", "city": "Siem Reap", "country": "Cambodia", "lat": 13.4107, "lon": 103.8128},
    {"code": "HAN", "name": "Noi Bai International Airport", "city": "Hanoi", "country": "Vietnam", "lat": 21.2212, "lon": 105.8072},
    {"code": "SGN", "name": "Tan Son Nhat International Airport", "city": "Ho Chi Minh City", "country": "Vietnam", "lat": 10.8188, "lon": 106.6520}
]

# Airlines
AIRLINES = ["AA", "UA", "DL", "WN", "B6", "NK", "F9", "AS", "HA", "VX"]

def calculate_distance(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """Calculate distance between two points using Haversine formula"""
    import math
    
    R = 6371  # Earth's radius in kilometers
    
    lat1_rad = math.radians(lat1)
    lon1_rad = math.radians(lon1)
    lat2_rad = math.radians(lat2)
    lon2_rad = math.radians(lon2)
    
    dlat = lat2_rad - lat1_rad
    dlon = lon2_rad - lon1_rad
    
    a = math.sin(dlat/2)**2 + math.cos(lat1_rad) * math.cos(lat2_rad) * math.sin(dlon/2)**2
    c = 2 * math.asin(math.sqrt(a))
    
    return R * c

def calculate_flight_duration(distance_km: float) -> int:
    """Calculate flight duration in minutes based on distance"""
    # Average speed: 800 km/h for short flights, 900 km/h for long flights
    avg_speed = 850 if distance_km < 2000 else 900
    return int((distance_km / avg_speed) * 60)

def calculate_price(distance_km: float, is_international: bool) -> int:
    """Calculate price in cents based on distance and route type"""
    base_price = 50  # $0.50 per km
    if is_international:
        base_price *= 1.5  # 50% premium for international
    
    # Add some randomness
    price_variation = random.uniform(0.8, 1.2)
    return int(distance_km * base_price * price_variation)

def generate_flight_number(airline: str, flight_id: int) -> str:
    """Generate flight number"""
    return f"{airline}{flight_id:04d}"

def generate_flights():
    """Generate all flights for October 2025"""
    flights = []
    flight_id = 1
    
    # Set random seed for reproducibility
    random.seed(42)
    
    # Generate flights for each day in October 2025
    for day in range(1, 31):  # October 1-30, 2025
        date = datetime(2025, 10, day)
        
        # For each airport, create 3 departing flights
        for source_airport in AIRPORTS:
            # Get 5 random destination airports (excluding source)
            available_destinations = [a for a in AIRPORTS if a["code"] != source_airport["code"]]
            destinations = random.sample(available_destinations, min(5, len(available_destinations)))
            
            # Create 3 flights from this source
            for flight_num in range(3):
                dest_airport = destinations[flight_num % len(destinations)]
                
                # Calculate distance and duration
                distance = calculate_distance(
                    source_airport["lat"], source_airport["lon"],
                    dest_airport["lat"], dest_airport["lon"]
                )
                duration_minutes = calculate_flight_duration(distance)
                
                # Generate departure time (spread throughout the day)
                departure_hour = 6 + (flight_num * 6) + random.randint(0, 2)  # 6, 12, 18 + random
                departure_time = date.replace(hour=departure_hour % 24, minute=random.randint(0, 59))
                arrival_time = departure_time + timedelta(minutes=duration_minutes)
                
                # Calculate price
                is_international = source_airport["country"] != dest_airport["country"]
                price = calculate_price(distance, is_international)
                
                # Generate flight number
                airline = random.choice(AIRLINES)
                flight_no = generate_flight_number(airline, flight_id)
                
                flight = {
                    "id": flight_id,
                    "flight_no": flight_no,
                    "source": source_airport["code"],
                    "destination": dest_airport["code"],
                    "departure_time": departure_time,
                    "arrival_time": arrival_time,
                    "price_money": price,
                    "distance_km": round(distance, 2),
                    "duration_minutes": duration_minutes,
                    "is_international": is_international
                }
                
                flights.append(flight)
                flight_id += 1
    
    return flights

def write_neo4j_cypher(flights: List[Dict], filename: str):
    """Write Neo4j Cypher statements"""
    with open(filename, 'w') as f:
        f.write("// Generated FlightInstance nodes for Neo4j (October 1-30, 2025)\n")
        f.write("// Clear existing flight instances first\n")
        f.write("MATCH (f:FlightInstance) DETACH DELETE f;\n\n")
        
        for flight in flights:
            f.write(f"CREATE (f{flight['id']}:FlightInstance {{\n")
            f.write(f"  id: \"{flight['id']}\",\n")
            f.write(f"  flightNo: \"{flight['flight_no']}\",\n")
            f.write(f"  source: \"{flight['source']}\",\n")
            f.write(f"  destination: \"{flight['destination']}\",\n")
            f.write(f"  departureTime: datetime(\"{flight['departure_time'].strftime('%Y-%m-%dT%H:%M:%SZ')}\"),\n")
            f.write(f"  arrivalTime: datetime(\"{flight['arrival_time'].strftime('%Y-%m-%dT%H:%M:%SZ')}\"),\n")
            f.write(f"  priceMoney: {flight['price_money']},\n")
            f.write(f"  status: \"ACTIVE\",\n")
            f.write(f"  totalAvailableSeats: 50,\n")
            f.write(f"  totalSeats: 50,\n")
            f.write(f"  remainingSeats: 50,\n")
            f.write(f"  heldSeats: 0,\n")
            f.write(f"  lastUpdated: datetime(),\n")
            f.write(f"  reconciledAt: datetime()\n")
            f.write(f"}});\n\n")

def write_sql_inserts(flights: List[Dict], filename: str):
    """Write SQL INSERT statements"""
    with open(filename, 'w') as f:
        f.write("-- Generated FlightInstance records for PostgreSQL (October 1-30, 2025)\n")
        f.write("-- Clear existing data first\n")
        f.write("DELETE FROM seat WHERE flight_instance_id IN (SELECT id FROM flight_instance);\n")
        f.write("DELETE FROM flight_instance;\n\n")
        f.write("-- Reset sequence\n")
        f.write("ALTER SEQUENCE flight_instance_id_seq RESTART WITH 1;\n\n")
        f.write("-- Insert FlightInstance records\n")
        f.write("INSERT INTO flight_instance (flight_no, departure_time, arrival_time, source, destination, price_money, created_at, updated_at) VALUES\n")
        
        for i, flight in enumerate(flights):
            comma = "," if i < len(flights) - 1 else ";"
            f.write(f"('{flight['flight_no']}', '{flight['departure_time'].strftime('%Y-%m-%d %H:%M:%S+00')}', '{flight['arrival_time'].strftime('%Y-%m-%d %H:%M:%S+00')}', '{flight['source']}', '{flight['destination']}', {flight['price_money']}, NOW(), NOW()){comma}\n")

def write_relationships_cypher(filename: str):
    """Write relationship creation Cypher"""
    with open(filename, 'w') as f:
        f.write("// Create relationships between airports and flights\n")
        f.write("// This creates DEPARTS_WITH and ARRIVES_AT relationships\n\n")
        f.write("// Create DEPARTS_WITH relationships for all flights\n")
        f.write("MATCH (a:Airport), (f:FlightInstance {source: a.code})\n")
        f.write("CREATE (a)-[:DEPARTS_WITH {departure_time: f.departureTime, created_at: datetime()}]->(f);\n\n")
        f.write("// Create ARRIVES_AT relationships for all flights\n")
        f.write("MATCH (a:Airport), (f:FlightInstance {destination: a.code})\n")
        f.write("CREATE (f)-[:ARRIVES_AT {arrival_time: f.arrivalTime, created_at: datetime()}]->(a);\n")

def write_seats_sql(filename: str, num_flights: int):
    """Write seat generation SQL"""
    with open(filename, 'w') as f:
        f.write("-- Generate 50 seats for each flight instance\n")
        f.write("-- Clear existing seat data first\n")
        f.write("DELETE FROM seat;\n\n")
        f.write("-- Reset sequence\n")
        f.write("ALTER SEQUENCE seat_id_seq RESTART WITH 1;\n\n")
        
        f.write("-- Insert seats for all flights\n")
        f.write("INSERT INTO seat (flight_instance_id, seat_no, cabin_class, is_available, version)\n")
        f.write("SELECT \n")
        f.write("    f.id as flight_instance_id,\n")
        f.write("    CONCAT(\n")
        f.write("        CASE \n")
        f.write("            WHEN seat_num <= 10 THEN LPAD(seat_num::text, 2, '0')\n")
        f.write("            ELSE LPAD(seat_num::text, 2, '0')\n")
        f.write("        END,\n")
        f.write("        CASE \n")
        f.write("            WHEN seat_num % 5 = 1 THEN 'A'\n")
        f.write("            WHEN seat_num % 5 = 2 THEN 'B'\n")
        f.write("            WHEN seat_num % 5 = 3 THEN 'C'\n")
        f.write("            WHEN seat_num % 5 = 4 THEN 'D'\n")
        f.write("            ELSE 'E'\n")
        f.write("        END\n")
        f.write("    ) as seat_no,\n")
        f.write("    CASE \n")
        f.write("        WHEN seat_num <= 10 THEN 'First'\n")
        f.write("        WHEN seat_num <= 30 THEN 'Business'\n")
        f.write("        ELSE 'Economy'\n")
        f.write("    END as cabin_class,\n")
        f.write("    true as is_available,\n")
        f.write("    0 as version\n")
        f.write("FROM flight_instance f\n")
        f.write("CROSS JOIN generate_series(1, 50) as seat_num\n")
        f.write("ORDER BY f.id, seat_num;\n")

def main():
    """Main function to generate all flight data"""
    print("Generating 9,000 flights for October 2025...")
    
    # Generate flights
    flights = generate_flights()
    print(f"Generated {len(flights)} flights")
    
    # Write Neo4j Cypher file
    print("Writing Neo4j Cypher file...")
    write_neo4j_cypher(flights, "generated-flights-neo4j.cypher")
    
    # Write SQL INSERT file
    print("Writing SQL INSERT file...")
    write_sql_inserts(flights, "generated-flights-sql.sql")
    
    # Write relationships file
    print("Writing relationships file...")
    write_relationships_cypher("generated-flight-relationships.cypher")
    
    # Write seats generation SQL
    print("Writing seats generation SQL...")
    write_seats_sql("generated-seats-sql.sql", len(flights))
    
    # Write summary JSON
    with open("flight-generation-summary.json", 'w') as f:
        json.dump({
            "total_flights": len(flights),
            "airports_count": len(AIRPORTS),
            "date_range": "2025-10-01 to 2025-10-30",
            "flights_per_airport_per_day": 3,
            "destinations_per_airport": 5,
            "total_seats": len(flights) * 50,
            "files_generated": [
                "generated-flights-neo4j.cypher",
                "generated-flights-sql.sql", 
                "generated-flight-relationships.cypher",
                "generated-seats-sql.sql"
            ]
        }, f, indent=2)
    
    print("✅ Flight generation complete!")
    print(f"📁 Generated files:")
    print(f"   - generated-flights-neo4j.cypher ({len(flights)} flights)")
    print(f"   - generated-flights-sql.sql ({len(flights)} flights)")
    print(f"   - generated-flight-relationships.cypher")
    print(f"   - generated-seats-sql.sql ({len(flights) * 50} seats)")
    print(f"   - flight-generation-summary.json")

if __name__ == "__main__":
    main()
