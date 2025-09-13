-- Insert test users and API keys with proper OAuth2 integration
-- These are for development/testing purposes only and use the new API key format

-- Insert test user with email and profile info for OAuth2 testing
INSERT INTO app_user (username, password_hash, email, first_name, last_name, role, email_verified, created_at, updated_at) VALUES
('testuser', '$2a$12$lWQA8qj1Pp9NfAsWY53rQuK/uChV.EJ1RTxhisDFuV0uHrJFm0/J6', 'testuser@example.com', 'Test', 'User', 'user', true, NOW(), NOW());

-- Insert test developer user for API development
INSERT INTO app_user (username, password_hash, email, first_name, last_name, role, email_verified, created_at, updated_at) VALUES
('developer', '$2a$12$lWQA8qj1Pp9NfAsWY53rQuK/uChV.EJ1RTxhisDFuV0uHrJFm0/J6', 'developer@example.com', 'API', 'Developer', 'user', true, NOW(), NOW());

-- Insert test organization admin user
INSERT INTO app_user (username, password_hash, email, first_name, last_name, role, email_verified, created_at, updated_at) VALUES
('org-admin', '$2a$12$lWQA8qj1Pp9NfAsWY53rQuK/uChV.EJ1RTxhisDFuV0uHrJFm0/J6', 'org-admin@example.com', 'Organization', 'Admin', 'admin', true, NOW(), NOW());

-- Insert API keys using the new format: {prefix}{suffix}.{secret}
-- Each API key also gets a corresponding OAuth2 client for service principal authentication

-- Test API Key: oc_dev_test1.test_secret_1234567890abcdef
INSERT INTO api_key (user_id, key_hash, key_prefix, name, client_id, allowed_scopes, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM app_user WHERE username = 'testuser'),
    '{bcrypt}$2a$12$71IJAwVtX8GpMzVzH8lq3.k7De/c5Tc7TIvmuXAgiN0M6VWPd4i1K', -- BCrypt hash of full key with prefix
    'oc_dev_',
    'Test API Key',
    'oc_api_key_test1',
    'read:profile,update:profile,read:api-key,create:api-key,delete:api-key,read:organizations,read:challenges,read:challenges-analytics,read:challenge-platforms,read:edam-concepts',
    NOW() + INTERVAL '1 year',
    NOW(),
    NOW()
);

-- Developer API Key: oc_dev_dev1.dev_secret_9876543210fedcba
INSERT INTO api_key (user_id, key_hash, key_prefix, name, client_id, allowed_scopes, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM app_user WHERE username = 'developer'),
    '{bcrypt}$2a$12$TG/Ty67DQkbYWRD6IJPNpu1y3mEE9NVMv2dHRA.Kb0HGXmUPUpeTO', -- BCrypt hash of full key with prefix
    'oc_dev_',
    'Developer API Key',
    'oc_api_key_dev1',
    'read:profile,update:profile,read:api-key,create:api-key,delete:api-key,read:organizations,create:organizations,update:organizations,read:challenges,create:challenges,update:challenges,read:challenges-analytics,read:challenge-platforms,read:edam-concepts',
    NOW() + INTERVAL '1 year',
    NOW(),
    NOW()
);

-- Admin API Key: oc_dev_admin1.admin_secret_abcd1234efgh5678
INSERT INTO api_key (user_id, key_hash, key_prefix, name, client_id, allowed_scopes, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM app_user WHERE username = 'org-admin'),
    '{bcrypt}$2a$12$EUPPJ0euYyruWcHKGQZ7P.zUZcPJDB1FeNjt7OJ1XMX41ni3YgMO.', -- BCrypt hash of full key with prefix
    'oc_dev_',
    'Admin API Key',
    'oc_api_key_admin1',
    'read:profile,update:profile,read:api-key,create:api-key,delete:api-key,read:organizations,create:organizations,update:organizations,delete:organizations,read:challenges,create:challenges,update:challenges,delete:challenges,read:challenges-analytics,read:challenge-platforms,create:challenge-platforms,update:challenge-platforms,delete:challenge-platforms,read:edam-concepts',
    NOW() + INTERVAL '1 year',
    NOW(),
    NOW()
);

-- Create corresponding OAuth2 clients for each API key
INSERT INTO oauth2_registered_client (
    id,
    client_id,
    client_id_issued_at,
    client_secret,
    client_secret_expires_at,
    client_name,
    client_authentication_methods,
    authorization_grant_types,
    redirect_uris,
    post_logout_redirect_uris,
    scopes,
    client_settings,
    token_settings
) VALUES
(
    gen_random_uuid()::text,
    'oc_api_key_test1',
    NOW(),
    '{noop}test_secret_1234567890abcdef',
    NOW() + INTERVAL '1 year',
    'API Key Client: Test API Key',
    'client_secret_basic,client_secret_post',
    'client_credentials',
    '',
    '',
    'read:profile,update:profile,read:api-key,create:api-key,delete:api-key,read:organizations,read:challenges,read:challenges-analytics,read:challenge-platforms,read:edam-concepts',
    '{"@class":"java.util.HashMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",7200.000000000]}'
),
(
    gen_random_uuid()::text,
    'oc_api_key_dev1',
    NOW(),
    '{noop}dev_secret_9876543210fedcba',
    NOW() + INTERVAL '1 year',
    'API Key Client: Developer API Key',
    'client_secret_basic,client_secret_post',
    'client_credentials',
    '',
    '',
    'read:profile,update:profile,read:api-key,create:api-key,delete:api-key,read:organizations,create:organizations,update:organizations,read:challenges,create:challenges,update:challenges,read:challenges-analytics,read:challenge-platforms,read:edam-concepts',
    '{"@class":"java.util.HashMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",7200.000000000]}'
),
(
    gen_random_uuid()::text,
    'oc_api_key_admin1',
    NOW(),
    '{noop}admin_secret_abcd1234efgh5678',
    NOW() + INTERVAL '1 year',
    'API Key Client: Admin API Key',
    'client_secret_basic,client_secret_post',
    'client_credentials',
    '',
    '',
    'read:profile,update:profile,read:api-key,create:api-key,delete:api-key,read:organizations,create:organizations,update:organizations,delete:organizations,read:challenges,create:challenges,update:challenges,delete:challenges,read:challenges-analytics,read:challenge-platforms,create:challenge-platforms,update:challenge-platforms,delete:challenge-platforms,read:edam-concepts',
    '{"@class":"java.util.HashMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",7200.000000000]}'
);

-- Test credentials summary:
-- Users (all with password 'changeme'):
-- - testuser (role: user)
-- - developer (role: user)
-- - org-admin (role: admin)
--
-- API Keys (new format: {prefix}{suffix}.{secret}):
-- - oc_dev_test1.test_secret_1234567890abcdef (testuser) -> OAuth2 client: oc_api_key_test1
-- - oc_dev_dev1.dev_secret_9876543210fedcba (developer) -> OAuth2 client: oc_api_key_dev1
-- - oc_dev_admin1.admin_secret_abcd1234efgh5678 (org-admin) -> OAuth2 client: oc_api_key_admin1
--
-- OAuth2 Client IDs use consistent "oc_api_key_{suffix}" format without legacy prefixes
