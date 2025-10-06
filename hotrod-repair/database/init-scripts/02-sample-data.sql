-- Hot Rods Car Repair Sample Data
-- Insert sample customers, vehicles, and service orders

SET search_path TO hotrods;

-- Insert sample customers
INSERT INTO customers (first_name, last_name, email, phone_number, address, city, state, zip_code, preferred_contact_method, customer_notes) VALUES
('Jake', 'Thunder', 'jake.thunder@email.com', '(555) 123-4567', '123 Speed Street', 'Motor City', 'MC', '48201', 'PHONE', 'Owns a 1969 Dodge Charger R/T. Prefers original parts.'),
('Sarah', 'Lightning', 'sarah.lightning@email.com', '(555) 234-5678', '456 Burnout Boulevard', 'Drag Strip', 'DS', '90210', 'EMAIL', 'Drag racer with multiple vehicles. Quick turnaround needed.'),
('Mike', 'Torque', 'mike.torque@email.com', '(555) 345-6789', '789 Horsepower Highway', 'Muscle Town', 'MT', '12345', 'TEXT', 'Classic muscle car collector. Budget-conscious.'),
('Lisa', 'Nitrous', 'lisa.nitrous@email.com', '(555) 456-7890', '321 Quarter Mile Road', 'Race City', 'RC', '54321', 'EMAIL', 'Street racing enthusiast. Needs performance upgrades.'),
('Tony', 'Gearhead', 'tony.gearhead@email.com', '(555) 567-8901', '654 Engine Block Lane', 'Piston Valley', 'PV', '67890', 'PHONE', 'Professional mechanic. Brings difficult restoration projects.'),
('Carmen', 'Redline', 'carmen.redline@email.com', '(555) 678-9012', '987 Turbo Trail', 'Boost City', 'BC', '98765', 'EMAIL', 'Import tuner specialist. Prefers aftermarket parts.'),
('Rex', 'Throttle', 'rex.throttle@email.com', '(555) 789-0123', '147 Supercharger Street', 'Power Town', 'PT', '13579', 'PHONE', 'Owns vintage hot rods. Participates in car shows.'),
('Vicky', 'Velocity', 'vicky.velocity@email.com', '(555) 890-1234', '258 Camshaft Circle', 'Rev City', 'RC', '24680', 'TEXT', 'Track day regular. Needs frequent brake and tire service.');

-- Insert sample vehicles
INSERT INTO vehicles (customer_id, vin_number, make, model, year, color, engine_type, transmission, mileage, license_plate, vehicle_notes) VALUES
(1, '1B3CC5FB2AN123456', 'Dodge', 'Charger R/T', 1969, 'Plum Crazy Purple', '440 Six Pack', '4-Speed Manual', 45000, 'THUNDER1', 'Numbers matching, original paint. Garage kept.'),
(1, '1G1YY26E365123457', 'Chevrolet', 'Corvette', 2006, 'Victory Red', 'LS2 6.0L V8', '6-Speed Manual', 28000, 'THUNDER2', 'Weekend driver, excellent condition.'),
(2, '1FTFW1ET0DFC12345', 'Ford', 'Mustang GT', 2013, 'Race Red', '5.0L Coyote V8', '6-Speed Manual', 15000, 'FAST1', 'Built for drag racing, roll cage installed.'),
(2, '1G1FB1RX0E0123456', 'Chevrolet', 'Camaro SS', 2014, 'Summit White', '6.2L LS3 V8', '6-Speed Manual', 22000, 'FAST2', 'Street/strip setup, nitrous ready.'),
(3, '1G1YY07S475123458', 'Chevrolet', 'Corvette', 2007, 'Atomic Orange', 'LS2 6.0L V8', 'Automatic', 35000, 'TORQUE1', 'Show car quality, concours restoration.'),
(3, '1FAFP40414F123459', 'Ford', 'Mustang Boss', 2014, 'School Bus Yellow', '5.0L V8', '6-Speed Manual', 8000, 'TORQUE2', 'Limited edition, track package.'),
(4, '1G1YY26E975123460', 'Chevrolet', 'Corvette Z06', 1997, 'Torch Red', 'LT4 5.7L V8', '6-Speed Manual', 18000, 'NITRO1', 'Modified for autocross, suspension upgrades.'),
(5, '1FTFW1ET5DFC12346', 'Ford', 'F-150 Lightning', 1993, 'Bright Red', '5.8L V8', '4-Speed Auto', 89000, 'GEAR1', 'Rare sport truck, original condition.'),
(6, '1HGBH41JXMN123461', 'Honda', 'Civic Type R', 2021, 'Championship White', '2.0L Turbo', '6-Speed Manual', 5000, 'BOOST1', 'Track-focused, aero package.'),
(7, '1G1YY07S445123462', 'Chevrolet', 'Corvette', 2004, 'Le Mans Blue', 'LS1 5.7L V8', '6-Speed Manual', 52000, 'HOTROD1', 'C5 generation, heads and cam upgrade.'),
(8, '1FA6P8CF6F5123463', 'Ford', 'Mustang GT350', 2015, 'Oxford White', '5.2L Voodoo V8', '6-Speed Manual', 12000, 'TRACK1', 'Track package, carbon fiber wheels.');

-- Insert sample service orders
INSERT INTO service_orders (customer_id, vehicle_id, order_number, service_description, status, priority, estimated_cost, mechanic_notes) VALUES
(1, 1, 'HR-2024-001', 'Complete carburetor rebuild and tune-up for 440 Six Pack engine', 'COMPLETED', 'HIGH', 850.00, 'Rebuilt Holley 4160, adjusted timing, new plugs and wires'),
(1, 2, 'HR-2024-002', 'Brake pad replacement and fluid flush', 'COMPLETED', 'NORMAL', 320.00, 'Installed performance pads, DOT 4 brake fluid'),
(2, 3, 'HR-2024-003', 'Nitrous system installation and tuning', 'IN_PROGRESS', 'HIGH', 1200.00, 'Installing 100-shot wet system, tuning in progress'),
(2, 4, 'HR-2024-004', 'Transmission service and clutch adjustment', 'SCHEDULED', 'NORMAL', 180.00, NULL),
(3, 5, 'HR-2024-005', 'Complete paint correction and ceramic coating', 'COMPLETED', 'LOW', 950.00, 'Show-quality finish achieved, 5-year coating applied'),
(4, 7, 'HR-2024-006', 'Suspension upgrade - coilovers and sway bars', 'WAITING_PARTS', 'HIGH', 1800.00, 'Waiting for custom coilover delivery'),
(5, 8, 'HR-2024-007', 'Engine rebuild - pistons, rings, and bearings', 'IN_PROGRESS', 'URGENT', 3500.00, '5.8L rebuild in progress, machining complete'),
(6, 9, 'HR-2024-008', 'Turbo upgrade and ECU tune', 'SCHEDULED', 'HIGH', 2200.00, NULL),
(7, 10, 'HR-2024-009', 'Heads and cam installation', 'COMPLETED', 'NORMAL', 1650.00, 'LS1 performance upgrade completed, dyno tuned'),
(8, 11, 'HR-2024-010', 'Track day preparation and safety inspection', 'SCHEDULED', 'NORMAL', 450.00, NULL);

-- Insert sample service items
INSERT INTO service_items (service_order_id, item_description, item_type, quantity, unit_price, total_price, part_number, labor_hours) VALUES
-- Service Order 1 items
(1, 'Holley 4160 Carburetor Rebuild Kit', 'PART', 1, 125.00, 125.00, 'HOL-37-1539', NULL),
(1, 'Carburetor Rebuild Labor', 'LABOR', 1, 450.00, 450.00, NULL, 6.0),
(1, 'Champion Spark Plugs (Set of 8)', 'PART', 1, 45.00, 45.00, 'CHA-RJ19LM', NULL),
(1, 'MSD Spark Plug Wires', 'PART', 1, 89.00, 89.00, 'MSD-31199', NULL),
(1, 'Engine Tune-Up Labor', 'LABOR', 1, 141.00, 141.00, NULL, 1.5),

-- Service Order 2 items
(2, 'Performance Brake Pads (Front)', 'PART', 1, 120.00, 120.00, 'HAW-HB119', NULL),
(2, 'DOT 4 Brake Fluid', 'FLUID', 2, 15.00, 30.00, 'VAL-MaxLife', NULL),
(2, 'Brake Service Labor', 'LABOR', 1, 170.00, 170.00, NULL, 2.0),

-- Service Order 3 items
(3, 'NOS Wet Nitrous Kit 100HP', 'PART', 1, 650.00, 650.00, 'NOS-05164NOS', NULL),
(3, 'Nitrous Installation Labor', 'LABOR', 1, 400.00, 400.00, NULL, 5.0),
(3, 'ECU Tuning Service', 'SERVICE', 1, 150.00, 150.00, NULL, 2.0),

-- Service Order 5 items
(5, 'Paint Correction Service', 'SERVICE', 1, 500.00, 500.00, NULL, 8.0),
(5, 'Ceramic Coating Application', 'SERVICE', 1, 450.00, 450.00, 'CER-COAT-PRO', 6.0),

-- Service Order 7 items
(7, 'Forged Piston Set', 'PART', 1, 800.00, 800.00, 'SRP-138070', NULL),
(7, 'Engine Bearing Set', 'PART', 1, 180.00, 180.00, 'KIN-MS909P', NULL),
(7, 'Piston Ring Set', 'PART', 1, 220.00, 220.00, 'TOT-CR8194-030', NULL),
(7, 'Engine Rebuild Labor', 'LABOR', 1, 2300.00, 2300.00, NULL, 30.0),

-- Service Order 9 items
(9, 'LS1 Performance Heads (Pair)', 'PART', 1, 950.00, 950.00, 'AFR-1121', NULL),
(9, 'Performance Camshaft', 'PART', 1, 320.00, 320.00, 'COMP-12-602-4', NULL),
(9, 'Head and Cam Installation', 'LABOR', 1, 380.00, 380.00, NULL, 8.0);

-- Update service order totals based on items
UPDATE service_orders SET 
    estimated_cost = (
        SELECT SUM(total_price) 
        FROM service_items 
        WHERE service_items.service_order_id = service_orders.service_order_id
    ),
    actual_cost = (
        SELECT SUM(total_price) 
        FROM service_items 
        WHERE service_items.service_order_id = service_orders.service_order_id
    )
WHERE service_order_id IN (1, 2, 3, 5, 7, 9);

-- Set completion dates for completed orders
UPDATE service_orders SET 
    actual_completion = CURRENT_TIMESTAMP - INTERVAL '5 days'
WHERE status = 'COMPLETED' AND service_order_id IN (1, 2, 5, 9);

-- Update customer last visit dates
UPDATE customers SET 
    last_visit_date = (
        SELECT MAX(service_date) 
        FROM service_orders 
        WHERE service_orders.customer_id = customers.customer_id
    );

-- Create some test data for reports
INSERT INTO service_orders (customer_id, vehicle_id, order_number, service_date, service_description, status, actual_cost, actual_completion) VALUES
(1, 1, 'HR-2023-045', '2023-12-15 10:00:00', 'Winter storage prep and oil change', 'COMPLETED', 125.00, '2023-12-15 14:30:00'),
(2, 3, 'HR-2023-046', '2023-11-20 09:00:00', 'Pre-season race prep and inspection', 'COMPLETED', 380.00, '2023-11-20 16:00:00'),
(3, 5, 'HR-2023-047', '2023-10-10 11:00:00', 'Show preparation detailing', 'COMPLETED', 275.00, '2023-10-10 17:00:00');
