-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Quest Data for Testing
-- ============================================================================
-- This migration inserts mock quest and battle data for local development
-- and testing of the Quest Contributors feature.
--
-- Mock Users (UUIDs):
-- - user1:  00000000-0000-0000-0000-000000000001 (50 battles)
-- - user2:  00000000-0000-0000-0000-000000000002 (40 battles)
-- - user3:  00000000-0000-0000-0000-000000000003 (25 battles)
-- - user4:  00000000-0000-0000-0000-000000000004 (20 battles)
-- - user5:  00000000-0000-0000-0000-000000000005 (8 battles)
-- - user6:  00000000-0000-0000-0000-000000000006 (5 battles)
-- - user7:  00000000-0000-0000-0000-000000000007 (2 battles)
-- - user8:  00000000-0000-0000-0000-000000000008 (3 battles)
-- - user9:  00000000-0000-0000-0000-000000000009 (2 battles)
-- - user10: 00000000-0000-0000-0000-000000000010 (3 battles)
-- - user11: 00000000-0000-0000-0000-000000000011 (1 battle)
-- - user12: 00000000-0000-0000-0000-000000000012 (2 battles)
-- - user13: 00000000-0000-0000-0000-000000000013 (3 battles)
-- - user14: 00000000-0000-0000-0000-000000000014 (1 battle)
-- - user15: 00000000-0000-0000-0000-000000000015 (2 battles)
-- - user16: 00000000-0000-0000-0000-000000000016 (1 battle)
-- - user17: 00000000-0000-0000-0000-000000000017 (2 battles)
--
-- Quest Duration: ~3 months (Feb 1 - Apr 30, 2026)
-- Tier thresholds (battles/week): Champion ≥10, Knight ≥5, Apprentice <5
-- Note: Tiers are dynamic — they depend on battles/week computed over elapsed time
-- ============================================================================

-- ============================================================================
-- Insert Mock Quest
-- ============================================================================
INSERT INTO api.quest (id, quest_id, start_date, end_date, title, description, goal, active_post_index, created_at, updated_at) VALUES
  (1, 'build-bioarena-together', '2026-02-01 00:00:00+00', '2026-04-30 23:59:59+00',
   'Build BioArena Together',
   'We are constructing a medieval arena in Minecraft to symbolize our collective effort. Every battle counts towards the build.',
   2850, 0, now(), now());


-- Note: Mock quest users are created by the auth-service dev migration
-- (V1000__insert_mock_quest_users.sql) since auth.user is managed there.

-- ============================================================================
-- Insert Mock Completed Battles for Quest Contributors Testing
-- ============================================================================
-- Note: Using existing model IDs from reference data (V2__insert_reference_data.sql)
-- Model1: 3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0 (GPT-4)
-- Model2: 8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11 (Claude-3.5-Sonnet)

-- User1: 50 completed battles (~1.8 per day, 8 hours apart)
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

-- User2: 40 completed battles (~1.4 per day, 10 hours apart)
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

-- User3: 25 completed battles (~11 hours apart)
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

-- User4: 20 completed battles (~14 hours apart)
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

-- User5: 8 completed battles (~50 hours apart)
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

-- User6: 5 completed battles (~80 hours apart)
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

-- User7: 2 completed battles (~160 hours apart)
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

-- ============================================================================
-- Users 8-14: Additional mock users with a few battles each (within quest period)
-- ============================================================================

-- User8: 3 battles
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000008'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-02-03 00:00:00+00'::timestamptz + ((n * 72) || ' hours')::interval,
  '2026-02-03 00:00:00+00'::timestamptz + ((n * 72 + 1) || ' hours')::interval
FROM generate_series(1, 3) AS n;

-- User9: 2 battles
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000009'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-02-05 00:00:00+00'::timestamptz + ((n * 96) || ' hours')::interval,
  '2026-02-05 00:00:00+00'::timestamptz + ((n * 96 + 1) || ' hours')::interval
FROM generate_series(1, 2) AS n;

-- User10: 3 battles
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000010'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-02-07 00:00:00+00'::timestamptz + ((n * 60) || ' hours')::interval,
  '2026-02-07 00:00:00+00'::timestamptz + ((n * 60 + 1) || ' hours')::interval
FROM generate_series(1, 3) AS n;

-- User11: 1 battle
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
VALUES (
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000011'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle 1',
  '2026-02-10 00:00:00+00'::timestamptz,
  '2026-02-10 01:00:00+00'::timestamptz
);

-- User12: 2 battles
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000012'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-02-12 00:00:00+00'::timestamptz + ((n * 100) || ' hours')::interval,
  '2026-02-12 00:00:00+00'::timestamptz + ((n * 100 + 1) || ' hours')::interval
FROM generate_series(1, 2) AS n;

-- User13: 3 battles
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000013'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-02-14 00:00:00+00'::timestamptz + ((n * 80) || ' hours')::interval,
  '2026-02-14 00:00:00+00'::timestamptz + ((n * 80 + 1) || ' hours')::interval
FROM generate_series(1, 3) AS n;

-- User14: 1 battle
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
VALUES (
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000014'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle 1',
  '2026-02-16 00:00:00+00'::timestamptz,
  '2026-02-16 01:00:00+00'::timestamptz
);

-- User15: 2 battles
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000015'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-02-18 00:00:00+00'::timestamptz + ((n * 90) || ' hours')::interval,
  '2026-02-18 00:00:00+00'::timestamptz + ((n * 90 + 1) || ' hours')::interval
FROM generate_series(1, 2) AS n;

-- User16: 1 battle
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
VALUES (
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000016'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle 1',
  '2026-02-20 00:00:00+00'::timestamptz,
  '2026-02-20 01:00:00+00'::timestamptz
);

-- User17: 2 battles
INSERT INTO api.battle (id, user_id, model1_id, model2_id, title, created_at, ended_at)
SELECT
  gen_random_uuid(),
  '00000000-0000-0000-0000-000000000017'::uuid,
  '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0'::uuid,
  '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11'::uuid,
  'Quest Battle ' || n,
  '2026-02-22 00:00:00+00'::timestamptz + ((n * 110) || ' hours')::interval,
  '2026-02-22 00:00:00+00'::timestamptz + ((n * 110 + 1) || ' hours')::interval
FROM generate_series(1, 2) AS n;
