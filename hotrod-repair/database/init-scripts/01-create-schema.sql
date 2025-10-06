-- Hot Rods Car Repair Database Initialization Script
-- This script creates the database schema and sample data

-- Create database schema
CREATE SCHEMA IF NOT EXISTS hotrods;
SET search_path TO hotrods;

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(200),
    city VARCHAR(50),
    state VARCHAR(20),
    zip_code VARCHAR(10),
    registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_visit_date TIMESTAMP,
    customer_notes TEXT,
    preferred_contact_method VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT true
);

-- Create vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE,
    vin_number VARCHAR(17) UNIQUE NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    color VARCHAR(30),
    engine_type VARCHAR(50),
    transmission VARCHAR(30),
    mileage INTEGER,
    license_plate VARCHAR(20),
    purchase_date DATE,
    registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vehicle_notes TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true
);

-- Create service_orders table
CREATE TABLE IF NOT EXISTS service_orders (
    service_order_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(customer_id),
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(vehicle_id),
    order_number VARCHAR(20) UNIQUE NOT NULL,
    service_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estimated_completion TIMESTAMP,
    actual_completion TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    priority VARCHAR(10) NOT NULL DEFAULT 'NORMAL',
    service_description TEXT NOT NULL,
    estimated_cost DECIMAL(10,2),
    actual_cost DECIMAL(10,2),
    mechanic_notes TEXT,
    customer_notes TEXT,
    warranty_months INTEGER,
    is_warranty_work BOOLEAN NOT NULL DEFAULT false
);

-- Create service_items table
CREATE TABLE IF NOT EXISTS service_items (
    service_item_id BIGSERIAL PRIMARY KEY,
    service_order_id BIGINT NOT NULL REFERENCES service_orders(service_order_id) ON DELETE CASCADE,
    item_description VARCHAR(100) NOT NULL,
    item_type VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2),
    total_price DECIMAL(10,2),
    part_number VARCHAR(50),
    labor_hours DECIMAL(4,2),
    item_notes TEXT
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_phone ON customers(phone_number);
CREATE INDEX IF NOT EXISTS idx_customers_active ON customers(is_active);
CREATE INDEX IF NOT EXISTS idx_vehicles_vin ON vehicles(vin_number);
CREATE INDEX IF NOT EXISTS idx_vehicles_customer ON vehicles(customer_id);
CREATE INDEX IF NOT EXISTS idx_service_orders_customer ON service_orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_service_orders_vehicle ON service_orders(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_service_orders_status ON service_orders(status);
CREATE INDEX IF NOT EXISTS idx_service_orders_date ON service_orders(service_date);
CREATE INDEX IF NOT EXISTS idx_service_items_order ON service_items(service_order_id);
