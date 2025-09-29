-- Sample Flight Instances for Flight Booking System
-- These represent flights for September 29-30, 2025

-- Clear existing data
DELETE FROM ticket_seat;
DELETE FROM ticket;
DELETE FROM seat_hold;
DELETE FROM seat;
DELETE FROM flight_instance;

-- Insert sample flight instances for September 29, 2025
INSERT INTO flight_instance (id, flight_no, departure_time, arrival_time, source, destination, price_money, created_at) VALUES
-- Direct flights JFK to LAX (September 29, 2025)
(1, 'AA100', '2025-09-29 08:00:00+00', '2025-09-29 11:00:00+00', 'JFK', 'LAX', 29900, NOW()),
(2, 'UA200', '2025-09-29 14:00:00+00', '2025-09-29 17:00:00+00', 'JFK', 'LAX', 32900, NOW()),
(3, 'DL300', '2025-09-29 20:00:00+00', '2025-09-29 23:00:00+00', 'JFK', 'LAX', 27900, NOW()),

-- Direct flights LAX to JFK (September 29, 2025)
(4, 'AA101', '2025-09-29 09:00:00+00', '2025-09-29 18:00:00+00', 'LAX', 'JFK', 29900, NOW()),
(5, 'UA201', '2025-09-29 15:00:00+00', '2025-09-30 00:00:00+00', 'LAX', 'JFK', 32900, NOW()),

-- JFK to ORD (for connections) - September 29, 2025
(6, 'AA102', '2025-09-29 07:00:00+00', '2025-09-29 09:00:00+00', 'JFK', 'ORD', 19900, NOW()),
(7, 'UA202', '2025-09-29 13:00:00+00', '2025-09-29 15:00:00+00', 'JFK', 'ORD', 17900, NOW()),

-- ORD to LAX (for connections) - September 29, 2025
(8, 'AA103', '2025-09-29 10:00:00+00', '2025-09-29 12:00:00+00', 'ORD', 'LAX', 24900, NOW()),
(9, 'UA203', '2025-09-29 16:00:00+00', '2025-09-29 18:00:00+00', 'ORD', 'LAX', 22900, NOW()),

-- JFK to DFW (for connections) - September 29, 2025
(10, 'AA104', '2025-09-29 06:00:00+00', '2025-09-29 09:00:00+00', 'JFK', 'DFW', 21900, NOW()),

-- DFW to LAX (for connections) - September 29, 2025
(11, 'AA105', '2025-09-29 11:00:00+00', '2025-09-29 13:00:00+00', 'DFW', 'LAX', 19900, NOW()),

-- Additional flights for variety - September 29, 2025
(12, 'DL301', '2025-09-29 12:00:00+00', '2025-09-29 14:00:00+00', 'ATL', 'LAX', 25900, NOW()),
(13, 'DL302', '2025-09-29 18:00:00+00', '2025-09-29 20:00:00+00', 'LAX', 'ATL', 25900, NOW()),

-- DEN connections - September 29, 2025
(14, 'UA204', '2025-09-29 08:00:00+00', '2025-09-29 10:00:00+00', 'DEN', 'LAX', 20900, NOW()),
(15, 'UA205', '2025-09-29 14:00:00+00', '2025-09-29 16:00:00+00', 'LAX', 'DEN', 20900, NOW()),

-- September 30, 2025 flights
-- Direct flights JFK to LAX (September 30, 2025)
(16, 'AA106', '2025-09-30 08:00:00+00', '2025-09-30 11:00:00+00', 'JFK', 'LAX', 29900, NOW()),
(17, 'UA206', '2025-09-30 14:00:00+00', '2025-09-30 17:00:00+00', 'JFK', 'LAX', 32900, NOW()),
(18, 'DL306', '2025-09-30 20:00:00+00', '2025-09-30 23:00:00+00', 'JFK', 'LAX', 27900, NOW()),

-- Direct flights LAX to JFK (September 30, 2025)
(19, 'AA107', '2025-09-30 09:00:00+00', '2025-09-30 18:00:00+00', 'LAX', 'JFK', 29900, NOW()),
(20, 'UA207', '2025-09-30 15:00:00+00', '2025-10-01 00:00:00+00', 'LAX', 'JFK', 32900, NOW()),

-- JFK to ORD (for connections) - September 30, 2025
(21, 'AA108', '2025-09-30 07:00:00+00', '2025-09-30 09:00:00+00', 'JFK', 'ORD', 19900, NOW()),
(22, 'UA208', '2025-09-30 13:00:00+00', '2025-09-30 15:00:00+00', 'JFK', 'ORD', 17900, NOW()),

-- ORD to LAX (for connections) - September 30, 2025
(23, 'AA109', '2025-09-30 10:00:00+00', '2025-09-30 12:00:00+00', 'ORD', 'LAX', 24900, NOW()),
(24, 'UA209', '2025-09-30 16:00:00+00', '2025-09-30 18:00:00+00', 'ORD', 'LAX', 22900, NOW()),

-- JFK to DFW (for connections) - September 30, 2025
(25, 'AA110', '2025-09-30 06:00:00+00', '2025-09-30 09:00:00+00', 'JFK', 'DFW', 21900, NOW()),

-- DFW to LAX (for connections) - September 30, 2025
(26, 'AA111', '2025-09-30 11:00:00+00', '2025-09-30 13:00:00+00', 'DFW', 'LAX', 19900, NOW()),

-- Additional flights for variety - September 30, 2025
(27, 'DL307', '2025-09-30 12:00:00+00', '2025-09-30 14:00:00+00', 'ATL', 'LAX', 25900, NOW()),
(28, 'DL308', '2025-09-30 18:00:00+00', '2025-09-30 20:00:00+00', 'LAX', 'ATL', 25900, NOW()),

-- DEN connections - September 30, 2025
(29, 'UA210', '2025-09-30 08:00:00+00', '2025-09-30 10:00:00+00', 'DEN', 'LAX', 20900, NOW()),
(30, 'UA211', '2025-09-30 14:00:00+00', '2025-09-30 16:00:00+00', 'LAX', 'DEN', 20900, NOW());

-- Insert seats for each flight (50 seats per flight)
INSERT INTO seat (flight_instance_id, seat_no, cabin_class, is_available, version) 
SELECT 
    f.id,
    'A' || LPAD(seat_num::text, 2, '0'),
    CASE WHEN seat_num <= 10 THEN 'FIRST' 
         WHEN seat_num <= 30 THEN 'BUSINESS' 
         ELSE 'ECONOMY' END,
    true,
    0
FROM flight_instance f
CROSS JOIN generate_series(1, 50) AS seat_num;

-- Update sequence to avoid conflicts
SELECT setval('flight_instance_id_seq', (SELECT MAX(id) FROM flight_instance));
SELECT setval('seat_id_seq', (SELECT MAX(id) FROM seat));
