-- Data migration: Convert existing API keys to OAuth2 service principals
-- This migration creates RegisteredClient entries for existing API keys

-- First, update configuration for the main openchallenges-client
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
    '1234567890abcdef',
    'openchallenges-client',
    CURRENT_TIMESTAMP,
    '{noop}secret',
    NULL,
    'OpenChallenges Web Application',
    'client_secret_basic,client_secret_post',
    'authorization_code,refresh_token,client_credentials',
    'http://127.0.0.1:8080/login/oauth2/code/openchallenges-oidc,http://127.0.0.1:8080/authorized',
    'http://127.0.0.1:8080/logged-out',
    'openid,profile,email,user:profile,user:email,user:keys,read:org,write:org,delete:org',
    '{"@class":"java.util.Map","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}',
    '{"@class":"java.util.Map","settings.token.reuse-refresh-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}'
);

-- Create OAuth2 clients for existing API keys
-- This uses a function to generate client_id from key_prefix and update api_key table
DO $$
DECLARE
    api_key_rec RECORD;
    new_client_id VARCHAR(100);
    role_scopes TEXT;
    key_suffix VARCHAR(50);
BEGIN
    -- Process each existing API key
    FOR api_key_rec IN SELECT ak.*, au.role, au.username FROM api_key ak JOIN app_user au ON ak.user_id = au.id LOOP
        -- Extract suffix from key_prefix (remove environment prefix)
        -- key_prefix format: "oc_dev_", "oc_stage_", "oc_prod_"
        -- We'll use a simple approach: remove the prefix and use remainder as suffix
        key_suffix := CASE 
            WHEN api_key_rec.key_prefix LIKE 'oc_dev_%' THEN SUBSTRING(api_key_rec.key_prefix FROM 8)  -- Remove 'oc_dev_'
            WHEN api_key_rec.key_prefix LIKE 'oc_stage_%' THEN SUBSTRING(api_key_rec.key_prefix FROM 10) -- Remove 'oc_stage_'
            WHEN api_key_rec.key_prefix LIKE 'oc_prod_%' THEN SUBSTRING(api_key_rec.key_prefix FROM 9)  -- Remove 'oc_prod_'
            ELSE api_key_rec.key_prefix -- Fallback to original prefix
        END;
        
        -- Generate client_id from suffix
        new_client_id := 'oc-ak_' || COALESCE(NULLIF(key_suffix, ''), 'legacy_' || SUBSTRING(api_key_rec.id::text FROM 1 FOR 8));
        
        -- Determine scopes based on user role
        CASE api_key_rec.role
            WHEN 'admin' THEN role_scopes := 'read:org,write:org,delete:org,read:challenge,write:challenge,delete:challenge';
            WHEN 'service' THEN role_scopes := 'read:org,write:org,read:challenge,write:challenge';
            WHEN 'user' THEN role_scopes := 'read:org,read:challenge';
            ELSE role_scopes := 'read:org';
        END CASE;
        
        -- Insert RegisteredClient for this API key
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
            api_key_rec.id::text,  -- Use API key UUID as registered client ID
            new_client_id,
            api_key_rec.created_at,
            api_key_rec.key_hash,  -- Use existing hash as client_secret (already BCrypt)
            api_key_rec.expires_at,
            'API Key: ' || api_key_rec.name || ' (' || api_key_rec.username || ')',
            'client_secret_basic',
            'client_credentials',
            NULL,  -- No redirect URIs for API keys
            NULL,  -- No post logout redirect URIs for API keys
            role_scopes,
            '{"@class":"java.util.Map","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
            '{"@class":"java.util.Map","settings.token.reuse-refresh-tokens":false,"settings.token.access-token-time-to-live":["java.time.Duration",900.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"}}'
        );
        
        -- Update api_key record with client_id and scopes
        UPDATE api_key 
        SET 
            client_id = new_client_id,
            allowed_scopes = role_scopes,
            status = 'active'
        WHERE id = api_key_rec.id;
        
        RAISE NOTICE 'Created OAuth2 client % for API key % (user: %)', new_client_id, api_key_rec.name, api_key_rec.username;
    END LOOP;
END $$;

-- Add foreign key constraint now that all records have client_id
ALTER TABLE api_key ADD CONSTRAINT fk_api_key_client_id 
    FOREIGN KEY (client_id) REFERENCES oauth2_registered_client(client_id) ON DELETE CASCADE;
