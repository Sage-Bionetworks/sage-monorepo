-- Migration to support API keys as OAuth2 service principals
-- This migration adds Spring Authorization Server JDBC tables and modifies the API key table

-- Add Spring Authorization Server tables
-- Based on: https://github.com/spring-projects/spring-authorization-server/blob/main/oauth2-authorization-server/src/main/resources/org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql

CREATE TABLE oauth2_registered_client (
    id varchar(100) NOT NULL,
    client_id varchar(100) NOT NULL,
    client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret varchar(200) DEFAULT NULL,
    client_secret_expires_at timestamp DEFAULT NULL,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris varchar(1000) DEFAULT NULL,
    scopes varchar(1000) NOT NULL,
    client_settings varchar(2000) NOT NULL,
    token_settings varchar(2000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization (
    id varchar(100) NOT NULL,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000) DEFAULT NULL,
    attributes varchar(4000) DEFAULT NULL,
    state varchar(500) DEFAULT NULL,
    authorization_code_value varchar(4000) DEFAULT NULL,
    authorization_code_issued_at timestamp DEFAULT NULL,
    authorization_code_expires_at timestamp DEFAULT NULL,
    authorization_code_metadata varchar(2000) DEFAULT NULL,
    access_token_value varchar(4000) DEFAULT NULL,
    access_token_issued_at timestamp DEFAULT NULL,
    access_token_expires_at timestamp DEFAULT NULL,
    access_token_metadata varchar(2000) DEFAULT NULL,
    access_token_type varchar(100) DEFAULT NULL,
    access_token_scopes varchar(1000) DEFAULT NULL,
    oidc_id_token_value varchar(4000) DEFAULT NULL,
    oidc_id_token_issued_at timestamp DEFAULT NULL,
    oidc_id_token_expires_at timestamp DEFAULT NULL,
    oidc_id_token_metadata varchar(2000) DEFAULT NULL,
    refresh_token_value varchar(4000) DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT NULL,
    refresh_token_expires_at timestamp DEFAULT NULL,
    refresh_token_metadata varchar(2000) DEFAULT NULL,
    user_code_value varchar(4000) DEFAULT NULL,
    user_code_issued_at timestamp DEFAULT NULL,
    user_code_expires_at timestamp DEFAULT NULL,
    user_code_metadata varchar(2000) DEFAULT NULL,
    device_code_value varchar(4000) DEFAULT NULL,
    device_code_issued_at timestamp DEFAULT NULL,
    device_code_expires_at timestamp DEFAULT NULL,
    device_code_metadata varchar(2000) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

-- Create indexes for performance
CREATE UNIQUE INDEX ix_oauth2_registered_client_client_id ON oauth2_registered_client(client_id);
CREATE INDEX ix_oauth2_authorization_registered_client_id ON oauth2_authorization(registered_client_id);
CREATE INDEX ix_oauth2_authorization_principal_name ON oauth2_authorization(principal_name);

-- Modify api_key table to support OAuth2 service principal pattern
ALTER TABLE api_key ADD COLUMN client_id VARCHAR(100);  -- FK to oauth2_registered_client.client_id
ALTER TABLE api_key ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'active';  -- active, revoked, suspended
ALTER TABLE api_key ADD COLUMN allowed_scopes TEXT;  -- redundant to oauth2_registered_client scopes for quick UX
ALTER TABLE api_key ADD COLUMN env_tag VARCHAR(32);  -- dev/stage/prod (optional)
ALTER TABLE api_key ADD COLUMN ip_allowlist_cidr TEXT;  -- comma-separated CIDR blocks (optional)

-- Add FK constraint after updating existing records
-- We'll do this in data migration step

-- Create index on client_id for lookups
CREATE INDEX idx_api_key_client_id ON api_key(client_id);
CREATE INDEX idx_api_key_status ON api_key(status);
CREATE INDEX idx_api_key_key_prefix ON api_key(key_prefix);
