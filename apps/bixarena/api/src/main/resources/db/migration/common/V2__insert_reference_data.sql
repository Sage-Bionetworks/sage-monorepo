-- ============================================================================
-- Reference Data: Required for Application Functionality
-- ============================================================================
-- This migration inserts reference data needed for the application to function
-- properly in ALL environments (dev, stage, prod).
--
-- Includes:
-- - Models: LLM model definitions for evaluation
-- - Example Prompts: Sample questions for user guidance
-- - Leaderboard Definition: Overall leaderboard configuration
-- ============================================================================

-- ============================================================================
-- Insert Model Records
-- ============================================================================
INSERT INTO api.model (id, slug, name, license, active, alias, external_link, organization, description, api_model_name, api_base, created_at, updated_at) VALUES
('8b7b9c2a-5b41-4a4f-9a7b-6c2b5f3e9d11', 'chatgpt-4o-latest', 'OpenAI: ChatGPT-4o Latest', 'commercial', true, NULL, 'https://openrouter.ai/models/openai/chatgpt-4o-latest', 'OpenAI', NULL, 'openai/chatgpt-4o-latest', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('e1c3a2d4-7f8a-4c21-9b3e-4d5f6a7b8c90', 'claude-opus-4', 'Anthropic: Claude Opus 4', 'commercial', true, NULL, 'https://openrouter.ai/models/anthropic/claude-opus-4', 'Anthropic', NULL, 'anthropic/claude-opus-4', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('a9d5c4b3-2e17-4f6a-8b9c-1d2e3f4a5b6c', 'claude-opus-4.1', 'Anthropic: Claude Opus 4.1', 'commercial', true, NULL, 'https://openrouter.ai/models/anthropic/claude-opus-4.1', 'Anthropic', NULL, 'anthropic/claude-opus-4.1', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('f0a1b2c3-d4e5-46f7-98a9-b0c1d2e3f4a5', 'claude-sonnet-4.5', 'Anthropic: Claude Sonnet 4.5', 'commercial', true, NULL, 'https://openrouter.ai/models/anthropic/claude-sonnet-4.5', 'Anthropic', NULL, 'anthropic/claude-sonnet-4.5', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('bb7f2e1c-3a45-4d67-8f90-1a2b3c4d5e6f', 'deepseek-chat-v3.1', 'DeepSeek: DeepSeek V3.1', 'commercial', true, NULL, 'https://openrouter.ai/models/deepseek/deepseek-chat-v3.1', 'DeepSeek', NULL, 'deepseek/deepseek-chat-v3.1', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('c6d5e4f3-a2b1-4c0d-9e8f-7a6b5c4d3e2f', 'deepseek-r1-0528', 'DeepSeek: DeepSeek R1 0528', 'commercial', true, NULL, 'https://openrouter.ai/models/deepseek/deepseek-r1-0528', 'DeepSeek', NULL, 'deepseek/deepseek-r1-0528', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('1a2b3c4d-5e6f-4789-a0b1-c2d3e4f5a6b7', 'deepseek-v3.1-terminus', 'DeepSeek: DeepSeek V3.1 Terminus', 'commercial', true, NULL, 'https://openrouter.ai/models/deepseek/deepseek-v3.1-terminus', 'DeepSeek', NULL, 'deepseek/deepseek-v3.1-terminus', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('2b3c4d5e-6f70-4a81-92b3-c4d5e6f7a8b9', 'deepseek-v3.2-exp', 'DeepSeek: DeepSeek V3.2 Exp', 'commercial', true, NULL, 'https://openrouter.ai/models/deepseek/deepseek-v3.2-exp', 'DeepSeek', NULL, 'deepseek/deepseek-v3.2-exp', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('3c4d5e6f-7081-4a92-b3c4-d5e6f7a8b9c0', 'gemini-2.5-pro', 'Google: Gemini 2.5 Pro', 'commercial', true, NULL, 'https://openrouter.ai/models/google/gemini-2.5-pro', 'Google', NULL, 'google/gemini-2.5-pro', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('4d5e6f70-8192-4a03-b4c5-d6e7f8a9b0c1', 'glm-4.6', 'Z.AI: GLM 4.6', 'commercial', true, NULL, 'https://openrouter.ai/models/z-ai/glm-4.6', 'Zhipu AI', NULL, 'z-ai/glm-4.6', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('5e6f7081-92a0-4b13-c4d5-e6f7a8b9c0d1', 'gpt-4.1', 'OpenAI: GPT-4.1', 'commercial', true, NULL, 'https://openrouter.ai/models/openai/gpt-4.1', 'OpenAI', NULL, 'openai/gpt-4.1', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('6f708192-a0b1-4c23-d4e5-f6a7b8c9d0e1', 'gpt-5-chat', 'OpenAI: GPT-5 Chat', 'commercial', true, NULL, 'https://openrouter.ai/models/openai/gpt-5-chat', 'OpenAI', NULL, 'openai/gpt-5-chat', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('708192a0-b1c2-4d34-e5f6-a7b8c9d0e1f2', 'grok-4', 'xAI: Grok 4', 'commercial', true, NULL, 'https://openrouter.ai/models/x-ai/grok-4', 'xAI', NULL, 'x-ai/grok-4', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('8192a0b1-c2d3-4e45-f6a7-b8c9d0e1f2a3', 'grok-4-fast', 'xAI: Grok 4 Fast', 'commercial', true, NULL, 'https://openrouter.ai/models/x-ai/grok-4-fast', 'xAI', NULL, 'x-ai/grok-4-fast', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('92a0b1c2-d3e4-4f56-a7b8-c9d0e1f2a3b4', 'kimi-k2', 'MoonshotAI: Kimi K2', 'commercial', true, NULL, 'https://openrouter.ai/models/moonshotai/kimi-k2', 'Moonshot AI', NULL, 'moonshotai/kimi-k2', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('a0b1c2d3-e4f5-4067-a8b9-c0d1e2f3a4b5', 'kimi-k2-0905', 'MoonshotAI: Kimi K2 0905', 'commercial', true, NULL, 'https://openrouter.ai/models/moonshotai/kimi-k2-0905', 'Moonshot AI', NULL, 'moonshotai/kimi-k2-0905', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('b1c2d3e4-f5a6-4178-b9c0-d1e2f3a4b5c6', 'o3', 'OpenAI: o3', 'commercial', true, NULL, 'https://openrouter.ai/models/openai/o3', 'OpenAI', NULL, 'openai/o3', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('c2d3e4f5-a6b7-4289-c0d1-e2f3a4b5c6d7', 'qwen3-235b-a22b-2507', 'Qwen: Qwen3 235B A22B 2507', 'open-source', true, NULL, 'https://openrouter.ai/models/qwen/qwen3-235b-a22b-2507', 'Qwen', NULL, 'qwen/qwen3-235b-a22b-2507', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('d3e4f5a6-b7c8-439a-d1e2-f3a4b5c6d7e8', 'qwen3-max', 'Qwen: Qwen3 Max', 'commercial', true, NULL, 'https://openrouter.ai/models/qwen/qwen3-max', 'Qwen', NULL, 'qwen/qwen3-max', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00'),
('e4f5a6b7-c8d9-44ab-e2f3-a4b5c6d7e8f9', 'qwen3-vl-235b-a22b-instruct', 'Qwen: Qwen3 VL 235B A22B Instruct', 'open-source', true, NULL, 'https://openrouter.ai/models/qwen/qwen3-vl-235b-a22b-instruct', 'Qwen', NULL, 'qwen/qwen3-vl-235b-a22b-instruct', 'https://openrouter.ai/api/v1', '2025-10-30 00:00:00+00', '2025-10-30 00:00:00+00');


-- ============================================================================
-- Insert Example Prompts
-- ============================================================================
-- Insert example prompt records (sorted alphabetically by question)
INSERT INTO api.example_prompt (id, question, source, active, created_at) VALUES
  ('01111111-1111-1111-1111-111111111111', 'Are serum leptin levels a prognostic factor in advanced lung cancer?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('0aaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Can the condition of the cell microenvironment of mediastinal lymph nodes help predict the risk of metastases in non-small cell lung cancer?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('03333333-3333-3333-3333-333333333333', 'Compare and contrast marker-gene versus whole-genome alignment approaches for metagenomic taxonomic profiling.', 'bixarena', TRUE, '2025-10-30 00:00:00+00'),
  ('04444444-4444-4444-4444-444444444444', 'Do mutations causing low HDL-C promote increased carotid intima-media thickness?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('02222222-2222-2222-2222-222222222222', 'Do preoperative serum C-reactive protein levels predict the definitive pathological stage in patients with clinically localized prostate cancer?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('06666666-6666-6666-6666-666666666666', 'Does HER2 immunoreactivity provide prognostic information in locally advanced urothelial carcinoma patients receiving adjuvant M-VEC chemotherapy?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('05555555-5555-5555-5555-555555555555', 'Does hypoglycaemia increase the risk of cardiovascular events?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('07777777-7777-7777-7777-777777777777', 'Does the lipid-lowering peroxisome proliferator-activated receptors ligand bezafibrate prevent colon cancer in patients with coronary artery disease?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('08888888-8888-8888-8888-888888888888', 'HIF1A as a major vascular endothelial growth factor regulator: do its polymorphisms have an association with age-related macular degeneration?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('09999999-9999-9999-9999-999999999999', 'How would you compute the Bonferroni corrected p-value for a genetics study?', 'bixarena', TRUE, '2025-10-30 00:00:00+00'),
  ('0fffffff-ffff-ffff-ffff-ffffffffffff', 'I need to convert a set of VCF files into a PLINK-formatted .bed, .bim, and .fam file set. What is the command-line tool and syntax to do this correctly?', 'bixarena', TRUE, '2025-10-30 00:00:00+00'),
  ('12222222-2222-2222-2222-222222222222', 'Is Alveolar Macrophage Phagocytic Dysfunction in Children With Protracted Bacterial Bronchitis a Forerunner to Bronchiectasis?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('13333333-3333-3333-3333-333333333333', 'Is CA72-4 a useful biomarker in differential diagnosis between ovarian endometrioma and epithelial ovarian cancer?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('10000000-0000-0000-0000-000000000000', 'Is first-line single-agent mitoxantrone in the treatment of high-risk metastatic breast cancer patients as effective as combination chemotherapy?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('14444444-4444-4444-4444-444444444444', 'Is human cytomegalovirus infection associated with hypertension?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('15555555-5555-5555-5555-555555555555', 'Is late-night salivary cortisol a better screening test for possible cortisol excess than standard screening tests in obese patients with Type 2 diabetes?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('16666666-6666-6666-6666-666666666666', 'Is non-HDL-cholesterol a better predictor of long-term outcome in patients after acute myocardial infarction compared to LDL-cholesterol?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('11111111-1111-1111-1111-111111111111', 'Is there a uniform basal endometrial gene expression profile during the implantation window in women who became pregnant in a subsequent ICSI cycle?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00'),
  ('19999999-9999-9999-9999-999999999999', 'What are the key differences between whole-genome sequencing (WGS) and whole-exome sequencing (WES)?', 'bixarena', TRUE, '2025-10-30 00:00:00+00'),
  ('1fffffff-ffff-ffff-ffff-ffffffffffff', 'Women with synchronous primary cancers of the endometrium and ovary: do they have Lynch syndrome?', 'pubmedqa', TRUE, '2025-10-30 00:00:00+00');


-- ============================================================================
-- Insert Leaderboard Definition
-- ============================================================================
-- Insert overall leaderboard (actual data will be populated by evaluation runs)
INSERT INTO api.leaderboard (id, slug, name, description, created_at, updated_at) VALUES
  ('11111111-1111-1111-1111-111111111111', 'overall', 'Overall', 'Overall performance ranking of all LLMs', '2025-08-01 10:00:00+00', '2025-08-16 14:30:00+00');
