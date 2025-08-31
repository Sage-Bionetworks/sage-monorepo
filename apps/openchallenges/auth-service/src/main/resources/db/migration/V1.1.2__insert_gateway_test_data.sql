-- Insert gateway test data (V1.1.2)
-- This migration adds test users and API keys specifically for gateway authentication testing
-- Builds upon existing test data from V1.1.1

-- Insert additional test users for gateway authentication testing
INSERT INTO app_user (username, password_hash, email, first_name, last_name, role, email_verified, created_at, updated_at) VALUES
('gateway-test-admin', '$2a$10$92QnBASQaYz2PGzGJ4X/qOLKBHKsQ6RdUlq7HFrM.4VqU.Tr4QMZG', 'gateway-admin@example.com', 'Gateway', 'Admin', 'admin', true, NOW(), NOW()),
('gateway-test-user', '$2a$10$HZVd7vkLh3RsY5g8YJX.E.qYtJ1VdFtM9r6lKrG/nVT3ZqH9dE.Pu', 'gateway-user@example.com', 'Gateway', 'User', 'user', true, NOW(), NOW()),
('api-service-test', '$2a$10$JKLHnm34VqW2xR8pQ6M.K.wT7dZ9XcR5jL2pE7gF8sY4qN1mB.Av6', 'api-service@example.com', 'API', 'Service', 'service', true, NOW(), NOW());

-- Insert additional test API keys
-- Admin test key: oc_dev_test_admin_key_001 (for testing admin operations)
INSERT INTO api_key (user_id, key_hash, key_prefix, name, expires_at, created_at, updated_at) VALUES
((SELECT id FROM app_user WHERE username = 'gateway-test-admin'), '$2a$12$yZG6jaaCLtT5OyoXRzRg4.X4lLpadZk.sU44mOvQRwztuz5FuqewO', 'oc_dev_', 'Gateway Admin Test API Key', NOW() + INTERVAL '1 year', NOW(), NOW());

-- User test key: oc_dev_test_user_key_001 (for testing user operations)
INSERT INTO api_key (user_id, key_hash, key_prefix, name, expires_at, created_at, updated_at) VALUES
((SELECT id FROM app_user WHERE username = 'gateway-test-user'), '$2a$12$n8ymBy8thlr2ZgJsYkbTMO/gzlBJ2gWW2uuh8Mg0ek4TmwJBJBHW2', 'oc_dev_', 'Gateway User Test API Key', NOW() + INTERVAL '1 year', NOW(), NOW());

-- Service test key: oc_dev_test_service_key_001 (for service-to-service communication)
INSERT INTO api_key (user_id, key_hash, key_prefix, name, expires_at, created_at, updated_at) VALUES
((SELECT id FROM app_user WHERE username = 'api-service-test'), '$2a$12$2M0O8nxssll4pSFzTgR8key.5cDBdVeBbdGOvlsVO1IJxrTCAiMh.', 'oc_dev_', 'Gateway Service Test API Key', NOW() + INTERVAL '1 year', NOW(), NOW());

-- Note: Test credentials for development only
-- Passwords: 'test123' (all users)
-- API Keys:
--   Admin: oc_dev_test_admin_key_001
--   User: oc_dev_test_user_key_001  
--   Service: oc_dev_test_service_key_001
