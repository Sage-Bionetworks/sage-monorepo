-- Insert default admin user
-- Password is 'admin123' hashed with BCrypt (strength 12)
-- You should change this password in production
INSERT INTO app_user (username, password_hash, role) VALUES
('admin', '$2a$12$0vDbrdL7o3enKVmjlIJGVeZC8obDRxUGcf76D3DowTo3gVt1wUluu', 'admin');

-- Insert default researcher user
-- Password is 'researcher123' hashed with BCrypt (strength 12)
-- You should change this password in production
INSERT INTO app_user (username, password_hash, role) VALUES
('researcher', '$2a$12$2wGLgcl3NnRDJFNod89oqOrG1PwK1JT1QlD6b9SW8wcR30ZqQiG6K', 'user');

-- Note: These are placeholder BCrypt hashes. In a real implementation,
-- you would either:
-- 1. Generate these hashes programmatically in the application startup
-- 2. Use environment variables with pre-hashed passwords
-- 3. Use a proper user management system
