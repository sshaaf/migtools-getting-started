-- Initial data for Spring Boot to Quarkus Migration Example
-- This file demonstrates Spring Boot data initialization patterns

-- Insert roles
INSERT INTO roles (name, description, active) VALUES 
('ADMIN', 'Administrator role with full access', true),
('USER', 'Regular user role with limited access', true),
('MANAGER', 'Manager role with moderate access', true);

-- Insert sample users (passwords are BCrypt encoded)
-- admin:admin123, user:user123, manager:manager123
INSERT INTO users (username, email, first_name, last_name, password, active, bio, created_at, updated_at) VALUES 
('admin', 'admin@example.com', 'Admin', 'User', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true, 'System administrator with full privileges', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user', 'user@example.com', 'Regular', 'User', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true, 'Regular user for testing purposes', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('manager', 'manager@example.com', 'Manager', 'User', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true, 'Manager with moderate privileges', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('john.doe', 'john.doe@example.com', 'John', 'Doe', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true, 'Software developer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('jane.smith', 'jane.smith@example.com', 'Jane', 'Smith', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true, 'Product manager', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), -- admin has ADMIN role
(1, 2), -- admin has USER role
(2, 2), -- user has USER role
(3, 3), -- manager has MANAGER role
(3, 2), -- manager has USER role
(4, 2), -- john.doe has USER role
(5, 2); -- jane.smith has USER role

-- Insert sample products
INSERT INTO products (name, description, price, category, stock_quantity, available, image_url, created_at, updated_at) VALUES 
('Laptop Computer', 'High-performance laptop for development work', 1299.99, 'Electronics', 50, true, 'https://example.com/laptop.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Wireless Mouse', 'Ergonomic wireless mouse with precision tracking', 29.99, 'Electronics', 100, true, 'https://example.com/mouse.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Coffee Mug', 'Ceramic coffee mug with company logo', 12.99, 'Office Supplies', 200, true, 'https://example.com/mug.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Standing Desk', 'Adjustable height standing desk', 599.99, 'Furniture', 25, true, 'https://example.com/desk.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Programming Book', 'Comprehensive guide to modern programming', 49.99, 'Books', 75, true, 'https://example.com/book.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mechanical Keyboard', 'RGB mechanical keyboard for gaming and coding', 149.99, 'Electronics', 30, true, 'https://example.com/keyboard.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Monitor', '27-inch 4K monitor for development', 399.99, 'Electronics', 40, true, 'https://example.com/monitor.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Notebook', 'Lined notebook for taking notes', 8.99, 'Office Supplies', 150, true, 'https://example.com/notebook.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample orders
INSERT INTO orders (order_number, user_id, status, total_amount, shipping_address, notes, created_at, updated_at) VALUES 
('ORD-001', 2, 'DELIVERED', 1329.98, '123 Main St, Anytown, USA 12345', 'First order for laptop and mouse', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ORD-002', 4, 'SHIPPED', 649.98, '456 Oak Ave, Somewhere, USA 67890', 'Standing desk order', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ORD-003', 5, 'PROCESSING', 199.97, '789 Pine Rd, Elsewhere, USA 54321', 'Keyboard and book bundle', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ORD-004', 2, 'PENDING', 408.98, '123 Main St, Anytown, USA 12345', 'Monitor and notebook order', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert order items
INSERT INTO order_items (order_id, product_id, quantity, price, notes) VALUES 
-- Order 1 items
(1, 1, 1, 1299.99, 'Latest model laptop'),
(1, 2, 1, 29.99, 'Matching wireless mouse'),
-- Order 2 items
(2, 4, 1, 599.99, 'White standing desk'),
(2, 3, 1, 12.99, 'Coffee mug'),
(2, 8, 3, 8.99, 'Extra notebooks'),
-- Order 3 items
(3, 6, 1, 149.99, 'Blue switch keyboard'),
(3, 5, 1, 49.99, 'Programming reference'),
-- Order 4 items
(4, 7, 1, 399.99, '4K resolution monitor'),
(4, 8, 1, 8.99, 'Meeting notes notebook');

-- Insert user favorite products
INSERT INTO user_favorite_products (user_id, product_id) VALUES 
(2, 1), -- user likes laptop
(2, 2), -- user likes mouse
(2, 7), -- user likes monitor
(4, 6), -- john.doe likes keyboard
(4, 5), -- john.doe likes programming book
(4, 1), -- john.doe likes laptop
(5, 4), -- jane.smith likes standing desk
(5, 3), -- jane.smith likes coffee mug
(5, 8); -- jane.smith likes notebook


