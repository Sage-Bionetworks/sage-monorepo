-- Insert sample leaderboards
INSERT INTO leaderboard (id, slug, name, description, created_at, updated_at) VALUES
  ('11111111-1111-1111-1111-111111111111', 'open-source', 'Open Source Models', 'Performance ranking of open-source AI models', '2025-08-01 10:00:00+00', '2025-08-16 14:30:00+00'),
  ('22222222-2222-2222-2222-222222222222', 'commercial', 'Commercial Models', 'Performance ranking of commercial AI models', '2025-08-01 10:00:00+00', '2025-08-16 14:30:00+00');

-- Insert sample models
INSERT INTO model (id, slug, name, license, created_at, updated_at) VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'gpt-4-turbo', 'GPT-4 Turbo', 'Commercial', '2025-07-15 09:00:00+00', '2025-08-10 12:00:00+00'),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'claude-3-opus', 'Claude 3 Opus', 'Commercial', '2025-07-20 10:30:00+00', '2025-08-12 15:30:00+00'),
  ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'llama-2-70b', 'Llama 2 70B', 'MIT', '2025-07-10 08:15:00+00', '2025-08-14 11:45:00+00'),
  ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'mixtral-8x7b', 'Mixtral 8x7B', 'Apache 2.0', '2025-07-25 14:20:00+00', '2025-08-15 16:10:00+00'),
  ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'gemini-pro', 'Gemini Pro', 'Commercial', '2025-08-05 11:00:00+00', '2025-08-16 13:20:00+00');

-- Insert current snapshots
INSERT INTO leaderboard_snapshot (id, leaderboard_id, snapshot_identifier, description, created_at) VALUES
  ('f0000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'snapshot_2025-08-16_14-30', 'Weekly evaluation run', '2025-08-16 14:30:00+00'),
  ('f0000000-0000-0000-0000-000000000002', '22222222-2222-2222-2222-222222222222', 'snapshot_2025-08-16_14-30', 'Weekly evaluation run', '2025-08-16 14:30:00+00'),
  ('f0000000-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'snapshot_2025-08-09_14-30', 'Previous week evaluation', '2025-08-09 14:30:00+00'),
  ('f0000000-0000-0000-0000-000000000004', '22222222-2222-2222-2222-222222222222', 'snapshot_2025-08-09_14-30', 'Previous week evaluation', '2025-08-09 14:30:00+00');

-- Insert current leaderboard entries for open-source models
INSERT INTO leaderboard_entry (id, leaderboard_id, model_id, snapshot_id, bt_score, vote_count, rank, secondary_score, created_at) VALUES
  ('e1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'f0000000-0000-0000-0000-000000000001', 0.925, 1250, 1, 0.887, '2025-08-16 14:30:00+00'),
  ('e1111111-1111-1111-1111-111111111112', '11111111-1111-1111-1111-111111111111', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'f0000000-0000-0000-0000-000000000001', 0.918, 1180, 2, 0.875, '2025-08-16 14:30:00+00');

-- Insert current leaderboard entries for commercial models
INSERT INTO leaderboard_entry (id, leaderboard_id, model_id, snapshot_id, bt_score, vote_count, rank, secondary_score, created_at) VALUES
  ('e2222222-2222-2222-2222-222222222221', '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'f0000000-0000-0000-0000-000000000002', 0.945, 1500, 1, 0.912, '2025-08-16 14:30:00+00'),
  ('e2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'f0000000-0000-0000-0000-000000000002', 0.938, 1420, 2, 0.905, '2025-08-16 14:30:00+00'),
  ('e2222222-2222-2222-2222-222222222223', '22222222-2222-2222-2222-222222222222', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'f0000000-0000-0000-0000-000000000002', 0.932, 1350, 3, 0.898, '2025-08-16 14:30:00+00');

-- Insert historical entries (previous week)
INSERT INTO leaderboard_entry (id, leaderboard_id, model_id, snapshot_id, bt_score, vote_count, rank, secondary_score, created_at) VALUES
  ('e1111111-1111-1111-1111-111111111113', '11111111-1111-1111-1111-111111111111', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'f0000000-0000-0000-0000-000000000003', 0.915, 1180, 1, 0.875, '2025-08-09 14:30:00+00'),
  ('e1111111-1111-1111-1111-111111111114', '11111111-1111-1111-1111-111111111111', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'f0000000-0000-0000-0000-000000000003', 0.908, 1120, 2, 0.865, '2025-08-09 14:30:00+00'),
  ('e2222222-2222-2222-2222-222222222224', '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'f0000000-0000-0000-0000-000000000004', 0.935, 1400, 1, 0.902, '2025-08-09 14:30:00+00'),
  ('e2222222-2222-2222-2222-222222222225', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'f0000000-0000-0000-0000-000000000004', 0.928, 1380, 2, 0.895, '2025-08-09 14:30:00+00');