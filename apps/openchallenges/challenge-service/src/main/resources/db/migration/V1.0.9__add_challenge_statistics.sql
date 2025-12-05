-- Add statistics columns to challenge table
ALTER TABLE challenge ADD COLUMN total_competitors INT;
ALTER TABLE challenge ADD COLUMN total_teams INT;
ALTER TABLE challenge ADD COLUMN total_submissions INT;
