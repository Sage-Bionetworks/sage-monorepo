-- Create the openchallenges database (if not already created by POSTGRES_DB env var)
SELECT 'CREATE DATABASE openchallenges'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'openchallenges')\gexec

-- Connect to openchallenges database to set up schemas
\c openchallenges;

-- Create schemas for service separation (instead of separate databases)
CREATE SCHEMA IF NOT EXISTS challenge;
CREATE SCHEMA IF NOT EXISTS organization;
CREATE SCHEMA IF NOT EXISTS auth;

-- Create admin role with necessary privileges
CREATE ROLE role_admin;

-- Grant all privileges on the openchallenges database to role_admin
GRANT ALL PRIVILEGES ON DATABASE openchallenges TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA challenge TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA organization TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA auth TO role_admin;

-- Grant privileges on existing tables in all schemas
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO role_admin;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA challenge TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA challenge TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA challenge TO role_admin;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA organization TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA organization TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA organization TO role_admin;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA auth TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA auth TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA auth TO role_admin;

-- Grant privileges on future objects in all schemas
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO role_admin;

ALTER DEFAULT PRIVILEGES IN SCHEMA challenge GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA challenge GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA challenge GRANT ALL ON FUNCTIONS TO role_admin;

ALTER DEFAULT PRIVILEGES IN SCHEMA organization GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA organization GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA organization GRANT ALL ON FUNCTIONS TO role_admin;

ALTER DEFAULT PRIVILEGES IN SCHEMA auth GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA auth GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA auth GRANT ALL ON FUNCTIONS TO role_admin;

-- Switch back to default database
\c postgres;

-- Grant role_admin to postgres user
GRANT role_admin TO postgres;

-- Create user for openchallenges-challenge-service
CREATE ROLE challenge_service LOGIN PASSWORD 'changeme';
GRANT CONNECT ON DATABASE openchallenges TO challenge_service;

-- Grant privileges on challenge schema in openchallenges database
\c openchallenges;
GRANT USAGE, CREATE ON SCHEMA challenge TO challenge_service;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA challenge TO challenge_service;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA challenge TO challenge_service;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA challenge TO challenge_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA challenge GRANT ALL ON TABLES TO challenge_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA challenge GRANT ALL ON SEQUENCES TO challenge_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA challenge GRANT ALL ON FUNCTIONS TO challenge_service;
ALTER DEFAULT PRIVILEGES FOR ROLE challenge_service IN SCHEMA challenge GRANT ALL ON TABLES TO challenge_service;
ALTER DEFAULT PRIVILEGES FOR ROLE challenge_service IN SCHEMA challenge GRANT ALL ON SEQUENCES TO challenge_service;
ALTER DEFAULT PRIVILEGES FOR ROLE challenge_service IN SCHEMA challenge GRANT ALL ON FUNCTIONS TO challenge_service;

-- Create user for openchallenges-organization-service
\c postgres;
CREATE ROLE organization_service LOGIN PASSWORD 'changeme';
GRANT CONNECT ON DATABASE openchallenges TO organization_service;

\c openchallenges;
GRANT USAGE, CREATE ON SCHEMA organization TO organization_service;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA organization TO organization_service;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA organization TO organization_service;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA organization TO organization_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA organization GRANT ALL ON TABLES TO organization_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA organization GRANT ALL ON SEQUENCES TO organization_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA organization GRANT ALL ON FUNCTIONS TO organization_service;
ALTER DEFAULT PRIVILEGES FOR ROLE organization_service IN SCHEMA organization GRANT ALL ON TABLES TO organization_service;
ALTER DEFAULT PRIVILEGES FOR ROLE organization_service IN SCHEMA organization GRANT ALL ON SEQUENCES TO organization_service;
ALTER DEFAULT PRIVILEGES FOR ROLE organization_service IN SCHEMA organization GRANT ALL ON FUNCTIONS TO organization_service;

-- Create user for openchallenges-auth-service
\c postgres;
CREATE ROLE auth_service LOGIN PASSWORD 'changeme';
GRANT CONNECT ON DATABASE openchallenges TO auth_service;

\c openchallenges;
GRANT USAGE, CREATE ON SCHEMA auth TO auth_service;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA auth TO auth_service;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA auth TO auth_service;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA auth TO auth_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA auth GRANT ALL ON TABLES TO auth_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA auth GRANT ALL ON SEQUENCES TO auth_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA auth GRANT ALL ON FUNCTIONS TO auth_service;
ALTER DEFAULT PRIVILEGES FOR ROLE auth_service IN SCHEMA auth GRANT ALL ON TABLES TO auth_service;
ALTER DEFAULT PRIVILEGES FOR ROLE auth_service IN SCHEMA auth GRANT ALL ON SEQUENCES TO auth_service;
ALTER DEFAULT PRIVILEGES FOR ROLE auth_service IN SCHEMA auth GRANT ALL ON FUNCTIONS TO auth_service;

-- Switch back to default database
\c postgres;

-- Create user for openchallenges-user-service (commented out - not currently used)
-- CREATE ROLE user_service LOGIN PASSWORD 'changeme';
-- GRANT role_admin TO user_service;
-- GRANT CONNECT ON DATABASE openchallenges TO user_service;

-- Create user for postgres_exporter (commented out - for monitoring)
-- CREATE ROLE postgres_exporter LOGIN PASSWORD 'changeme' CONNECTION LIMIT 3;
-- GRANT pg_monitor TO postgres_exporter;
-- GRANT CONNECT ON DATABASE postgres TO postgres_exporter;
