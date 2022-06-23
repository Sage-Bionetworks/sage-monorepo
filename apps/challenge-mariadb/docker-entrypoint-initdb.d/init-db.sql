-- challenge-core-service
create user challenge_core_service identified by 'changeme';
create role challenge_core_service_role;
create database challenge_core_service;

grant challenge_core_service_role to challenge_core_service;
grant challenge_core_service_role to maria;
grant all on challenge_core_service.* to challenge_core_service_role;
