-- challenge-core-service
create user challenge_core_service identified by 'changeme';
create role challenge_core_service_role_admin;
create database challenge_core_service;

grant all on challenge_core_service.* to challenge_core_service_role_admin;
grant challenge_core_service_role_admin to challenge_core_service;
set default role challenge_core_service_role_admin for challenge_core_service;
