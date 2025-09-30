-- Insert sample leaderboards
INSERT INTO leaderboard (id, slug, name, description, created_at, updated_at) VALUES
  ('11111111-1111-1111-1111-111111111111', 'open-source', 'Open Source Models', 'Performance ranking of open-source AI models', '2025-08-01 10:00:00+00', '2025-08-16 14:30:00+00'),
  ('22222222-2222-2222-2222-222222222222', 'commercial', 'Commercial Models', 'Performance ranking of commercial AI models', '2025-08-01 10:00:00+00', '2025-08-16 14:30:00+00');

-- Insert sample models
INSERT INTO model (id, slug, name, license, organization, description, external_link, api_model_name, api_base, api_type, visible, created_at, updated_at) VALUES
  ('f1111111-1111-1111-1111-111111111111', 'kimi-k2', 'Kimi K2', 'Open Source', 'Moonshot AI', 'Kimi K2 model by Moonshot AI', 'https://openrouter.ai/models/moonshotai/kimi-k2', 'moonshotai/kimi-k2:free', 'https://openrouter.ai/api/v1', 'openai', true, '2025-09-30 14:30:00+00', '2025-09-30 14:30:00+00'),
  ('f2222222-2222-2222-2222-222222222222', 'deepseek-r1-0528', 'DeepSeek R1 (0528)', 'Open Source', 'DeepSeek', 'DeepSeek R1 model from May 28th', 'https://openrouter.ai/models/deepseek/deepseek-r1-0528', 'deepseek/deepseek-r1-0528:free', 'https://openrouter.ai/api/v1', 'openai', false, '2025-09-30 14:30:00+00', '2025-09-30 14:30:00+00'),
  ('f3333333-3333-3333-3333-333333333333', 'qwen3-235b-a22b', 'Qwen 3 235B', 'Open Source', 'Alibaba', 'Qwen 3 235B parameter model', 'https://openrouter.ai/models/qwen/qwen3-235b-a22b', 'qwen/qwen3-235b-a22b:free', 'https://openrouter.ai/api/v1', 'openai', true, '2025-09-30 14:30:00+00', '2025-09-30 14:30:00+00'),
  ('f4444444-4444-4444-4444-444444444444', 'deepseek-chat-v3-0324', 'DeepSeek Chat v3 (0324)', 'Open Source', 'DeepSeek', 'DeepSeek Chat v3 from March 24th', 'https://openrouter.ai/models/deepseek/deepseek-chat-v3-0324', 'deepseek/deepseek-chat-v3-0324:free', 'https://openrouter.ai/api/v1', 'openai', true, '2025-09-30 14:30:00+00', '2025-09-30 14:30:00+00'),
  ('f5555555-5555-5555-5555-555555555555', 'deepseek-r1', 'DeepSeek R1', 'Open Source', 'DeepSeek', 'DeepSeek R1 latest model', 'https://openrouter.ai/models/deepseek/deepseek-r1', 'deepseek/deepseek-r1:free', 'https://openrouter.ai/api/v1', 'openai', true, '2025-09-30 14:30:00+00', '2025-09-30 14:30:00+00');

-- Insert current snapshots
INSERT INTO leaderboard_snapshot (id, leaderboard_id, snapshot_identifier, description, created_at) VALUES
  ('f0000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'snapshot_2025-08-16_14-30', 'Weekly evaluation run', '2025-08-16 14:30:00+00'),
  ('f0000000-0000-0000-0000-000000000002', '22222222-2222-2222-2222-222222222222', 'snapshot_2025-08-16_14-30', 'Weekly evaluation run', '2025-08-16 14:30:00+00'),
  ('f0000000-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'snapshot_2025-08-09_14-30', 'Previous week evaluation', '2025-08-09 14:30:00+00'),
  ('f0000000-0000-0000-0000-000000000004', '22222222-2222-2222-2222-222222222222', 'snapshot_2025-08-09_14-30', 'Previous week evaluation', '2025-08-09 14:30:00+00');

-- Insert current leaderboard entries for open-source models
INSERT INTO leaderboard_entry (id, leaderboard_id, model_id, snapshot_id, bt_score, vote_count, rank, secondary_score, created_at) VALUES
  ('e1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'f0000000-0000-0000-0000-000000000001', 0.925, 1250, 1, 0.887, '2025-08-16 14:30:00+00'),
  ('e1111111-1111-1111-1111-111111111112', '11111111-1111-1111-1111-111111111111', 'f2222222-2222-2222-2222-222222222222', 'f0000000-0000-0000-0000-000000000001', 0.918, 1180, 2, 0.875, '2025-08-16 14:30:00+00');

-- Insert current leaderboard entries for commercial models
-- INSERT INTO leaderboard_entry (id, leaderboard_id, model_id, snapshot_id, bt_score, vote_count, rank, secondary_score, created_at) VALUES
--   ('e2222222-2222-2222-2222-222222222221', '22222222-2222-2222-2222-222222222222', 'f3333333-3333-3333-3333-333333333333', 'f0000000-0000-0000-0000-000000000002', 0.945, 1500, 1, 0.912, '2025-08-16 14:30:00+00'),
--   ('e2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'f4444444-4444-4444-4444-444444444444', 'f0000000-0000-0000-0000-000000000002', 0.938, 1420, 2, 0.905, '2025-08-16 14:30:00+00'),
--   ('e2222222-2222-2222-2222-222222222223', '22222222-2222-2222-2222-222222222222', 'f5555555-5555-5555-5555-555555555555', 'f0000000-0000-0000-0000-000000000002', 0.932, 1350, 3, 0.898, '2025-08-16 14:30:00+00');

-- Insert historical entries (previous week)
INSERT INTO leaderboard_entry (id, leaderboard_id, model_id, snapshot_id, bt_score, vote_count, rank, secondary_score, created_at) VALUES
  ('e1111111-1111-1111-1111-111111111113', '11111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'f0000000-0000-0000-0000-000000000003', 0.915, 1180, 1, 0.875, '2025-08-09 14:30:00+00'),
  ('e1111111-1111-1111-1111-111111111114', '11111111-1111-1111-1111-111111111111', 'f2222222-2222-2222-2222-222222222222', 'f0000000-0000-0000-0000-000000000003', 0.908, 1120, 2, 0.865, '2025-08-09 14:30:00+00'),
  ('e2222222-2222-2222-2222-222222222224', '22222222-2222-2222-2222-222222222222', 'f3333333-3333-3333-3333-333333333333', 'f0000000-0000-0000-0000-000000000004', 0.935, 1400, 1, 0.902, '2025-08-09 14:30:00+00'),
  ('e2222222-2222-2222-2222-222222222225', '22222222-2222-2222-2222-222222222222', 'f4444444-4444-4444-4444-444444444444', 'f0000000-0000-0000-0000-000000000004', 0.928, 1380, 2, 0.895, '2025-08-09 14:30:00+00');