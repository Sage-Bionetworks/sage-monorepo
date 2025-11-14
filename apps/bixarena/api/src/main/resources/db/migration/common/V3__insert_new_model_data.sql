-- ============================================================================
-- Reference Data: Add GPT-5.1 Model
-- ============================================================================
-- This migration inserts new model data into the api.model table.
--
-- Includes:
-- - GPT-5.1: OpenAI model via OpenRouter
-- ============================================================================

-- ============================================================================
-- Insert Model Record
-- ============================================================================
INSERT INTO api.model (id, slug, name, license, active, alias, external_link, organization, description, api_model_name, api_base, created_at, updated_at) VALUES
('f5a6b7c8-d9e0-4f12-a3b4-c5d6e7f8a9b0', 'gpt-5.1', 'OpenAI: GPT-5.1', 'commercial', true, NULL, 'https://openrouter.ai/openai/gpt-5.1', 'OpenAI', NULL, 'openai/gpt-5.1', 'https://openrouter.ai/api/v1', '2025-11-14 00:00:00+00', '2025-11-14 00:00:00+00');
