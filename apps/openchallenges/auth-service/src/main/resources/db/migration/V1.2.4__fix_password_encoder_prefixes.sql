-- Fix password encoder prefixes for existing API key hashes
-- This migration adds the {bcrypt} prefix to any key_hash values that don't already have it

-- Update API key hashes that don't have a password encoder prefix
UPDATE api_key 
SET key_hash = '{bcrypt}' || key_hash
WHERE key_hash NOT LIKE '{%}%';

-- Update OAuth2 client secrets that don't have a password encoder prefix
-- (This should not be needed as they should already have {noop} prefix, but being safe)
UPDATE oauth2_registered_client 
SET client_secret = '{noop}' || client_secret
WHERE client_secret NOT LIKE '{%}%';
