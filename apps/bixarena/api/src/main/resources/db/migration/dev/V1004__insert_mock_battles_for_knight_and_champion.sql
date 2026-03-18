-- ============================================================================
-- DEV ENVIRONMENT ONLY: Additional Mock Battles for Knight & Champion Tiers
-- ============================================================================
-- The original mock battles (V1000) mostly fall before the quest start date
-- (Feb 1, 2026), so all contributors end up as apprentices. This migration
-- adds enough validated battles within the quest period to produce:
--
-- - quest_champion_1 (user1): +70 battles → ~10.8 battles/week (Champion)
-- - quest_knight_1   (user3): +35 battles → ~5.4 battles/week  (Knight)
--
-- Battles/week = battle_count / weeks_elapsed (Feb 1 → now)
-- ============================================================================

-- User1 (quest_champion_1): 70 battles spread across Feb 1 - Mar 15
-- ~1.5 per day, 6 hours apart
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000001'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Champion Battle ' || n,
  '2026-02-01 00:00:00+00'::timestamptz + ((n * 6) || ' hours')::interval,
  '2026-02-01 00:00:00+00'::timestamptz + ((n * 6 + 1) || ' hours')::interval
FROM generate_series(1, 70) AS n;

-- User3 (quest_knight_1): 35 battles spread across Feb 1 - Mar 15
-- ~0.8 per day, 12 hours apart
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000003'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Knight Battle ' || n,
  '2026-02-01 06:00:00+00'::timestamptz + ((n * 12) || ' hours')::interval,
  '2026-02-01 06:00:00+00'::timestamptz + ((n * 12 + 1) || ' hours')::interval
FROM generate_series(1, 35) AS n;

-- Validate all newly inserted battles and set effective_validation_id
-- (Same pattern as V1003 but only for battles without a validation yet)
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
  '00000000-0000-0000-0000-000000000003'::uuid
)
AND b.ended_at IS NOT NULL
AND b.effective_validation_id IS NULL;

UPDATE api.battle b
SET effective_validation_id = bv.id
FROM api.battle_validation bv
WHERE bv.battle_id = b.id
  AND bv.method = 'mock-validation'
  AND b.effective_validation_id IS NULL
  AND b.user_id IN (
    '00000000-0000-0000-0000-000000000001'::uuid,
    '00000000-0000-0000-0000-000000000003'::uuid
  );
