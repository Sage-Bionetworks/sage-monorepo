-- Add anonymous OAuth2 client for public endpoint access
-- This client allows the API gateway to generate JWT tokens for endpoints marked with x-anonymous-access: true

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
    'oc_anonymous_client',
    NOW(),
    '{noop}anonymous_secret_for_public_access',
    NULL, -- Never expires
    'Anonymous Access Client',
    'client_secret_basic,client_secret_post',
    'client_credentials',
    '',
    '',
    'read:orgs,read:challenges,read:challenges-analytics,read:challenge-platforms,read:edam-concepts', -- Read-only scopes for public data
    '{"@class":"java.util.HashMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",7200.000000000]}'
);
