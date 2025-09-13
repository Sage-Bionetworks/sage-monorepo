-- Add OAuth2 client for challenge service for service-to-service communication
-- This allows the challenge service to authenticate with other services using OAuth2 Token Exchange

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
) VALUES (
    gen_random_uuid()::text,
    'challenge-service',
    CURRENT_TIMESTAMP,
    '{noop}challenge-service-secret',
    NULL,
    'OpenChallenges Challenge Service',
    'client_secret_basic',
    'urn:ietf:params:oauth:grant-type:token-exchange,client_credentials',
    NULL,
    NULL,
    'read:organizations,update:organizations',
    '{"@class":"java.util.HashMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"}}'
);
