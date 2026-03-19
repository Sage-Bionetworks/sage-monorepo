-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Battle Validations for Quest Contributors
-- ============================================================================
-- This migration creates battle_validation rows for all completed mock battles
-- and sets effective_validation_id on each battle so they count toward quest
-- contributor stats.
--
-- Without this, the quest contributors list appears empty because the
-- findContributorsByDateRange() query requires:
--   INNER JOIN api.battle_validation bv ON bv.id = b.effective_validation_id
--   AND bv.is_biomedical = true
-- ============================================================================

-- Step 1: Insert a battle_validation row for each completed mock battle
INSERT INTO api.battle_validation (id, battle_id, method, confidence, is_biomedical, validated_by, reason, created_at)
SELECT
  gen_random_uuid(),
  b.id,
  'mock-validation',
  0.950,
  true,
  NULL,
  'Mock validation for dev testing',
  b.ended_at
FROM api.battle b
WHERE b.user_id IN (
  '00000000-0000-0000-0000-000000000001'::uuid,
  '00000000-0000-0000-0000-000000000002'::uuid,
  '00000000-0000-0000-0000-000000000003'::uuid,
  '00000000-0000-0000-0000-000000000004'::uuid,
  '00000000-0000-0000-0000-000000000005'::uuid,
  '00000000-0000-0000-0000-000000000006'::uuid,
  '00000000-0000-0000-0000-000000000007'::uuid,
  '00000000-0000-0000-0000-000000000008'::uuid,
  '00000000-0000-0000-0000-000000000009'::uuid,
  '00000000-0000-0000-0000-000000000010'::uuid,
  '00000000-0000-0000-0000-000000000011'::uuid,
  '00000000-0000-0000-0000-000000000012'::uuid,
  '00000000-0000-0000-0000-000000000013'::uuid,
  '00000000-0000-0000-0000-000000000014'::uuid,
  '00000000-0000-0000-0000-000000000015'::uuid,
  '00000000-0000-0000-0000-000000000016'::uuid,
  '00000000-0000-0000-0000-000000000017'::uuid
)
AND b.ended_at IS NOT NULL;

-- Step 2: Set effective_validation_id on each battle to point to its validation
UPDATE api.battle b
SET effective_validation_id = bv.id
FROM api.battle_validation bv
WHERE bv.battle_id = b.id
  AND bv.method = 'mock-validation'
  AND b.user_id IN (
    '00000000-0000-0000-0000-000000000001'::uuid,
    '00000000-0000-0000-0000-000000000002'::uuid,
    '00000000-0000-0000-0000-000000000003'::uuid,
    '00000000-0000-0000-0000-000000000004'::uuid,
    '00000000-0000-0000-0000-000000000005'::uuid,
    '00000000-0000-0000-0000-000000000006'::uuid,
    '00000000-0000-0000-0000-000000000007'::uuid,
    '00000000-0000-0000-0000-000000000008'::uuid,
    '00000000-0000-0000-0000-000000000009'::uuid,
    '00000000-0000-0000-0000-000000000010'::uuid,
    '00000000-0000-0000-0000-000000000011'::uuid,
    '00000000-0000-0000-0000-000000000012'::uuid,
    '00000000-0000-0000-0000-000000000013'::uuid,
    '00000000-0000-0000-0000-000000000014'::uuid,
    '00000000-0000-0000-0000-000000000015'::uuid,
    '00000000-0000-0000-0000-000000000016'::uuid,
    '00000000-0000-0000-0000-000000000017'::uuid
  );
