-- Create databases
CREATE DATABASE challenge_service;
CREATE DATABASE organization_service;
CREATE DATABASE user_service;
CREATE DATABASE auth_service;
CREATE DATABASE edam;

-- Create admin role with necessary privileges
CREATE ROLE role_admin;

-- Connect to each database and grant privileges to role_admin
\c challenge_service;
GRANT ALL PRIVILEGES ON DATABASE challenge_service TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO role_admin;
-- Grant privileges on future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO role_admin;

\c organization_service;
GRANT ALL PRIVILEGES ON DATABASE organization_service TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO role_admin;

\c user_service;
GRANT ALL PRIVILEGES ON DATABASE user_service TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO role_admin;

\c auth_service;
GRANT ALL PRIVILEGES ON DATABASE auth_service TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO role_admin;

\c edam;
GRANT ALL PRIVILEGES ON DATABASE edam TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO role_admin;

-- Switch back to default database
\c postgres;

-- Grant role_admin to postgres user (assuming postgres user exists or will be created)
-- Note: In PostgreSQL, you might need to create the postgres user first if it doesn't exist
-- CREATE ROLE postgres LOGIN PASSWORD 'password';
GRANT role_admin TO postgres;

-- Create user for openchallenges-challenge-service
CREATE ROLE challenge_service LOGIN PASSWORD 'changeme';
GRANT CONNECT ON DATABASE challenge_service TO challenge_service;
GRANT CONNECT ON DATABASE edam TO challenge_service;

-- Grant privileges on challenge_service database
\c challenge_service;
GRANT ALL PRIVILEGES ON SCHEMA public TO challenge_service;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO challenge_service;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO challenge_service;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO challenge_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO challenge_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO challenge_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO challenge_service;

-- Grant read privileges on edam database
\c edam;
GRANT USAGE ON SCHEMA public TO challenge_service;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO challenge_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO challenge_service;

-- Switch back to default database
\c postgres;

-- Create user for openchallenges-organization-service
CREATE ROLE organization_service LOGIN PASSWORD 'changeme';
GRANT CONNECT ON DATABASE organization_service TO organization_service;

\c organization_service;
GRANT ALL PRIVILEGES ON SCHEMA public TO organization_service;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO organization_service;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO organization_service;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO organization_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO organization_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO organization_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO organization_service;

-- Switch back to default database
\c postgres;

-- Create user for openchallenges-auth-service
CREATE ROLE auth_service LOGIN PASSWORD 'changeme';
GRANT CONNECT ON DATABASE auth_service TO auth_service;

\c auth_service;
GRANT ALL PRIVILEGES ON SCHEMA public TO auth_service;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO auth_service;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO auth_service;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO auth_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO auth_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO auth_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO auth_service;

-- Switch back to default database
\c postgres;

-- Create user for openchallenges-user-service (commented out in original)
-- \c postgres;
-- CREATE ROLE user_service LOGIN PASSWORD 'changeme';
-- GRANT role_admin TO user_service;
-- GRANT CONNECT ON DATABASE user_service TO user_service;

-- Create user for openchallenges-mysqld-exporter equivalent (pg_stat_monitor or similar)
-- \c postgres;
-- CREATE ROLE postgres_exporter LOGIN PASSWORD 'changeme' CONNECTION LIMIT 3;
-- GRANT pg_monitor TO postgres_exporter;
-- GRANT CONNECT ON DATABASE postgres TO postgres_exporter;