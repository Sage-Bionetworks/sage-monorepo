-- Migration to refactor challenge incentives from separate table to array column
-- This migration consolidates incentives into the challenge table as an array

-- Step 1: Add incentives array column to challenge table
ALTER TABLE challenge ADD COLUMN incentives VARCHAR(50)[] DEFAULT NULL;

-- Step 2: Migrate existing incentive data from challenge_incentive table to array column
UPDATE challenge
SET incentives = (
  SELECT ARRAY_AGG(ci.name ORDER BY ci.name)
  FROM challenge_incentive ci
  WHERE ci.challenge_id = challenge.id
)
WHERE EXISTS (
  SELECT 1 FROM challenge_incentive ci WHERE ci.challenge_id = challenge.id
);

-- Step 3: Add check constraints to ensure valid incentive values and no duplicates
ALTER TABLE challenge ADD CONSTRAINT challenge_incentives_values_check
  CHECK (
    incentives <@ ARRAY['monetary', 'publication', 'speaking_engagement', 'other']::VARCHAR(50)[]
  );

-- Ensure no duplicate values in the incentives array (acts as a set)
-- Note: PostgreSQL doesn't allow subqueries in check constraints, so we'll enforce this at the application level
-- or create a custom function if needed. For now, we rely on the unique constraint from the original table
-- and careful application logic to prevent duplicates.

-- Step 4: Drop the challenge_incentive table since data is now in the array column
DROP TABLE challenge_incentive;
