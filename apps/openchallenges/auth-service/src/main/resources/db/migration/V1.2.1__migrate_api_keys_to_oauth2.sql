-- Migrate existing API keys to OAuth2 service principals
-- This migration creates OAuth2 RegisteredClient entries for all existing API keys

-- First, create OAuth2 client for the main web application
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
    'openchallenges-client',
    CURRENT_TIMESTAMP,
    '{noop}secret',
    NULL,
    'OpenChallenges Web Application',
    'client_secret_basic,client_secret_post',
    'authorization_code,refresh_token,client_credentials',
    'http://openchallenges-api-gateway:8082/oauth2/code/openchallenges-oidc,http://openchallenges-api-gateway:8082/oauth2/authorized',
    'http://openchallenges-api-gateway:8082/oauth2/logged-out',
    'openid,profile,email,read:profile,update:profile,read:api-key,create:api-key,delete:api-key,read:orgs,create:orgs,update:orgs,delete:orgs',
    '{"@class":"java.util.HashMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}',
    '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000]}'
);

-- Create OAuth2 clients for existing API keys
-- This uses a consistent naming pattern: oc_api_key_{suffix}
DO $$
DECLARE
    api_key_rec RECORD;
    suffix_part TEXT;
    client_id_value TEXT;
BEGIN
    FOR api_key_rec IN SELECT * FROM api_key LOOP
        -- Generate suffix from id since existing keys don't have structured format
        suffix_part := 'legacy_' || SUBSTRING(api_key_rec.id::text FROM 1 FOR 8);
        client_id_value := 'oc_api_key_' || suffix_part;
        client_id_value := 'oc_api_key_' || suffix_part;
        
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
            api_key_rec.id::text,
            client_id_value,
            api_key_rec.created_at,
            '{noop}dummy_secret',
            api_key_rec.expires_at,
            'API Key Client: ' || api_key_rec.name,
            'client_secret_basic,client_secret_post',
            'client_credentials',
            '',
            '',
            'read:profile,update:profile,read:api-key,create:api-key,delete:api-key,read:orgs,create:orgs,update:orgs,delete:orgs',
            '{"@class":"java.util.HashMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
            '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",7200.000000000]}'
        );
        
        -- Update API key with OAuth2 client reference
        UPDATE api_key 
        SET 
            client_id = client_id_value,
            allowed_scopes = 'read:profile,update:profile,read:api-key,create:api-key,delete:api-key,read:orgs,create:orgs,update:orgs,delete:orgs'
        WHERE id = api_key_rec.id;
    END LOOP;
END $$;
