-- Expand message.content from VARCHAR(10000) to VARCHAR(50000).
--
-- The previous 10000-char cap (V5, SMR-623) still truncates or rejects long
-- LLM responses on write: chat_max_response_tokens is 4096 and a token can
-- represent 3-10 chars depending on content (non-English, code, formatted
-- output), so real responses can exceed 10000 chars. 50000 covers the
-- realistic worst case with headroom. Metadata-only change in Postgres
-- (no index or constraint on content), no table rewrite.

ALTER TABLE api.message
  ALTER COLUMN content TYPE VARCHAR(50000);
