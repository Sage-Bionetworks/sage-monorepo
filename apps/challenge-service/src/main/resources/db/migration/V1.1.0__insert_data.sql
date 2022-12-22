-- challenge.challenge_platform data

INSERT INTO challenge_platform (id, name)
VALUES ('1', 'Synapse'),
  ('2', 'CodaLab'),
  ('3', 'Kaggle');

-- challenge.challenge data

INSERT INTO challenge (id, name, status, difficulty)
VALUES (
    '1',
    'The Digital Mammography DREAM Challenge',
    'upcoming',
    'good_for_beginners'
  ),
  (
    '2',
    'Patient Mortality EHR DREAM Challenge',
    'active',
    'intermediate'
  ),
  (
    '3',
    'COVID-19 EHR DREAM Challenge',
    'completed',
    'advanced'
  );