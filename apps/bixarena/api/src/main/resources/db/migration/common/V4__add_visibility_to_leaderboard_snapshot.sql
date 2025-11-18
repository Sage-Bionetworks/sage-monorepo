-- ============================================================================
-- Schema Change: Add Visibility to Leaderboard Snapshots
-- ============================================================================
-- This migration adds a visibility column to the leaderboard_snapshot table
-- to control which snapshots are publicly accessible.
--
-- Changes:
-- - Add visibility column with values: 'public', 'private'
-- - Default value: 'private' (secure by default)
-- - Add index for efficient filtering
-- - Update existing snapshots placeholder to 'private'
-- ============================================================================

-- ============================================================================
-- Add Visibility Column
-- ============================================================================
ALTER TABLE api.leaderboard_snapshot
  ADD COLUMN visibility VARCHAR(20) NOT NULL DEFAULT 'private'
  CHECK (visibility IN ('public', 'private'));

-- ============================================================================
-- Add Index for Performance
-- ============================================================================
CREATE INDEX idx_api_leaderboard_snapshot_visibility ON api.leaderboard_snapshot(visibility);