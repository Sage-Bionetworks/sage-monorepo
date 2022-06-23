-- challenge-core-service
create user challenge_core_service identified by 'changeme';
create database challenge_core_service;
grant all on challenge_core_service.* to challenge_core_service;