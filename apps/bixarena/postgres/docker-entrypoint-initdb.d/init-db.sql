-- Create databases
CREATE DATABASE bixarena;

-- Create admin role with necessary privileges
CREATE ROLE role_admin;

-- Connect to each database and grant privileges to role_admin
\c bixarena;
GRANT ALL PRIVILEGES ON DATABASE bixarena TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO role_admin;
-- Grant privileges on future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO role_admin;

-- Switch back to default database
\c postgres;

-- Grant role_admin to postgres user (assuming postgres user exists or will be created)
-- Note: In PostgreSQL, you might need to create the postgres user first if it doesn't exist
-- CREATE ROLE postgres LOGIN PASSWORD 'password';
GRANT role_admin TO postgres;

-- Create user for bixarena
CREATE ROLE bixarena LOGIN PASSWORD 'changeme';
GRANT CONNECT ON DATABASE bixarena TO bixarena;

-- Grant privileges on bixarena database
\c bixarena;
GRANT ALL PRIVILEGES ON SCHEMA public TO bixarena;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO bixarena;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO bixarena;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO bixarena;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO bixarena;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO bixarena;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO bixarena;

-- Drop table if exists
DROP TABLE IF EXISTS conversation CASCADE;

-- Conversation table - stores chat messages
CREATE TABLE conversation (
    id SERIAL PRIMARY KEY,
    role VARCHAR(20) NOT NULL CHECK (role IN ('user', 'assistant')),
    content TEXT NOT NULL,
    num_tokens INTEGER,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conversation_timestamp ON conversation(timestamp);
CREATE INDEX idx_conversation_role ON conversation(role);

-- Sample data for testing
INSERT INTO conversation (role, content, num_tokens) VALUES
    ('user', 'Hello', 1),
    ('assistant', 'Hello! How can I help you today?', 8),
    ('user', 'Hi there', 2),
    ('assistant', 'Hi! Welcome to BixArena. What would you like to know?', 11);