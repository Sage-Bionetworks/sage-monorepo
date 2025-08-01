-- Rollback script for V1.0.9__refactor_incentives_to_array.sql
-- This script reverts the incentives array back to a separate table structure

-- Step 1: Recreate the challenge_incentive table
CREATE TABLE challenge_incentive (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL CHECK (
    name IN ('monetary', 'publication', 'speaking_engagement', 'other')
  ),
  challenge_id BIGINT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_challenge_inc FOREIGN KEY (challenge_id) REFERENCES challenge(id),
  CONSTRAINT unique_incentive UNIQUE (name, challenge_id)
);

-- Step 2: Migrate data from array column back to separate table
INSERT INTO challenge_incentive (name, challenge_id, created_at)
SELECT 
  unnest(incentives) as name,
  id as challenge_id,
  created_at
FROM challenge 
WHERE incentives IS NOT NULL AND array_length(incentives, 1) > 0;

-- Step 3: Remove the incentives array column from challenge table
ALTER TABLE challenge DROP CONSTRAINT IF EXISTS challenge_incentives_check;
ALTER TABLE challenge DROP COLUMN incentives;
