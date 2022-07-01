create database challenge;
create role challenge_role_admin;
grant all on challenge.* to challenge_role_admin;

-- challenge-core-service
create user challenge_core_service identified by 'changeme';
grant challenge_role_admin to challenge_core_service;
set default role challenge_role_admin for challenge_core_service;

-- challenge-user-service
create user challenge_user_service identified by 'changeme';
grant challenge_role_admin to challenge_user_service;
set default role challenge_role_admin for challenge_user_service;