-- organization definition

CREATE TABLE organization (
  id                    BIGSERIAL PRIMARY KEY,
  name                  VARCHAR(255) NOT NULL,
  login                 VARCHAR(100) NOT NULL UNIQUE,
  avatar_key            VARCHAR(255),
  website_url           VARCHAR(500),
  description           VARCHAR(1000),
  challenge_count       INTEGER DEFAULT 0,
  created_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  acronym               VARCHAR(20),
  CONSTRAINT login_check CHECK (char_length(login) >= 2 AND login ~ '^[a-z0-9]+(?:-[a-z0-9]+)*$')
);


-- organization_category definition

CREATE TABLE organization_category (
    id                    BIGSERIAL PRIMARY KEY,
    organization_id       BIGINT NOT NULL,
    category              VARCHAR(20) CHECK (category IN ('featured')),
    FOREIGN KEY (organization_id) REFERENCES organization(id)
);


-- challenge_contribution definition

CREATE TABLE challenge_contribution (
    id                    BIGSERIAL PRIMARY KEY,
    challenge_id          BIGINT NOT NULL,
    organization_id       BIGINT NOT NULL,
    role                  VARCHAR(50) CHECK (role IN ('challenge_organizer', 'data_contributor', 'sponsor')),
    FOREIGN KEY (organization_id) REFERENCES organization(id),
    CONSTRAINT unique_item UNIQUE (challenge_id, organization_id, role)
);
