-- create challenge_platform table
CREATE TABLE challenge_platform (
  id BIGSERIAL PRIMARY KEY,
  slug VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL UNIQUE,
  avatar_key VARCHAR(255),
  website_url VARCHAR(500) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

-- create edam_concept table
CREATE TABLE edam_concept (
  id SERIAL PRIMARY KEY,
  class_id VARCHAR(60) NOT NULL UNIQUE,
  preferred_label VARCHAR(80) NOT NULL
);

-- create challenge table
CREATE TABLE challenge (
  id BIGSERIAL PRIMARY KEY,
  slug VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  headline VARCHAR(80),
  description VARCHAR(1000),
  avatar_url VARCHAR(500),
  website_url VARCHAR(500),
  status VARCHAR(20) NOT NULL CHECK (status IN ('upcoming', 'active', 'completed')),
  platform_id BIGINT,
  doi VARCHAR(120),
  start_date DATE,
  end_date DATE,
  operation_id BIGINT,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT fk_platform FOREIGN KEY (platform_id) REFERENCES challenge_platform(id),
  CONSTRAINT fk_operation FOREIGN KEY (operation_id) REFERENCES edam_concept(id),
  CONSTRAINT slug_check CHECK (
    LENGTH(slug) >= 3
    AND slug ~ '^[a-z0-9]+(?:-[a-z0-9]+)*$'
  )
);

-- create challenge_contribution table
CREATE TABLE challenge_contribution (
  id BIGSERIAL PRIMARY KEY,
  challenge_id BIGINT NOT NULL,
  organization_id BIGINT NOT NULL,
  role VARCHAR(50) NOT NULL CHECK (
    role IN ('challenge_organizer', 'data_contributor', 'sponsor')
  ),
  CONSTRAINT fk_challenge FOREIGN KEY (challenge_id) REFERENCES challenge(id),
  CONSTRAINT unique_contribution UNIQUE (challenge_id, organization_id, role)
);

-- create challenge_incentive table
CREATE TABLE challenge_incentive (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL CHECK (
    name IN ('monetary', 'publication', 'speaking_engagement', 'other')
  ),
  challenge_id BIGINT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT fk_challenge_inc FOREIGN KEY (challenge_id) REFERENCES challenge(id),
  CONSTRAINT unique_incentive UNIQUE (name, challenge_id)
);

-- create challenge_submission_type table
CREATE TABLE challenge_submission_type (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL CHECK (
    name IN ('container_image', 'prediction_file', 'notebook', 'mlcube', 'other')
  ),
  challenge_id BIGINT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT fk_challenge_sub FOREIGN KEY (challenge_id) REFERENCES challenge(id),
  CONSTRAINT unique_submission_type UNIQUE (name, challenge_id)
);

-- create challenge_input_data_type table
CREATE TABLE challenge_input_data_type (
  id BIGSERIAL PRIMARY KEY,
  challenge_id BIGINT NOT NULL,
  edam_concept_id BIGINT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT fk_challenge_input FOREIGN KEY (challenge_id) REFERENCES challenge(id),
  CONSTRAINT fk_edam_concept FOREIGN KEY (edam_concept_id) REFERENCES edam_concept(id),
  CONSTRAINT unique_input_data_type UNIQUE (challenge_id, edam_concept_id)
);

-- create challenge_star table
CREATE TABLE challenge_star (
  id BIGSERIAL PRIMARY KEY,
  challenge_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT fk_challenge_star FOREIGN KEY (challenge_id) REFERENCES challenge(id),
  CONSTRAINT unique_star UNIQUE (challenge_id, user_id)
);

-- create challenge_category table
CREATE TABLE challenge_category (
  id BIGSERIAL PRIMARY KEY,
  challenge_id BIGINT NOT NULL,
  name VARCHAR(20) NOT NULL CHECK (name IN ('featured', 'benchmark', 'hackathon')),
  CONSTRAINT fk_challenge_cat FOREIGN KEY (challenge_id) REFERENCES challenge(id)
);
