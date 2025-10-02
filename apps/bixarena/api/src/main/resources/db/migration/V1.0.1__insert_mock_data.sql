-- Insert sample leaderboards
INSERT INTO leaderboard (id, slug, name, description, created_at, updated_at) VALUES
  ('11111111-1111-1111-1111-111111111111', 'open-source', 'Open Source Models', 'Performance ranking of open-source AI models', '2025-08-01 10:00:00+00', '2025-08-16 14:30:00+00'),
  ('22222222-2222-2222-2222-222222222222', 'commercial', 'Commercial Models', 'Performance ranking of commercial AI models', '2025-08-01 10:00:00+00', '2025-08-16 14:30:00+00');

-- Insert sample models
INSERT INTO model (id, slug, name, license, active, alias, external_link, organization, description, api_model_name, api_base, created_at, updated_at) VALUES
  ('f1111111-1111-1111-1111-111111111111', 'kimi-k2', 'Kimi K2', 'open-source', true, NULL, 'https://openrouter.ai/models/moonshotai/kimi-k2', 'Moonshot AI', 'Large-scale MoE language model by Moonshot AI with 1T parameters and 32B active. Optimized for agentic capabilities, tool use, reasoning, and code synthesis. Excels in coding, reasoning, and tool-use benchmarks with 128K context support.', 'moonshotai/kimi-k2:free', 'https://openrouter.ai/api/v1', '2025-10-01 14:30:00+00', '2025-10-01 14:30:00+00'),
  ('f2222222-2222-2222-2222-222222222222', 'deepseek-r1-0528', 'DeepSeek R1 0528', 'open-source', false, NULL, 'https://openrouter.ai/models/deepseek/deepseek-r1-0528', 'DeepSeek', 'May 28th update to the original DeepSeek R1 Performance on par with OpenAI o1, but open-sourced and with fully open reasoning tokens. It is 671B parameters in size, with 37B active in an inference pass.', 'deepseek/deepseek-r1-0528:free', 'https://openrouter.ai/api/v1', '2025-10-01 14:30:00+00', '2025-10-01 14:30:00+00'),
  ('f3333333-3333-3333-3333-333333333333', 'qwen3-235b-a22b', 'Qwen3 235B A22B', 'open-source', true, NULL, 'https://openrouter.ai/models/qwen/qwen3-235b-a22b', 'Alibaba', '235B parameter MoE model by Qwen with 22B active parameters. Features thinking/non-thinking modes for complex reasoning and general chat. Strong multilingual support (100+ languages) with 32K-131K context window.', 'qwen/qwen3-235b-a22b:free', 'https://openrouter.ai/api/v1', '2025-10-01 14:30:00+00', '2025-10-01 14:30:00+00'),
  ('f4444444-4444-4444-4444-444444444444', 'deepseek-chat-v3-0324', 'DeepSeek Chat V3 0324', 'open-source', true, NULL, 'https://openrouter.ai/models/deepseek/deepseek-chat-v3-0324', 'DeepSeek', 'DeepSeek V3, a 685B-parameter, mixture-of-experts model, is the latest iteration of the flagship chat model family from the DeepSeek team. It succeeds the DeepSeek V3 model and performs really well on a variety of tasks.', 'deepseek/deepseek-chat-v3-0324:free', 'https://openrouter.ai/api/v1', '2025-10-01 14:30:00+00', '2025-10-01 14:30:00+00'),
  ('f5555555-5555-5555-5555-555555555555', 'deepseek-r1', 'DeepSeek R1', 'open-source', true, NULL, 'https://openrouter.ai/models/deepseek/deepseek-r1', 'DeepSeek', 'DeepSeek R1 is here: Performance on par with OpenAI o1, but open-sourced and with fully open reasoning tokens. It is 671B parameters in size, with 37B active in an inference pass.', 'deepseek/deepseek-r1:free', 'https://openrouter.ai/api/v1', '2025-10-01 14:30:00+00', '2025-10-01 14:30:00+00');

-- Insert current snapshots
INSERT INTO leaderboard_snapshot (id, leaderboard_id, snapshot_identifier, description, created_at) VALUES
  ('f0000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'snapshot_2025-08-16_14-30', 'Weekly evaluation run', '2025-08-16 14:30:00+00'),
  ('f0000000-0000-0000-0000-000000000002', '22222222-2222-2222-2222-222222222222', 'snapshot_2025-08-16_14-30', 'Weekly evaluation run', '2025-08-16 14:30:00+00'),
  ('f0000000-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'snapshot_2025-08-09_14-30', 'Previous week evaluation', '2025-08-09 14:30:00+00'),
  ('f0000000-0000-0000-0000-000000000004', '22222222-2222-2222-2222-222222222222', 'snapshot_2025-08-09_14-30', 'Previous week evaluation', '2025-08-09 14:30:00+00');

-- Insert current leaderboard entries for open-source models
INSERT INTO leaderboard_entry (id, leaderboard_id, model_id, snapshot_id, bt_score, vote_count, rank, secondary_score, created_at) VALUES
  ('e1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'f0000000-0000-0000-0000-000000000001', 0.925, 1250, 1, 0.887, '2025-08-16 14:30:00+00'),
  ('e1111111-1111-1111-1111-111111111112', '11111111-1111-1111-1111-111111111111', 'f3333333-3333-3333-3333-333333333333', 'f0000000-0000-0000-0000-000000000001', 0.918, 1180, 2, 0.875, '2025-08-16 14:30:00+00');


-- Insert historical entries (previous week)
INSERT INTO leaderboard_entry (id, leaderboard_id, model_id, snapshot_id, bt_score, vote_count, rank, secondary_score, created_at) VALUES
  ('e1111111-1111-1111-1111-111111111113', '11111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'f0000000-0000-0000-0000-000000000003', 0.915, 1180, 1, 0.875, '2025-08-09 14:30:00+00'),
  ('e1111111-1111-1111-1111-111111111114', '11111111-1111-1111-1111-111111111111', 'f3333333-3333-3333-3333-333333333333', 'f0000000-0000-0000-0000-000000000003', 0.908, 1120, 2, 0.865, '2025-08-09 14:30:00+00'),
  ('e2222222-2222-2222-2222-222222222224', '22222222-2222-2222-2222-222222222222', 'f4444444-4444-4444-4444-444444444444', 'f0000000-0000-0000-0000-000000000004', 0.935, 1400, 1, 0.902, '2025-08-09 14:30:00+00'),
  ('e2222222-2222-2222-2222-222222222225', '22222222-2222-2222-2222-222222222222', 'f5555555-5555-5555-5555-555555555555', 'f0000000-0000-0000-0000-000000000004', 0.928, 1380, 2, 0.895, '2025-08-09 14:30:00+00');