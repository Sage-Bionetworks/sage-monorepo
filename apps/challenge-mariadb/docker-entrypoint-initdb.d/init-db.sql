-- challenge-core-service
CREATE USER challenge_core_service IDENTIFIED BY 'changeme';
CREATE DATABASE challenge_core_service;
GRANT ALL PRIVILEGES ON challenge_core_service TO challenge_core_service;