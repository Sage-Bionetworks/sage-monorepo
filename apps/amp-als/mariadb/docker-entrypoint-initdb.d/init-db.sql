SET
  GLOBAL local_infile = 'ON';

create database dataset_service;

create role role_admin;

grant all on dataset_service.* to role_admin;

-- Create the user maria
grant role_admin to maria;

set default role role_admin for maria;

-- Create the user for amp-als-dataset-service
create user 'dataset_service' identified by 'changeme';

grant all privileges on dataset_service.* to 'dataset_service';
