-- keycloak
CREATE USER keycloak WITH ENCRYPTED PASSWORD 'changeme';
CREATE DATABASE keycloak;
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
