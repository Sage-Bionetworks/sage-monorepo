-- sqlfluff:dialect:postgres
-- keycloak
CREATE USER keycloak
WITH
  ENCRYPTED PASSWORD 'changeme';

CREATE DATABASE keycloak;

GRANT all PRIVILEGES ON DATABASE keycloak TO keycloak;
