-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Users for Quest Testing
-- ============================================================================
-- These users are used by the API service's quest mock data (V1000-V1004).
-- They must be created in the auth service since auth.user is managed here.
-- ============================================================================

INSERT INTO auth.user (id, username, created_at, updated_at) VALUES
  ('00000000-0000-0000-0000-000000000001'::uuid, 'quest_user_1', now(), now()),
  ('00000000-0000-0000-0000-000000000002'::uuid, 'quest_user_2', now(), now()),
  ('00000000-0000-0000-0000-000000000003'::uuid, 'quest_user_3', now(), now()),
  ('00000000-0000-0000-0000-000000000004'::uuid, 'quest_user_4', now(), now()),
  ('00000000-0000-0000-0000-000000000005'::uuid, 'quest_user_5', now(), now()),
  ('00000000-0000-0000-0000-000000000006'::uuid, 'quest_user_6', now(), now()),
  ('00000000-0000-0000-0000-000000000007'::uuid, 'quest_user_7', now(), now()),
  ('00000000-0000-0000-0000-000000000008'::uuid, 'quest_user_8', now(), now()),
  ('00000000-0000-0000-0000-000000000009'::uuid, 'quest_user_9', now(), now()),
  ('00000000-0000-0000-0000-000000000010'::uuid, 'quest_user_10', now(), now()),
  ('00000000-0000-0000-0000-000000000011'::uuid, 'quest_user_11', now(), now()),
  ('00000000-0000-0000-0000-000000000012'::uuid, 'quest_user_12', now(), now()),
  ('00000000-0000-0000-0000-000000000013'::uuid, 'quest_user_13', now(), now()),
  ('00000000-0000-0000-0000-000000000014'::uuid, 'quest_user_14', now(), now()),
  ('00000000-0000-0000-0000-000000000015'::uuid, 'quest_user_15', now(), now()),
  ('00000000-0000-0000-0000-000000000016'::uuid, 'quest_user_16', now(), now()),
  ('00000000-0000-0000-0000-000000000017'::uuid, 'quest_user_17', now(), now())
;
