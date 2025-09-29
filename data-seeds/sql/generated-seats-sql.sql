-- Generate 50 seats for each flight instance
-- Clear existing seat data first
DELETE FROM seat;

-- Reset sequence
ALTER SEQUENCE seat_id_seq RESTART WITH 1;

-- Insert seats for all flights
INSERT INTO seat (flight_instance_id, seat_no, cabin_class, is_available, version)
SELECT 
    f.id as flight_instance_id,
    CONCAT(
        CASE 
            WHEN seat_num <= 10 THEN LPAD(seat_num::text, 2, '0')
            ELSE LPAD(seat_num::text, 2, '0')
        END,
        CASE 
            WHEN seat_num % 5 = 1 THEN 'A'
            WHEN seat_num % 5 = 2 THEN 'B'
            WHEN seat_num % 5 = 3 THEN 'C'
            WHEN seat_num % 5 = 4 THEN 'D'
            ELSE 'E'
        END
    ) as seat_no,
    CASE 
        WHEN seat_num <= 10 THEN 'First'
        WHEN seat_num <= 30 THEN 'Business'
        ELSE 'Economy'
    END as cabin_class,
    true as is_available,
    0 as version
FROM flight_instance f
CROSS JOIN generate_series(1, 50) as seat_num
ORDER BY f.id, seat_num;
