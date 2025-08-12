-- create organization table

CREATE TABLE organization (
  id                    BIGSERIAL PRIMARY KEY,
  name                  VARCHAR(255) NOT NULL,
  login                 VARCHAR(100) NOT NULL UNIQUE,
  avatar_key            VARCHAR(255),
  website_url           VARCHAR(500),
  description           VARCHAR(1000),
  created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at            TIMESTAMP WITH TIME ZONE NOT NULL,
  acronym               VARCHAR(20),
  CONSTRAINT login_check CHECK (
    char_length(login) >= 2 AND login ~ '^[a-z0-9]+(?:-[a-z0-9]+)*$'
  )
);


-- create organization_category table

CREATE TABLE organization_category (
    id                    BIGSERIAL PRIMARY KEY,
    organization_id       BIGINT NOT NULL,
    category              VARCHAR(20) NOT NULL CHECK (category IN ('featured')),
    CONSTRAINT fk_organization
      FOREIGN KEY (organization_id)
      REFERENCES organization(id)
);


-- create challenge_participation table

CREATE TABLE challenge_participation (
    id BIGSERIAL          PRIMARY KEY,
    challenge_id          BIGINT NOT NULL,
    organization_id       BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (
        role IN ('challenge_organizer', 'data_contributor', 'sponsor')
    ),
    CONSTRAINT fk_organization
      FOREIGN KEY (organization_id)
      REFERENCES organization(id),
    CONSTRAINT uq_participation UNIQUE (challenge_id, organization_id, role)
);
