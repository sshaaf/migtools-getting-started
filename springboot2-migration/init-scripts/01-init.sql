-- Database initialization script for Spring Boot 2 Migration example

-- Create users table if it doesn't exist
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO users (name, email, active) VALUES 
    ('John Doe', 'john.doe@example.com', true),
    ('Jane Smith', 'jane.smith@example.com', true),
    ('Bob Johnson', 'bob.johnson@example.com', false),
    ('Alice Brown', 'alice.brown@example.com', true),
    ('Charlie Wilson', 'charlie.wilson@example.com', true)
ON CONFLICT (email) DO NOTHING;

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(active);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
