SET GLOBAL local_infile = 'ON';

create database challenge_service;
create database organization_service;
create database user_service;
create database edam;

create role role_admin;
grant all on challenge_service.* to role_admin;
grant all on organization_service.* to role_admin;
grant all on user_service.* to role_admin;
grant all on edam.* to role_admin;

-- Create the user maria
grant role_admin to maria;
set default role role_admin for maria;

-- Create the user for challenge-core-service
-- create user challenge_core_service identified by 'changeme';
-- grant role_admin to challenge_core_service;
-- set default role role_admin for challenge_core_service;

-- Create the user for openchallenges-challenge-service
create user challenge_service identified by 'changeme';
grant role_admin to challenge_service;
set default role role_admin for challenge_service;

-- Create the user for openchallenges-organization-service
create user organization_service identified by 'changeme';
grant role_admin to organization_service;
set default role role_admin for organization_service;

-- Create the user for openchallenges-user-service
create user user_service identified by 'changeme';
grant role_admin to user_service;
set default role role_admin for user_service;

-- Create the user for openchallenges-mysqld-exporter
create user 'mysqld-exporter' identified by 'changeme' with max_user_connections 3;;
grant process, slave monitor on *.* to 'mysqld-exporter';
grant select on performance_schema.* to 'mysqld-exporter';

-- Create the user for edam-etl
create user 'edam-etl' identified by 'changeme';
grant role_admin to 'edam-etl';
set default role role_admin for 'edam-etl';

-- Create the read-only user to the EDAM DB for the challenge service
create user 'edam-challenge-service' identified by 'changeme';
grant select on edam.* to 'edam-challenge-service';