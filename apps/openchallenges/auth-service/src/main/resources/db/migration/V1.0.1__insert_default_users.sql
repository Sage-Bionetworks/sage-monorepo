-- Insert default admin user
-- Password is 'changeme' hashed with BCrypt (strength 12)
-- You should change this password in production
INSERT INTO app_user (username, password_hash, role, created_at, updated_at) VALUES
('admin', '$2a$12$lWQA8qj1Pp9NfAsWY53rQuK/uChV.EJ1RTxhisDFuV0uHrJFm0/J6', 'admin', NOW(), NOW());

-- Insert default researcher user
-- Password is 'changeme' hashed with BCrypt (strength 12)
-- You should change this password in production
INSERT INTO app_user (username, password_hash, role, created_at, updated_at) VALUES
('researcher', '$2a$12$lWQA8qj1Pp9NfAsWY53rQuK/uChV.EJ1RTxhisDFuV0uHrJFm0/J6', 'user', NOW(), NOW());

-- Insert default challenge service user
-- Password is 'changeme' hashed with BCrypt (strength 12)
-- You should change this password in production
INSERT INTO app_user (username, password_hash, role, created_at, updated_at) VALUES
('challenge-service', '$2a$12$lWQA8qj1Pp9NfAsWY53rQuK/uChV.EJ1RTxhisDFuV0uHrJFm0/J6', 'service', NOW(), NOW());

-- Note: These are placeholder BCrypt hashes. In a real implementation,
-- you would either:
-- 1. Generate these hashes programmatically in the application startup
-- 2. Use environment variables with pre-hashed passwords
-- 3. Use a proper user management system
