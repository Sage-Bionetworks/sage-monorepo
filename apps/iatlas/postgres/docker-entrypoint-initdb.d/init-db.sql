-- sqlfluff:dialect:postgres
CREATE USER iatlas
WITH
  ENCRYPTED PASSWORD 'changeme';

CREATE DATABASE iatlas;

GRANT ALL PRIVILEGES ON DATABASE iatlas TO iatlas;
