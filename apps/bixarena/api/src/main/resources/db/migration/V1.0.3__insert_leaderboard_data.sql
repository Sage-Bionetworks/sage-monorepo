-- Insert overview leaderboard
INSERT INTO api.leaderboard (id, slug, name, description, created_at, updated_at) VALUES
  ('11111111-1111-1111-1111-111111111111', 'overview', 'Overview', 'Overall performance ranking of all LLMs', '2025-08-01 10:00:00+00', '2025-08-16 14:30:00+00');

-- Insert current snapshots
INSERT INTO api.leaderboard_snapshot (id, leaderboard_id, snapshot_identifier, description, created_at) VALUES
  ('f0000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'snapshot_2025-08-16_14-30', 'Weekly evaluation run', '2025-08-16 14:30:00+00'),
  ('f0000000-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'snapshot_2025-08-09_14-30', 'Previous week evaluation', '2025-08-09 14:30:00+00');

-- Insert current leaderboard entries for overview leaderboard
INSERT INTO api.leaderboard_entry (id, leaderboard_id, model_id, snapshot_id, bt_score, vote_count, rank, bootstrap_q025, bootstrap_q975, created_at) VALUES
  ('e1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0', 'f0000000-0000-0000-0000-000000000001', 0.925, 1250, 1, 887.000, 963.000, '2025-08-16 14:30:00+00'),
  ('e1111111-1111-1111-1111-111111111112', '11111111-1111-1111-1111-111111111111', '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11', 'f0000000-0000-0000-0000-000000000001', 0.918, 1180, 2, 875.000, 961.000, '2025-08-16 14:30:00+00');


-- Insert historical entries (previous week)
INSERT INTO api.leaderboard_entry (id, leaderboard_id, model_id, snapshot_id, bt_score, vote_count, rank, bootstrap_q025, bootstrap_q975, created_at) VALUES
  ('e1111111-1111-1111-1111-111111111113', '11111111-1111-1111-1111-111111111111', '3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0', 'f0000000-0000-0000-0000-000000000003', 0.915, 1180, 1, 875.000, 955.000, '2025-08-09 14:30:00+00'),
  ('e1111111-1111-1111-1111-111111111114', '11111111-1111-1111-1111-111111111111', '8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11', 'f0000000-0000-0000-0000-000000000003', 0.908, 1120, 2, 865.000, 951.000, '2025-08-09 14:30:00+00');