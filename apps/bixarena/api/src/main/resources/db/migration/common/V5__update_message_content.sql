-- ============================================================================
-- Schema Change: Update Message Content Column Length
-- ============================================================================
-- This migration expands the content column length in the message table
-- from 5000 to 10000 characters to support longer messages.
--
-- Changes:
-- - Expand content column from VARCHAR(5000) to VARCHAR(10000)
-- ============================================================================

-- ============================================================================
-- Expand Content Column Length
-- ============================================================================
ALTER TABLE api.message
  ALTER COLUMN content TYPE VARCHAR(10000);
