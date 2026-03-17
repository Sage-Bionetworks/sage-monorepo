-- ============================================================================
-- Quest Metadata and Quest Posts
-- ============================================================================
-- This migration extends the quest table with metadata fields (title,
-- description, goal, active post index) and creates the quest_post table
-- for storing quest content with publish scheduling, progress gating,
-- and tier-based access control.
--
-- See RFC-0002: BixArena Quest Post Management API
-- ============================================================================

-- ============================================================================
-- Extend quest table with metadata
-- ============================================================================
ALTER TABLE api.quest
    ADD COLUMN title VARCHAR(200) NOT NULL DEFAULT '',
    ADD COLUMN description VARCHAR(5000) NOT NULL DEFAULT '',
    ADD COLUMN goal INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN active_post_index INTEGER NOT NULL DEFAULT 0;

-- Remove defaults after adding columns (defaults are only needed for existing rows)
ALTER TABLE api.quest
    ALTER COLUMN title DROP DEFAULT,
    ALTER COLUMN description DROP DEFAULT,
    ALTER COLUMN goal DROP DEFAULT,
    ALTER COLUMN active_post_index DROP DEFAULT,
    ADD CONSTRAINT chk_quest_goal CHECK (goal >= 0),
    ADD CONSTRAINT chk_quest_active_post_index CHECK (active_post_index >= 0);

-- ============================================================================
-- Create quest_post table
-- ============================================================================
CREATE TABLE api.quest_post (
    id BIGSERIAL PRIMARY KEY,
    quest_id BIGINT NOT NULL REFERENCES api.quest(id) ON DELETE CASCADE,
    post_index INTEGER NOT NULL,
    date DATE,
    title VARCHAR(300) NOT NULL,
    description TEXT NOT NULL,
    images JSONB NOT NULL DEFAULT '[]',
    publish_date TIMESTAMPTZ,
    required_progress INTEGER,
    required_tier VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_quest_post_index UNIQUE (quest_id, post_index),
    CONSTRAINT chk_quest_post_index CHECK (post_index >= 0),
    CONSTRAINT chk_quest_post_required_progress CHECK (required_progress >= 0),
    CONSTRAINT chk_quest_post_required_tier CHECK (required_tier IN ('knight', 'champion'))
);

-- Index for querying posts by quest, ordered by post_index
CREATE INDEX idx_quest_post_quest_id ON api.quest_post(quest_id, post_index);
