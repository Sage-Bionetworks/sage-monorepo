-- Grant admin role to tschaffter for local development
UPDATE auth.user SET role = 'admin' WHERE username = 'tschaffter';
