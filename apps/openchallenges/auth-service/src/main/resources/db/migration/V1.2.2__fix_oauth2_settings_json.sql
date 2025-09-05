-- Fix OAuth2 client settings JSON format
-- The initial migration used simple string concatenation which created invalid JSON
-- This migration updates all existing clients to use proper JSON format

UPDATE oauth2_registered_client 
SET client_settings = '{"@class":"java.util.HashMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}'
WHERE client_id LIKE 'oc_api_key_%' 
  AND client_settings = '{"settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}';

UPDATE oauth2_registered_client 
SET token_settings = '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",7200.000000000]}'
WHERE client_id LIKE 'oc_api_key_%' 
  AND token_settings = '{"settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",7200.000000000]}';

-- Also fix the main client if needed
UPDATE oauth2_registered_client 
SET client_settings = '{"@class":"java.util.HashMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}'
WHERE client_id = 'openchallenges-client' 
  AND client_settings = '{"settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}';

UPDATE oauth2_registered_client 
SET token_settings = '{"@class":"java.util.HashMap","settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000]}'
WHERE client_id = 'openchallenges-client' 
  AND token_settings = '{"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000]}';
