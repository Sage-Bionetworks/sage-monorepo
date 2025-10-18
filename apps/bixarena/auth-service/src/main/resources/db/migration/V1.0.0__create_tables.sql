-- Create app_user table
CREATE TABLE auth.app_user (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  username VARCHAR(255) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  role VARCHAR(50) NOT NULL DEFAULT 'user',
  enabled BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Create indexes for better performance
CREATE INDEX idx_auth_app_user_username ON auth.app_user(username);
CREATE INDEX idx_auth_app_user_email ON auth.app_user(email) WHERE email IS NOT NULL;
CREATE INDEX idx_auth_app_user_first_name ON auth.app_user(first_name) WHERE first_name IS NOT NULL;
CREATE INDEX idx_auth_app_user_last_name ON auth.app_user(last_name) WHERE last_name IS NOT NULL;

-- Create external_account table for OAuth2 provider linking
CREATE TABLE auth.external_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.app_user(id) ON DELETE CASCADE,
    provider VARCHAR(50) NOT NULL,  -- 'synapse'
    external_id VARCHAR(255) NOT NULL,  -- provider's user ID
    external_username VARCHAR(255),  -- provider's username/login
    external_email VARCHAR(255),  -- provider's email
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Ensure one account per provider per user
    UNIQUE(user_id, provider),
    -- Ensure external account uniqueness per provider
    UNIQUE(provider, external_id)
);

-- Add indexes for performance
CREATE INDEX idx_auth_external_account_user_id ON auth.external_account(user_id);
CREATE INDEX idx_auth_external_account_provider ON auth.external_account(provider);
CREATE INDEX idx_auth_external_account_external_id ON auth.external_account(provider, external_id);
CREATE INDEX idx_auth_external_account_external_email ON auth.external_account(external_email);

-- Add updated_at trigger function if not exists
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add triggers for updated_at columns
CREATE TRIGGER update_app_user_updated_at BEFORE UPDATE ON auth.app_user FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_external_account_updated_at BEFORE UPDATE ON auth.external_account FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
