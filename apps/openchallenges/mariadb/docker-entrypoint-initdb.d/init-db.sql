create database openchallenges_challenge_service;
create database openchallenges_organization_service;
create database openchallenges_user_service;

create role openchallenges_role_admin;
grant all on openchallenges_challenge_service.* to openchallenges_role_admin;
grant all on openchallenges_organization_service.* to openchallenges_role_admin;
grant all on openchallenges_user_service.* to openchallenges_role_admin;

-- Create the user maria
grant openchallenges_role_admin to maria;
set default role openchallenges_role_admin for maria;

-- Create the user for challenge-core-service
-- create user challenge_core_service identified by 'changeme';
-- grant openchallenges_role_admin to challenge_core_service;
-- set default role openchallenges_role_admin for challenge_core_service;

-- Create the user for openchallenges-challenge-service
create user openchallenges_challenge_service identified by 'changeme';
grant openchallenges_role_admin to openchallenges_challenge_service;
set default role openchallenges_role_admin for openchallenges_challenge_service;

-- Create the user for openchallenges-organization-service
create user openchallenges_organization_service identified by 'changeme';
grant openchallenges_role_admin to openchallenges_organization_service;
set default role openchallenges_role_admin for openchallenges_organization_service;

-- Create the user for openchallenges-user-service
create user openchallenges_user_service identified by 'changeme';
grant openchallenges_role_admin to openchallenges_user_service;
set default role openchallenges_role_admin for openchallenges_user_service;