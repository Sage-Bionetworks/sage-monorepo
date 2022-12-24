-- challenge.challenge_platform data

INSERT INTO challenge_platform (id, name, display_name)
VALUES ('1', 'synapse', 'Synapse'),
  ('2', 'codalab', 'CodaLab'),
  ('3', 'kaggle', 'Kaggle');

-- challenge.challenge data

INSERT INTO challenge (id, name, status, difficulty, platform_id)
VALUES (
    '1',
    'The Digital Mammography DREAM Challenge',
    'upcoming',
    'good_for_beginners',
    '1'
  ),
  (
    '2',
    'Patient Mortality EHR DREAM Challenge',
    'active',
    'intermediate',
    '1'
  ),
  (
    '3',
    'COVID-19 EHR DREAM Challenge',
    'completed',
    'advanced',
    '2'
  );

-- challenge.challenge_incentive data

INSERT INTO challenge_incentive (id, name, challenge_id)
VALUES ('1', 'monetary', 1),
  ('2', 'publication', 1),
  ('3', 'monetary', 2);

-- challenge.challenge_submission_type data

INSERT INTO challenge_submission_type (id, name, challenge_id)
VALUES ('1', 'container_image', 1),
  ('2', 'prediction_file', 1),
  ('3', 'container_image', 2);