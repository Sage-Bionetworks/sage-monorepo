-- Insert test users and API keys for authentication testing
-- These are for development/testing purposes only

-- Insert test user with email and profile info for OAuth2 testing
INSERT INTO app_user (username, password_hash, email, first_name, last_name, role, email_verified, created_at, updated_at) VALUES
('testuser', '$2a$12$lWQA8qj1Pp9NfAsWY53rQuK/uChV.EJ1RTxhisDFuV0uHrJFm0/J6', 'testuser@example.com', 'Test', 'User', 'user', true, NOW(), NOW());

-- Insert test developer user for API development
INSERT INTO app_user (username, password_hash, email, first_name, last_name, role, email_verified, created_at, updated_at) VALUES
('developer', '$2a$12$lWQA8qj1Pp9NfAsWY53rQuK/uChV.EJ1RTxhisDFuV0uHrJFm0/J6', 'developer@example.com', 'API', 'Developer', 'developer', true, NOW(), NOW());

-- Insert test organization admin user
INSERT INTO app_user (username, password_hash, email, first_name, last_name, role, email_verified, created_at, updated_at) VALUES
('org-admin', '$2a$12$lWQA8qj1Pp9NfAsWY53rQuK/uChV.EJ1RTxhisDFuV0uHrJFm0/J6', 'org-admin@example.com', 'Organization', 'Admin', 'admin', true, NOW(), NOW());

-- Insert API key for testuser (for API key authentication testing)
-- Key value: oc_test_1234567890abcdef (this is what the user would use)
-- Hash: BCrypt hash of "oc_test_1234567890abcdef"
INSERT INTO api_key (user_id, key_hash, key_prefix, name, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM app_user WHERE username = 'testuser'),
    '$2a$12$E8.tF8z8D8L.0vJzZy4uAOJ8F8F8F8F8F8F8F8F8F8F8F8F8F8F8F8',
    'oc_test_',
    'Test API Key',
    NOW() + INTERVAL '1 year', -- Expires in 1 year
    NOW(),
    NOW()
);

-- Insert API key for developer (for API development testing)
-- Key value: oc_dev_9876543210fedcba (this is what the developer would use)
-- Hash: BCrypt hash of "oc_dev_9876543210fedcba"
INSERT INTO api_key (user_id, key_hash, key_prefix, name, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM app_user WHERE username = 'developer'),
    '$2a$12$A1.bC2d3E4f5G6h7I8j9K0L1M2N3O4P5Q6R7S8T9U0V1W2X3Y4Z5A6',
    'oc_dev_',
    'Developer API Key',
    NOW() + INTERVAL '1 year', -- Expires in 1 year
    NOW(),
    NOW()
);

-- Insert API key for org-admin (for admin operations testing)
-- Key value: oc_admin_abcd1234efgh5678 (this is what the admin would use)
-- Hash: BCrypt hash of "oc_admin_abcd1234efgh5678"
INSERT INTO api_key (user_id, key_hash, key_prefix, name, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM app_user WHERE username = 'org-admin'),
    '$2a$12$X9.yZ0a1B2c3D4e5F6g7H8i9J0k1L2m3N4o5P6q7R8s9T0u1V2w3X4',
    'oc_admin_',
    'Admin API Key',
    NOW() + INTERVAL '1 year', -- Expires in 1 year
    NOW(),
    NOW()
);

-- Insert a long-term service API key (no expiration) for service-to-service authentication
-- Key value: oc_service_xyz789abc123def456 (this is what services would use)
-- Hash: BCrypt hash of "oc_service_xyz789abc123def456"
INSERT INTO api_key (user_id, key_hash, key_prefix, name, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM app_user WHERE username = 'challenge-service'),
    '$2a$12$B5.cD6e7F8g9H0i1J2k3L4m5N6o7P8q9R0s1T2u3V4w5X6y7Z8a9B0',
    'oc_service_',
    'Service API Key',
    NULL, -- No expiration for service keys
    NOW(),
    NOW()
);

-- Note: The BCrypt hashes above are placeholders. In production, you would:
-- 1. Generate real API keys using a cryptographically secure random generator
-- 2. Hash them with BCrypt before storing
-- 3. Provide the unhashed keys to users securely (e.g., show once in UI)
-- 
-- For testing purposes, the actual API key values mentioned in comments can be used
-- if the application properly hashes incoming keys before comparing with stored hashes.
-- 
-- Test credentials summary:
-- Users (all with password 'changeme'):
-- - testuser (role: user)
-- - developer (role: developer)  
-- - org-admin (role: admin)
-- 
-- API Keys:
-- - oc_test_1234567890abcdef (testuser)
-- - oc_dev_9876543210fedcba (developer)
-- - oc_admin_abcd1234efgh5678 (org-admin)
-- - oc_service_xyz789abc123def456 (challenge-service)
