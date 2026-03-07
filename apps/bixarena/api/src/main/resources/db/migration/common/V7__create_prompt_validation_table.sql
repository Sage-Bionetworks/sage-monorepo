-- ============================================================================
-- Prompt Validation Table
-- ============================================================================
-- Stores individual validation results for battle prompts.
-- Each row represents one validation of a prompt by a specific method
-- (e.g. 'openrouter-haiku-v1', 'bert-biomedical-v1', 'human').
-- Only the initial prompt (round 1) is validated.
-- ============================================================================

CREATE TABLE api.prompt_validation (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  battle_id UUID NOT NULL REFERENCES api.battle(id) ON DELETE CASCADE,
  message_id UUID NOT NULL REFERENCES api.message(id) ON DELETE CASCADE,
  method VARCHAR(100) NOT NULL,
  confidence DECIMAL(4,3) NOT NULL CHECK (confidence >= 0 AND confidence <= 1),
  is_biomedical BOOLEAN NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Prevent duplicate validations for the same battle + method
CREATE UNIQUE INDEX idx_prompt_validation_battle_method
  ON api.prompt_validation(battle_id, method);

-- Index for finding all validations for a battle
CREATE INDEX idx_prompt_validation_battle_id
  ON api.prompt_validation(battle_id);

-- Index for finding all validations for a specific message
CREATE INDEX idx_prompt_validation_message_id
  ON api.prompt_validation(message_id);
