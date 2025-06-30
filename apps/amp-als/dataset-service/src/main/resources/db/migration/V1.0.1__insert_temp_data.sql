INSERT INTO dataset (id, name, description) VALUES
  (1, 'Mock Dataset 1', 'Clinical data from patients with cardiovascular conditions'),
  (2, 'Mock Dataset 2', 'Longitudinal dataset of individuals with early signs of neurodegeneration'),
  (3, 'Mock Dataset 3', 'Registry of patients enrolled in various oncology clinical trials');

-- Update the sequence to continue from the highest ID
SELECT setval('dataset_id_seq', (SELECT MAX(id) FROM dataset));
