-- Add profile fields to app_user table for user profile management
ALTER TABLE app_user ADD COLUMN bio TEXT;
ALTER TABLE app_user ADD COLUMN website VARCHAR(500);

-- Add index for website field if needed for queries
CREATE INDEX idx_app_user_website ON app_user(website);
