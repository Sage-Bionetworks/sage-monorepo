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