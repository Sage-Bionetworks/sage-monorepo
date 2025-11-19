-- ============================================================================
-- Schema Change: Update Columns of Leaderboard Snapshots
-- ============================================================================
-- This migration adds a visibility and updated_at columns to the leaderboard_snapshot table
-- to control which snapshots are publicly accessible.
--
-- Changes:
-- - Add visibility column with values: 'public', 'private'
-- - Add updated_at column
-- - Add index on visibility
-- ============================================================================

-- ============================================================================
-- Add Visibility Column
-- ============================================================================
ALTER TABLE api.leaderboard_snapshot
  ADD COLUMN visibility VARCHAR(20) NOT NULL DEFAULT 'private'
  CHECK (visibility IN ('public', 'private'));

-- ============================================================================
-- Add UpdatedAt Column
-- ============================================================================
ALTER TABLE api.leaderboard_snapshot
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now();

-- ============================================================================
-- Create Index on Visibility
-- ============================================================================
CREATE INDEX idx_api_leaderboard_snapshot_visibility ON api.leaderboard_snapshot(visibility);