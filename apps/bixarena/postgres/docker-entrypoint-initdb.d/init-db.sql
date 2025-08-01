-- Connect to the bixarena database to set up privileges
\c bixarena;

-- Grant all privileges on the bixarena database to postgres user
GRANT ALL PRIVILEGES ON DATABASE bixarena TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO postgres;

-- Grant privileges on future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO postgres;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO postgres;

-- Create Conversation table
CREATE TABLE conversation (
    role VARCHAR(20) NOT NULL CHECK (role IN ('user', 'assistant')),
    content TEXT NOT NULL,
    num_tokens INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_conversation_role ON conversation(role);
CREATE INDEX idx_conversation_created_at ON conversation(created_at);

-- Insert sample conversation data
INSERT INTO conversation (role, content, num_tokens) VALUES
    ('user', 'Hello', 2),
    ('assistant', 'Hello! How can I help you today?', 8);
