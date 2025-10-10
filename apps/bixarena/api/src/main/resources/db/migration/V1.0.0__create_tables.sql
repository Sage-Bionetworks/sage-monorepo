-- Leaderboards table
CREATE TABLE leaderboard (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  slug VARCHAR(100) UNIQUE NOT NULL,
  name VARCHAR(200) NOT NULL,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Models table
CREATE TABLE model (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  slug VARCHAR(200) UNIQUE NOT NULL,
  name VARCHAR(300) NOT NULL,
  license VARCHAR(20) NOT NULL CHECK (license IN ('open-source', 'commercial')),
  active BOOLEAN NOT NULL DEFAULT FALSE,
  alias VARCHAR(200),
  external_link VARCHAR(300) NOT NULL,
  organization VARCHAR(200),
  description VARCHAR(300),
  api_model_name VARCHAR(300) NOT NULL,
  api_base VARCHAR(300) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Indexes for performance
CREATE INDEX idx_model_license ON model(license);
CREATE INDEX idx_model_active ON model(active);

-- Leaderboard snapshots table
CREATE TABLE leaderboard_snapshot (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  leaderboard_id UUID NOT NULL REFERENCES leaderboard(id) ON DELETE CASCADE,
  snapshot_identifier VARCHAR(200) NOT NULL,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(leaderboard_id, snapshot_identifier)
);

-- Leaderboard entries table (stores current and historical data)
CREATE TABLE leaderboard_entry (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  leaderboard_id UUID NOT NULL REFERENCES leaderboard(id) ON DELETE CASCADE,
  model_id UUID NOT NULL REFERENCES model(id) ON DELETE CASCADE,
  snapshot_id UUID NOT NULL REFERENCES leaderboard_snapshot(id) ON DELETE CASCADE,
  bt_score DECIMAL(10,6) NOT NULL,
  vote_count INTEGER NOT NULL DEFAULT 0,
  rank INTEGER NOT NULL,
  secondary_score DECIMAL(10,6),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(leaderboard_id, model_id, snapshot_id)
);

-- Indexes for performance
CREATE INDEX idx_leaderboard_entry_leaderboard_id ON leaderboard_entry(leaderboard_id);
CREATE INDEX idx_leaderboard_entry_model_id ON leaderboard_entry(model_id);
CREATE INDEX idx_leaderboard_entry_snapshot_id ON leaderboard_entry(snapshot_id);
CREATE INDEX idx_leaderboard_entry_rank ON leaderboard_entry(rank);
CREATE INDEX idx_leaderboard_entry_bt_score ON leaderboard_entry(bt_score DESC);
CREATE INDEX idx_leaderboard_snapshot_leaderboard_id ON leaderboard_snapshot(leaderboard_id);
CREATE INDEX idx_leaderboard_snapshot_created_at ON leaderboard_snapshot(created_at DESC);

-- Example prompts table (self-contained biomedical example prompts)
CREATE TABLE example_prompt (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  question VARCHAR(1000) NOT NULL,
  source VARCHAR(100) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  -- Table constraints
  CONSTRAINT chk_example_prompt_source CHECK (source IN ('pubmedqa', 'bixbench', 'bixarena'))
);

-- Indexes for performance
CREATE INDEX idx_example_prompt_source ON example_prompt(source);
CREATE INDEX idx_example_prompt_active ON example_prompt(active);

-- Create app_user table
CREATE TABLE app_user (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  username VARCHAR(255) UNIQUE NOT NULL,
  role VARCHAR(50) NOT NULL DEFAULT 'user',
  enabled BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Create indexes for better performance
CREATE INDEX idx_app_user_username ON app_user(username);

-- Create external_account table for OAuth2 provider linking
CREATE TABLE external_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
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
CREATE INDEX idx_external_account_user_id ON external_account(user_id);
CREATE INDEX idx_external_account_provider ON external_account(provider);
CREATE INDEX idx_external_account_external_id ON external_account(provider, external_id);
CREATE INDEX idx_external_account_external_email ON external_account(external_email);

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
