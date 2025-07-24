-- Create databases
CREATE DATABASE dataset_service;

-- Create admin role with necessary privileges
CREATE ROLE role_admin;

-- Connect to dataset_service database and grant privileges to role_admin
\c dataset_service;
GRANT ALL PRIVILEGES ON DATABASE dataset_service TO role_admin;
GRANT ALL PRIVILEGES ON SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO role_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO role_admin;
-- Grant privileges on future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO role_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO role_admin;

-- Switch back to default database
\c postgres;

-- Grant role_admin to postgres user (assuming postgres user exists)
GRANT role_admin TO postgres;

-- Create user for amp-als-dataset-service
CREATE ROLE dataset_service LOGIN PASSWORD 'changeme';
GRANT CONNECT ON DATABASE dataset_service TO dataset_service;

-- Grant privileges on dataset_service database
\c dataset_service;
GRANT ALL PRIVILEGES ON SCHEMA public TO dataset_service;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO dataset_service;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO dataset_service;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO dataset_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO dataset_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO dataset_service;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO dataset_service;
