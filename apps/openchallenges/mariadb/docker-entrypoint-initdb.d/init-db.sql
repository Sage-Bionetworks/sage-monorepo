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

-- Create the user for openchallenges-challenge-service
create user 'challenge_service' identified by 'changeme';
grant all privileges on challenge_service.* to 'challenge_service';
grant select on edam.* to 'challenge_service';

-- Create the user for openchallenges-organization-service
create user 'organization_service' identified by 'changeme';
grant all privileges on organization_service.* to 'organization_service';

-- Create the user for openchallenges-edam-etl
create user 'edam_etl' identified by 'changeme';
grant all privileges on edam.* to 'edam_etl';

-- Create the user for openchallenges-user-service
-- create user user_service identified by 'changeme';
-- grant role_admin to user_service;
-- set default role role_admin for user_service;

-- Create the user for openchallenges-mysqld-exporter
-- create user 'mysqld-exporter' identified by 'changeme' with max_user_connections 3;;
-- grant process, slave monitor on *.* to 'mysqld-exporter';
-- grant select on performance_schema.* to 'mysqld-exporter';


-- Allow access from other hosts
GRANT ALL PRIVILEGES ON *.* TO 'root'@'0.0.0.0' IDENTIFIED BY 'changeme' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'maria'@'0.0.0.0' IDENTIFIED BY 'changeme' WITH GRANT OPTION;