-- ============================================================================
-- Prompt Validation Table
-- ============================================================================
-- Stores individual validation results for battle prompts.
-- Each row represents one validation of a prompt by a specific method
-- (e.g. 'openrouter-haiku-v1', 'bert-biomedical-v1', 'human').
-- Only the initial prompt (round 1) is validated automatically.
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


-- ============================================================================
-- Battle Validation Table
-- ============================================================================
-- Stores battle-level validation results (all user prompts considered together).
-- Each row represents one validation of a battle by a specific method and
-- optionally by a specific human reviewer.
-- Automated validations are triggered when the user votes to complete a battle.
-- Human validations are created via the admin endpoint.
-- ============================================================================

CREATE TABLE api.battle_validation (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  battle_id UUID NOT NULL REFERENCES api.battle(id) ON DELETE CASCADE,
  method VARCHAR(100) NOT NULL,
  confidence DECIMAL(4,3) NOT NULL CHECK (confidence >= 0 AND confidence <= 1),
  is_biomedical BOOLEAN NOT NULL,
  validated_by UUID,  -- NULL for automated validations, user_id for human reviews
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Prevent duplicate validations for the same battle + method + validator.
-- COALESCE handles NULL validated_by so automated validations (NULL) are unique per method.
CREATE UNIQUE INDEX idx_battle_validation_battle_method_validator
  ON api.battle_validation(battle_id, method, COALESCE(validated_by, '00000000-0000-0000-0000-000000000000'));

-- Index for finding all validations for a battle
CREATE INDEX idx_battle_validation_battle_id
  ON api.battle_validation(battle_id);


-- ============================================================================
-- Effective Validation FK on Battle
-- ============================================================================
-- Points to the single battle_validation row that determines whether a battle
-- counts in stats. AI validation is auto-set as effective; admins can override.
-- Battles without an effective validation are excluded from stats.
-- ============================================================================

ALTER TABLE api.battle
  ADD COLUMN effective_validation_id UUID REFERENCES api.battle_validation(id) ON DELETE SET NULL;

CREATE INDEX idx_battle_effective_validation
  ON api.battle(effective_validation_id);
