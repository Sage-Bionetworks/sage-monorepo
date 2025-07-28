-- Insert API key that the challenge service can use to interact with the organization service
INSERT INTO api_key (user_id, key_hash, key_prefix, name, expires_at) VALUES
(
    (SELECT id FROM app_user WHERE username = 'challenge-service'),
    '$2a$12$r551H1sD7nmZ1qzBYjiz5e7qo7FlRcBZYgtd.tcpXsDTAMxX1w1cK',
    'oc_dev_',
    'Login Session',
    NULL -- No expiration for this key
);