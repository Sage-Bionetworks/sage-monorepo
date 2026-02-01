-- ============================================================================
-- Quest Table for Community Quests
-- ============================================================================
-- This migration creates the quest table to store community quest metadata
-- including time boundaries for contributor tracking.
-- ============================================================================

-- Create quest table
CREATE TABLE api.quest (
    id BIGSERIAL PRIMARY KEY,
    quest_id VARCHAR(100) NOT NULL UNIQUE,
    start_date TIMESTAMPTZ NOT NULL,
    end_date TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Create index for quest_id lookup (primary query path)
CREATE INDEX idx_quest_quest_id ON api.quest(quest_id);

-- Create composite index for battle queries (user_id, ended_at, status check via ended_at IS NOT NULL)
-- This index optimizes the contributor query that finds users with completed battles in date range
CREATE INDEX idx_battle_user_ended ON api.battle(user_id, ended_at) WHERE ended_at IS NOT NULL;
