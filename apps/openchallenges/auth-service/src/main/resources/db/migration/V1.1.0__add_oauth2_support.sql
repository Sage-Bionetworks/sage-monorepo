-- Add OAuth2 support to app_user table
ALTER TABLE app_user ADD COLUMN email VARCHAR(255);
ALTER TABLE app_user ADD COLUMN first_name VARCHAR(100);
ALTER TABLE app_user ADD COLUMN last_name VARCHAR(100);
ALTER TABLE app_user ADD COLUMN avatar_url VARCHAR(500);
ALTER TABLE app_user ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;

-- Create external_account table for OAuth2 provider linking
CREATE TABLE external_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    provider VARCHAR(50) NOT NULL,  -- 'google', 'synapse', etc.
    external_id VARCHAR(255) NOT NULL,  -- provider's user ID
    external_username VARCHAR(255),  -- provider's username/login
    external_email VARCHAR(255),  -- provider's email
    access_token_hash VARCHAR(255),  -- encrypted OAuth2 access token
    refresh_token_hash VARCHAR(255),  -- encrypted OAuth2 refresh token
    expires_at TIMESTAMP WITH TIME ZONE,  -- access token expiry
    provider_data JSONB,  -- additional provider-specific data
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Ensure one account per provider per user
    UNIQUE(user_id, provider),
    -- Ensure external account uniqueness per provider
    UNIQUE(provider, external_id)
);

-- Add indexes for performance
CREATE INDEX idx_external_account_user_id ON external_account(user_id);
CREATE INDEX idx_external_account_provider ON external_account(provider);
CREATE INDEX idx_external_account_external_id ON external_account(provider, external_id);
CREATE INDEX idx_external_account_external_email ON external_account(external_email);

CREATE INDEX idx_app_user_email ON app_user(email);

-- Add updated_at trigger function if not exists
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add triggers for updated_at columns
CREATE TRIGGER update_app_user_updated_at BEFORE UPDATE ON app_user FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_external_account_updated_at BEFORE UPDATE ON external_account FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
