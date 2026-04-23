-- ============================================================================
-- Categorization Tables
-- ============================================================================
-- This migration creates the example_prompt_categorization and
-- battle_categorization tables, and adds the effective_categorization_id FK
-- on the example_prompt and battle tables. Prompt categorizations carry a
-- single category per run (1-to-1); battle categorizations use a join table
-- because each run can produce 1-3 categories (1-to-many).
-- ============================================================================


-- ============================================================================
-- Example Prompt Categorization Table
-- ============================================================================
-- Stores one categorization run per example prompt.
-- Each row represents one run (AI or human override).
-- categorized_by NULL  = AI run
-- categorized_by <uid> = admin human override
-- reason is optional for human overrides, always NULL for AI runs.
-- status records the terminal outcome of the run:
--   'matched'   — classifier picked a category (category column is non-NULL)
--   'abstained' — classifier ran successfully but declared no category fits
--   'failed'    — classifier could not run (AI service / LLM error; retry-able)
-- category holds the slug for 'matched' rows; NULL for 'abstained' and 'failed'.
-- ============================================================================

CREATE TABLE api.example_prompt_categorization (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  prompt_id        UUID NOT NULL REFERENCES api.example_prompt(id) ON DELETE CASCADE,
  method           VARCHAR(100) NOT NULL,
  categorized_by   UUID NULL,            -- NULL = AI, non-NULL = admin user ID
  reason           VARCHAR(1000) NULL,   -- optional for human overrides, always NULL for AI
  status           VARCHAR(20) NOT NULL, -- 'matched' | 'abstained' | 'failed'
  category         VARCHAR(100) NULL,    -- non-NULL only when status='matched'
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_epc_prompt_id ON api.example_prompt_categorization(prompt_id);
CREATE INDEX idx_epc_category ON api.example_prompt_categorization(category);
CREATE INDEX idx_epc_status ON api.example_prompt_categorization(status);


-- ============================================================================
-- Effective Categorization FK on Example Prompt
-- ============================================================================
-- Points to the single example_prompt_categorization row that is currently
-- in effect. AI auto-sets this; admins can override.
-- ============================================================================

ALTER TABLE api.example_prompt
  ADD COLUMN effective_categorization_id UUID NULL
  REFERENCES api.example_prompt_categorization(id) ON DELETE SET NULL;

CREATE INDEX idx_example_prompt_effective_categorization
  ON api.example_prompt(effective_categorization_id);


-- ============================================================================
-- Created-by audit on Example Prompt
-- ============================================================================
-- NULL for imported prompts and rows that existed before this migration.
-- Set to the user ID of the user who created the prompt via the API.
-- ============================================================================

ALTER TABLE api.example_prompt
  ADD COLUMN created_by UUID NULL;


-- ============================================================================
-- Battle Categorization Table
-- ============================================================================
-- Same structure as example_prompt_categorization but scoped to battles.
-- Auto-triggered after a battle's biomedical validation becomes effective.
-- Admins can create manual overrides (subject to the same biomedical gate).
-- status semantics mirror example_prompt_categorization:
--   'matched'   — classifier picked at least one category (join table non-empty)
--   'abstained' — classifier ran successfully but declared no fit (empty join table)
--   'failed'    — classifier could not run (AI service / LLM error; retry-able)
-- ============================================================================

CREATE TABLE api.battle_categorization (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  battle_id        UUID NOT NULL REFERENCES api.battle(id) ON DELETE CASCADE,
  method           VARCHAR(100) NOT NULL,
  categorized_by   UUID NULL,
  reason           VARCHAR(1000) NULL,
  status           VARCHAR(20) NOT NULL, -- 'matched' | 'abstained' | 'failed'
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_bc_battle_id ON api.battle_categorization(battle_id);
CREATE INDEX idx_bc_status ON api.battle_categorization(status);


-- ============================================================================
-- Battle Categorization Category Join Table
-- ============================================================================

CREATE TABLE api.battle_categorization_category (
  categorization_id UUID         NOT NULL REFERENCES api.battle_categorization(id) ON DELETE CASCADE,
  category          VARCHAR(100) NOT NULL,
  PRIMARY KEY (categorization_id, category)
);


-- ============================================================================
-- Effective Categorization FK on Battle
-- ============================================================================

ALTER TABLE api.battle
  ADD COLUMN effective_categorization_id UUID NULL
  REFERENCES api.battle_categorization(id) ON DELETE SET NULL;

CREATE INDEX idx_battle_effective_categorization
  ON api.battle(effective_categorization_id);
