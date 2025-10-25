-- Leaderboards table
CREATE TABLE api.leaderboard (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  slug VARCHAR(100) UNIQUE NOT NULL,
  name VARCHAR(200) NOT NULL,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Models table
CREATE TABLE api.model (
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
CREATE INDEX idx_api_model_license ON api.model(license);
CREATE INDEX idx_api_model_active ON api.model(active);

-- Leaderboard snapshots table
CREATE TABLE api.leaderboard_snapshot (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  leaderboard_id UUID NOT NULL REFERENCES api.leaderboard(id) ON DELETE CASCADE,
  snapshot_identifier VARCHAR(200) NOT NULL,
  description TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(leaderboard_id, snapshot_identifier)
);

-- Leaderboard entries table (stores current and historical data)
CREATE TABLE api.leaderboard_entry (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  leaderboard_id UUID NOT NULL REFERENCES api.leaderboard(id) ON DELETE CASCADE,
  model_id UUID NOT NULL REFERENCES api.model(id) ON DELETE CASCADE,
  snapshot_id UUID NOT NULL REFERENCES api.leaderboard_snapshot(id) ON DELETE CASCADE,
  bt_score DECIMAL(10,6) NOT NULL,
  evaluation_count INTEGER NOT NULL DEFAULT 0,
  rank INTEGER NOT NULL,
  secondary_score DECIMAL(10,6),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(leaderboard_id, model_id, snapshot_id)
);

-- Indexes for performance
CREATE INDEX idx_api_leaderboard_entry_leaderboard_id ON api.leaderboard_entry(leaderboard_id);
CREATE INDEX idx_api_leaderboard_entry_model_id ON api.leaderboard_entry(model_id);
CREATE INDEX idx_api_leaderboard_entry_snapshot_id ON api.leaderboard_entry(snapshot_id);
CREATE INDEX idx_api_leaderboard_entry_rank ON api.leaderboard_entry(rank);
CREATE INDEX idx_api_leaderboard_entry_bt_score ON api.leaderboard_entry(bt_score DESC);
CREATE INDEX idx_api_leaderboard_snapshot_leaderboard_id ON api.leaderboard_snapshot(leaderboard_id);
CREATE INDEX idx_api_leaderboard_snapshot_created_at ON api.leaderboard_snapshot(created_at DESC);

-- Example prompts table (self-contained biomedical example prompts)
CREATE TABLE api.example_prompt (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  question VARCHAR(1000) NOT NULL,
  source VARCHAR(100) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  -- Table constraints
  CONSTRAINT chk_example_prompt_source CHECK (source IN ('pubmedqa', 'bixbench', 'bixarena'))
);

-- Indexes for performance
CREATE INDEX idx_api_example_prompt_source ON api.example_prompt(source);
CREATE INDEX idx_api_example_prompt_active ON api.example_prompt(active);

-- Create battle table
CREATE TABLE api.battle (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(255),
  user_id UUID NOT NULL,
  model_1_id UUID NOT NULL REFERENCES api.model(id) ON DELETE CASCADE,
  model_2_id UUID NOT NULL REFERENCES api.model(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  ended_at TIMESTAMPTZ
);

-- Indexes for performance
CREATE INDEX idx_api_battle_user_id ON api.battle(user_id);
CREATE INDEX idx_api_battle_created_at ON api.battle(created_at DESC);

-- Create round table
CREATE TABLE api.battle_evaluation (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  battle_id UUID NOT NULL REFERENCES api.battle(id) ON DELETE CASCADE,
  outcome VARCHAR(20) NOT NULL,
  is_valid BOOLEAN NOT NULL DEFAULT FALSE,
  validation_error VARCHAR(1000),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  -- Table constraints
  CONSTRAINT unique_battle_evaluation UNIQUE (battle_id),
  CONSTRAINT chk_evaluation_outcome CHECK (outcome IN ('MODEL_1', 'MODEL_2', 'TIE'))
);

-- Indexes for evaluation queries
CREATE INDEX idx_api_battle_evaluation_outcome ON api.battle_evaluation(outcome);
CREATE INDEX idx_api_battle_evaluation_created_at ON api.battle_evaluation(created_at DESC);
CREATE INDEX idx_api_battle_evaluation_is_valid ON api.battle_evaluation(is_valid);