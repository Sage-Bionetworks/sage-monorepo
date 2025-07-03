-- Insert default admin user
-- Password is 'admin123' hashed with BCrypt (strength 12)
-- You should change this password in production
INSERT INTO user (username, password_hash, role) VALUES
('admin', '$2a$12$8YU4YgK1XZp1YxXhK4K1JuO8TzUwN3CJ9z8QJtZ3K9YgT2L1N8J9.', 'admin');

-- Insert default researcher user
-- Password is 'researcher123' hashed with BCrypt (strength 12)
-- You should change this password in production
INSERT INTO user (username, password_hash, role) VALUES
('researcher', '$2a$12$9ZV5ZhL2YAq2ZyYiL5L2KvP9UaVxO4DK0a9RKuA4L0ZhU3M2O9K0.', 'user');

-- Note: These are placeholder BCrypt hashes. In a real implementation,
-- you would either:
-- 1. Generate these hashes programmatically in the application startup
-- 2. Use environment variables with pre-hashed passwords
-- 3. Use a proper user management system
