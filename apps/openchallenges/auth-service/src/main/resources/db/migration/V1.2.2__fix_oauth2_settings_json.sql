-- Fix OAuth2 client settings JSON to include proper @class annotations and minimal settings
-- This fixes the Jackson deserialization error and provides required non-empty settings

UPDATE oauth2_registered_client 
SET client_settings = '{"@class":"java.util.HashMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}'
WHERE client_id = 'openchallenges-client';

UPDATE oauth2_registered_client 
SET token_settings = '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000]}'
WHERE client_id = 'openchallenges-client';

-- Fix API key clients with properly annotated minimal settings
UPDATE oauth2_registered_client 
SET 
    client_settings = '{"@class":"java.util.HashMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    token_settings = '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",900.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000]}'
WHERE client_id LIKE 'oc-ak_%';
