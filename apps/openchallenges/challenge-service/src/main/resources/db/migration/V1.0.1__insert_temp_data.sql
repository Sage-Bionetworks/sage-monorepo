-- challenge_platform data
LOAD DATA LOCAL INFILE '/workspace/BOOT-INF/classes/db/platforms.csv' INTO TABLE challenge_platform
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;

-- challenge data
LOAD DATA LOCAL INFILE '/workspace/BOOT-INF/classes/db/challenges.csv' INTO TABLE challenge
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- challenge_organization_role data
LOAD DATA LOCAL INFILE '/workspace/BOOT-INF/classes/db/contribution_roles.csv' INTO TABLE challenge_contribution
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- challenge_incentive data
LOAD DATA LOCAL INFILE '/workspace/BOOT-INF/classes/db/incentives.csv' INTO TABLE challenge_incentive
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- challenge_submission_type data
LOAD DATA LOCAL INFILE '/workspace/BOOT-INF/classes/db/submission_types.csv' INTO TABLE challenge_submission_type
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- challenge_star data
INSERT INTO challenge_star (id, challenge_id, user_id)
VALUES (1, 1, 1),
  (2, 2, 1),
  (3, 1, 2);


-- challenge_input_data_type
INSERT INTO challenge_input_data_type (id, slug, name)
VALUES (1, 'genomic', 'genomic'),
  (2, 'proteomic', 'proteomic'),
  (3, 'gene-expression', 'gene expression'),
  (4, 'metabolomic', 'metabolomic');


-- challenge_x_challenge_input_data_type definition
INSERT INTO challenge_x_challenge_input_data_type (id, challenge_id, challenge_input_data_type_id)
VALUES ('1', 1, 1),
  ('2', 2, 1),
  ('3', 1, 2),
  ('4', 4, 4);


-- challenge_category data
INSERT INTO challenge_category (id, challenge_id, category)
VALUES (1, 161, 'featured'),
  (2, 156, 'featured'),
  (3, 58, 'featured');
