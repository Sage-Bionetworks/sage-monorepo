-- organization data
LOAD DATA LOCAL INFILE '${db_organizations_csv_path}' INTO TABLE organization
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;


-- organization_category data
INSERT INTO organization_category (id, organization_id, category)
VALUES (1, 1, 'featured'),
  (2, 12, 'featured'),
  (3, 13, 'featured');


-- contributor_roles data
LOAD DATA LOCAL INFILE '${db_contribution_roles_csv_path}' INTO TABLE challenge_contribution
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;
