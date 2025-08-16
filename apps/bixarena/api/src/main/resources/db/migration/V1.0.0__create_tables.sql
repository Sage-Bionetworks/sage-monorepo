-- Sessions
CREATE TABLE conversation (
  id UUID PRIMARY KEY,
  user_id INT NOT NULL REFERENCES "user"(id),
  title TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);