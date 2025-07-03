-- Update default user passwords with correct BCrypt hashes
UPDATE user SET password_hash = '$2a$12$0vDbrdL7o3enKVmjlIJGVeZC8obDRxUGcf76D3DowTo3gVt1wUluu' WHERE username = 'admin';
UPDATE user SET password_hash = '$2a$12$2wGLgcl3NnRDJFNod89oqOrG1PwK1JT1QlD6b9SW8wcR30ZqQiG6K' WHERE username = 'researcher';
