-- Insert API key that the challenge service can use to interact with the organization service
INSERT INTO api_key (user_id, key_hash, key_prefix, name, expires_at, created_at, updated_at) VALUES
(
    (SELECT id FROM app_user WHERE username = 'challenge-service'),
    '$2a$12$DMm4WSPALi5M8hQEE5caU.CpHY4czx8nDt1aqn.FlY2sk3sKvBBlq',
    'oc_dev_',
    'Login Session',
    NULL, -- No expiration for this key
    NOW(),
    NOW()
);