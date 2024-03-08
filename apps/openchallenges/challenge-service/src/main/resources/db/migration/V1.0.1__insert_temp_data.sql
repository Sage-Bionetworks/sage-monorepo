-- challenge_platform data
LOAD DATA LOCAL INFILE '${db_platforms_csv_path}' INTO TABLE challenge_platform
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- edam_concept data
LOAD DATA LOCAL INFILE '${db_edam_concept_csv_path}' INTO TABLE edam_concept
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- challenge data
LOAD DATA LOCAL INFILE '${db_challenges_csv_path}' INTO TABLE challenge
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- challenge_organization_role data
LOAD DATA LOCAL INFILE '${db_contribution_roles_csv_path}' INTO TABLE challenge_contribution
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- challenge_incentive data
LOAD DATA LOCAL INFILE '${db_incentives_csv_path}' INTO TABLE challenge_incentive
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- challenge_submission_type data
LOAD DATA LOCAL INFILE '${db_submission_types_csv_path}' INTO TABLE challenge_submission_type
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


  -- challenge_input_data_type data
LOAD DATA LOCAL INFILE '${db_challenge_input_data_type_csv_path}' INTO TABLE challenge_input_data_type
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- challenge_star data
INSERT INTO challenge_star (id, challenge_id, user_id)
VALUES (1, 1, 1),
  (2, 2, 1),
  (3, 1, 2);


-- challenge_category data
LOAD DATA LOCAL INFILE '${db_categories_csv_path}' INTO TABLE challenge_category
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;
