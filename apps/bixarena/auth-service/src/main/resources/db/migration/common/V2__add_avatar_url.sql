-- Add avatar_url column to store provider profile image URL
-- Updated on each login, constructed server-side per provider
ALTER TABLE auth.user ADD COLUMN avatar_url VARCHAR(300);

COMMENT ON COLUMN auth.user.avatar_url IS 'Profile image URL from the authentication provider';
