-- organization definition

CREATE SEQUENCE organization_id_seq;

CREATE TABLE organization (
  id                    BIGINT NOT NULL DEFAULT nextval('organization_id_seq'),
  name                  VARCHAR(255) NOT NULL,
  login                 VARCHAR(64) NOT NULL UNIQUE,
  avatar_key            VARCHAR(255) DEFAULT NULL,
  website_url           VARCHAR(500) DEFAULT NULL,
  description           VARCHAR(1000) DEFAULT NULL,
  challenge_count       INTEGER DEFAULT 0,
  created_at            TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at            TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  acronym               VARCHAR(10) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT login_check CHECK (char_length(login) >= 2 AND login ~ '^[a-z0-9]+(?:-[a-z0-9]+)*$')
);

ALTER SEQUENCE organization_id_seq OWNED BY organization.id;


-- organization_category definition

CREATE SEQUENCE organization_category_id_seq;

CREATE TABLE organization_category (
    id                    INTEGER NOT NULL DEFAULT nextval('organization_category_id_seq'),
    organization_id       BIGINT NOT NULL,
    category              VARCHAR(20) CHECK (category IN ('featured')),
    PRIMARY KEY (id),
    FOREIGN KEY (organization_id) REFERENCES organization(id)
);

ALTER SEQUENCE organization_category_id_seq OWNED BY organization_category.id;


-- challenge_contribution definition

CREATE SEQUENCE challenge_contribution_id_seq;

CREATE TABLE challenge_contribution (
    id                    INTEGER NOT NULL DEFAULT nextval('challenge_contribution_id_seq'),
    challenge_id          BIGINT NOT NULL,
    organization_id       BIGINT NOT NULL,
    role                  VARCHAR(50) CHECK (role IN ('challenge_organizer', 'data_contributor', 'sponsor')),
    PRIMARY KEY (id),
    FOREIGN KEY (organization_id) REFERENCES organization(id),
    CONSTRAINT unique_item UNIQUE (challenge_id, organization_id, role)
);

ALTER SEQUENCE challenge_contribution_id_seq OWNED BY challenge_contribution.id;