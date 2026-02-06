-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Quest Data for Testing
-- ============================================================================
-- This migration inserts mock quest and battle data for local development
-- and testing of the Quest Contributors feature.
--
-- Mock Users (UUIDs):
-- - user1: 00000000-0000-0000-0000-000000000001 (Champion - 50 battles, 12.5/week)
-- - user2: 00000000-0000-0000-0000-000000000002 (Champion - 40 battles, 10/week)
-- - user3: 00000000-0000-0000-0000-000000000003 (Knight - 25 battles, 6.25/week)
-- - user4: 00000000-0000-0000-0000-000000000004 (Knight - 20 battles, 5/week)
-- - user5: 00000000-0000-0000-0000-000000000005 (Apprentice - 8 battles, 2/week)
-- - user6: 00000000-0000-0000-0000-000000000006 (Apprentice - 5 battles, 1.25/week)
-- - user7: 00000000-0000-0000-0000-000000000007 (Apprentice - 2 battles, 0.5/week)
--
-- Quest Duration: 4 weeks (Jan 20 - Feb 17, 2026)
-- Expected Tiers (battles/week thresholds):
-- - Champions: ≥10 battles/week (≥40 battles in 4 weeks)
-- - Knights: ≥5 battles/week (≥20 battles in 4 weeks)
-- - Apprentices: <5 battles/week (<20 battles in 4 weeks)
-- ============================================================================

-- ============================================================================
-- Insert Mock Quest
-- ============================================================================
INSERT INTO api.quest (id, quest_id, start_date, end_date, created_at, updated_at) VALUES
  (1, 'build-bioarena-together', '2026-01-20 00:00:00+00', '2026-02-17 23:59:59+00', now(), now());


-- ============================================================================
-- Insert Mock Users for Quest Testing
-- ============================================================================
-- Note: These users are created in the auth schema for testing purposes
INSERT INTO auth.user (id, username, created_at, updated_at) VALUES
  ('00000000-0000-0000-0000-000000000001'::uuid, 'quest_champion_1', now(), now()),
  ('00000000-0000-0000-0000-000000000002'::uuid, 'quest_champion_2', now(), now()),
  ('00000000-0000-0000-0000-000000000003'::uuid, 'quest_knight_1', now(), now()),
  ('00000000-0000-0000-0000-000000000004'::uuid, 'quest_knight_2', now(), now()),
  ('00000000-0000-0000-0000-000000000005'::uuid, 'quest_apprentice_1', now(), now()),
  ('00000000-0000-0000-0000-000000000006'::uuid, 'quest_apprentice_2', now(), now()),
  ('00000000-0000-0000-0000-000000000007'::uuid, 'quest_apprentice_3', now(), now())
ON CONFLICT (id) DO NOTHING;


-- ============================================================================
-- Insert Mock Completed Battles for Quest Contributors Testing
-- ============================================================================
-- Note: Using existing model IDs from reference data (V2__insert_reference_data.sql)
-- Model1: 3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0 (GPT-4)
-- Model2: 8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11 (Claude-3.5-Sonnet)

-- User1: 50 completed battles (Champion - 12.5 battles/week over 4 weeks)
-- Distribute battles evenly over 4 weeks (~1.8 per day, 8 hours apart)
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000001'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-01-20 00:00:00+00'::timestamptz + ((n * 8) || ' hours')::interval,
  '2026-01-20 00:00:00+00'::timestamptz + ((n * 8 + 1) || ' hours')::interval
FROM generate_series(1, 50) AS n;

-- User2: 40 completed battles (Champion - 10 battles/week over 4 weeks)
-- Distribute battles evenly over 4 weeks (~1.4 per day, 10 hours apart)
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000002'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-01-20 06:00:00+00'::timestamptz + ((n * 10) || ' hours')::interval,
  '2026-01-20 06:00:00+00'::timestamptz + ((n * 10 + 1) || ' hours')::interval
FROM generate_series(1, 40) AS n;

-- User3: 25 completed battles (Knight - 6.25 battles/week over 4 weeks)
-- Distribute battles evenly over quest period (~11 hours apart)
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000003'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-01-20 12:00:00+00'::timestamptz + ((n * 11) || ' hours')::interval,
  '2026-01-20 12:00:00+00'::timestamptz + ((n * 11 + 1) || ' hours')::interval
FROM generate_series(1, 25) AS n;

-- User4: 20 completed battles (Knight - 5 battles/week over 4 weeks)
-- Distribute battles evenly over quest period (~14 hours apart)
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000004'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-01-20 18:00:00+00'::timestamptz + ((n * 14) || ' hours')::interval,
  '2026-01-20 18:00:00+00'::timestamptz + ((n * 14 + 1) || ' hours')::interval
FROM generate_series(1, 20) AS n;

-- User5: 8 completed battles (Apprentice - ~2 battles/week)
-- Distribute battles evenly over 4 weeks
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000005'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-01-21 00:00:00+00'::timestamptz + ((n * 50) || ' hours')::interval,
  '2026-01-21 00:00:00+00'::timestamptz + ((n * 50 + 1) || ' hours')::interval
FROM generate_series(1, 8) AS n;

-- User6: 5 completed battles (Apprentice - ~1.2 battles/week)
-- Distribute battles evenly over 4 weeks
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000006'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-01-21 12:00:00+00'::timestamptz + ((n * 80) || ' hours')::interval,
  '2026-01-21 12:00:00+00'::timestamptz + ((n * 80 + 1) || ' hours')::interval
FROM generate_series(1, 5) AS n;

-- User7: 2 completed battles (Apprentice - ~0.5 battles/week)
-- Distribute battles evenly over 4 weeks
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000007'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-01-22 00:00:00+00'::timestamptz + ((n * 160) || ' hours')::interval,
  '2026-01-22 00:00:00+00'::timestamptz + ((n * 160 + 1) || ' hours')::interval
FROM generate_series(1, 2) AS n;

-- Add some battles outside the quest period (should not be counted)
-- User1: 10 battles before quest start (should not count)
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000001'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Pre-Quest Battle ' || n,
  '2026-01-10 00:00:00+00'::timestamptz + (n || ' hours')::interval,
  '2026-01-10 00:00:00+00'::timestamptz + ((n + 1) || ' hours')::interval
FROM generate_series(1, 10) AS n;

-- User2: 5 incomplete battles during quest (ended_at is null, should not count)
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000002'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Incomplete Battle ' || n,
  '2026-01-27 00:00:00+00'::timestamptz + (n || ' hours')::interval,
  NULL
FROM generate_series(1, 5) AS n;
