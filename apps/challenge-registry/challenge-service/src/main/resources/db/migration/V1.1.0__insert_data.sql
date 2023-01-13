-- challenge_platform data

INSERT INTO challenge_platform (id, slug, name, avatar_url, website_url)
VALUES (
    '1',
    'synapse',
    'Synapse',
    'https://via.placeholder.com/300.png',
    'https://synapse.org'
  ),
  (
    '2',
    'codalab',
    'CodaLab',
    'https://via.placeholder.com/300.png',
    'https://codalab.org'
  ),
  (
    '3',
    'kaggle',
    'Kaggle',
    'https://via.placeholder.com/300.png',
    'https://www.kaggle.com'
  );

-- challenge_input_data_type

INSERT INTO challenge_input_data_type (id, slug, name)
VALUES ('1', 'genomic', 'genomic'),
  ('2', 'proteomic', 'proteomic'),
  ('3', 'gene-expression', 'gene expression');

-- challenge data

INSERT INTO challenge (
    id,
    name,
    headline,
    description,
    status,
    difficulty,
    platform_id,
    start_date,
    end_date
  )
VALUES (
    '1',
    'The Digital Mammography DREAM Challenge',
    'Example headline',
    'Example description',
    'upcoming',
    'good_for_beginners',
    '1',
    '2017-01-01',
    '2017-02-01'
  ),
  (
    '2',
    'Patient Mortality EHR DREAM Challenge',
    'Example headline',
    'Example description',
    'active',
    'intermediate',
    '1',
    '2018-01-01',
    '2018-02-01'
  ),
  (
    '3',
    'COVID-19 EHR DREAM Challenge',
    'Example headline',
    'Example description',
    'completed',
    'advanced',
    '2',
    '2019-01-01',
    '2019-02-01'
  );

-- challenge_incentive data

INSERT INTO challenge_incentive (id, name, challenge_id)
VALUES ('1', 'monetary', 1),
  ('2', 'publication', 1),
  ('3', 'monetary', 2);

-- challenge_submission_type data

INSERT INTO challenge_submission_type (id, name, challenge_id)
VALUES ('1', 'container_image', 1),
  ('2', 'prediction_file', 2),
  ('3', 'container_image', 2);

-- challenge_star data

INSERT INTO challenge_star (id, challenge_id, user_id)
VALUES ('1', 1, 1),
  ('2', 2, 1),
  ('3', 1, 2);

-- challenge_x_challenge_input_data_type definition

INSERT INTO challenge_x_challenge_input_data_type (id, challenge_id, challenge_input_data_type_id)
VALUES ('1', 1, 1),
  ('2', 2, 1),
  ('3', 1, 2);