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
CREATE INDEX idx_model_license ON model(license);
CREATE INDEX idx_model_active ON model(active);

-- Sessions (existing table - keeping it)
CREATE TABLE conversation (
  id UUID PRIMARY KEY,
  -- user_id INT NOT NULL REFERENCES "user"(id),
  title TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);