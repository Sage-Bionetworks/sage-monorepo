-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Challenge Data for Testing
-- ============================================================================
-- This migration inserts mock challenge data for local development and
-- testing. It will ONLY run in the dev environment.
--
-- In stage and prod environments, data managers will manually add challenge
-- data using psql or Python scripts.
--
-- Migration naming: V1000+ ensures this runs AFTER all schema migrations
-- Location: db/migration/dev/ (dev profile only)
-- ============================================================================

-- ============================================================================
-- Insert Mock Challenges
-- ============================================================================
-- Sample includes 15 diverse challenges covering different:
-- - Disease areas (cancer, Alzheimer's, ALS, respiratory, etc.)
-- - Challenge types (prediction, inference, detection, etc.)
-- - Platforms (Synapse, CAMI, Grand Challenge, Kaggle, etc.)
-- - Statuses (completed, active, upcoming)
-- ============================================================================

INSERT INTO challenge (id, slug, name, headline, description, avatar_url, website_url, status, platform_id, doi, start_date, end_date, operation_id, created_at, updated_at) VALUES
(1, 'network-topology-and-parameter-inference', 'Network Topology and Parameter Inference', 'Optimize methods to estimate biology model parameters', 'Participants are asked to develop and/or apply optimization methods, including the selection of the most informative experiments, to accurately estimate parameters and predict outcomes of perturbations in Systems Biology models.', NULL, 'https://www.synapse.org/#!Synapse:syn2821735', 'completed', 1, NULL, '2012-06-01', '2012-10-01', NULL, '2023-11-15 22:40:15', '2025-05-23 23:03:15'),
(2, 'breast-cancer-prognosis', 'Breast Cancer Prognosis', 'Predict breast cancer survival from clinical and genomic data', 'The goal of the breast cancer prognosis Challenge is to assess the accuracy of computational models designed to predict breast cancer survival.', NULL, 'https://www.synapse.org/#!Synapse:syn2813426', 'completed', 1, NULL, '2012-07-12', '2012-10-15', NULL, '2023-11-14 20:36:32', '2024-02-19 18:17:47'),
(9, 'icgc-tcga-dream-mutation-calling', 'ICGC-TCGA DREAM Mutation Calling', 'Crowdsourcing challenge to improve cancer mutation detection', 'International effort to improve standard methods for identifying cancer-associated mutations and rearrangements in whole-genome sequencing (WGS) data.', NULL, 'https://www.synapse.org/#!Synapse:syn312572', 'completed', 1, NULL, '2013-12-14', '2016-04-22', NULL, '2023-06-23 00:00:00', '2025-04-03 19:15:58'),
(12, 'alzheimers-disease-big-data', 'Alzheimer''s Disease Big Data', 'Seeking accurate predictive biomarkers', 'Apply an open science approach to rapidly identify accurate predictive AD biomarkers for improved diagnosis and treatment.', NULL, 'https://www.synapse.org/#!Synapse:syn2290704', 'completed', 1, NULL, '2014-06-02', '2014-10-17', NULL, '2023-06-23 00:00:00', '2023-10-14 05:38:17'),
(15, 'als-stratification-prize4life', 'ALS Stratification Prize4Life', 'Predicting ALS progression and survival with data', 'Predict disease progression and survival using clinical data from ALS trials and registries.', NULL, 'https://www.synapse.org/#!Synapse:syn2873386', 'completed', 1, NULL, '2015-06-22', '2015-10-04', NULL, '2023-06-23 00:00:00', '2023-10-14 05:38:19'),
(20, 'disease-module-identification', 'Disease Module Identification', 'Find disease modules in genomic networks', 'Systematically assess module identification methods on genomic networks to discover novel modules and pathways underlying complex diseases.', NULL, 'https://www.synapse.org/#!Synapse:syn6156761', 'completed', 1, 'https://doi.org/10.1038/s41592-019-0509-5', '2016-06-24', '2016-10-01', NULL, '2023-11-01 22:21:32', '2023-10-16 21:17:48'),
(24, 'digital-mammography-dream-challenge', 'Digital Mammography DREAM Challenge', 'Improve mammography prediction to detect breast cancer early', 'Improve the predictive accuracy of digital mammography for the early detection of breast cancer.', NULL, 'https://www.synapse.org/#!Synapse:syn4224222', 'completed', 1, 'https://doi.org/10.1001/jamanetworkopen.2020.0265', '2016-11-18', '2017-05-16', NULL, '2023-06-23 00:00:00', '2023-10-14 05:38:29'),
(50, 'cagi-6', 'CAGI 6', 'Predict phenotypic impacts of genomic variants', 'The 6th edition of the Critical Assessment of Genome Interpretation, assessing computational methods for predicting phenotypic impacts.', NULL, 'https://genomeinterpretation.org/cagi6-overview', 'completed', NULL, NULL, '2021-07-01', '2023-03-01', NULL, '2023-06-23 00:00:00', '2023-10-14 05:38:57'),
(100, 'covid-19-image-segmentation', 'COVID-19 Image Segmentation', 'Segment COVID-19 infections in CT scans', 'Develop algorithms to automatically segment COVID-19 infections in chest CT scans.', NULL, 'https://grand-challenge.org/challenges/', 'completed', 5, NULL, '2020-04-01', '2020-08-31', NULL, '2023-11-01 22:25:32', '2023-10-14 05:39:35'),
(150, 'protein-ligand-classification', 'Protein-Ligand Classification', 'Classify protein-ligand binding interactions', 'Predict whether small molecules will bind to specific protein targets.', NULL, 'https://www.kaggle.com/competitions', 'completed', 8, NULL, '2022-03-15', '2022-06-30', NULL, '2023-11-01 22:27:15', '2023-10-14 05:40:12'),
(200, 'cell-painting-prediction', 'Cell Painting Prediction', 'Predict cellular morphology from genetic perturbations', 'Use machine learning to predict cell morphology changes from genetic perturbations.', NULL, 'https://www.synapse.org/#!Synapse:syn123456', 'active', 1, NULL, '2024-01-15', '2024-12-31', NULL, '2024-01-10 15:30:00', '2024-01-10 15:30:00'),
(250, 'drug-response-prediction-2024', 'Drug Response Prediction 2024', 'Predict cancer drug responses from multi-omics data', 'Predict patient responses to cancer therapeutics using genomic, transcriptomic, and proteomic data.', NULL, 'https://www.synapse.org/#!Synapse:syn789012', 'active', 1, NULL, '2024-06-01', '2025-03-31', 2813, '2024-05-15 10:00:00', '2024-05-15 10:00:00'),
(300, 'brain-age-prediction-challenge', 'Brain Age Prediction Challenge', 'Estimate biological brain age from MRI scans', 'Develop models to predict biological brain age from structural MRI data.', NULL, 'https://grand-challenge.org/challenges/brain-age/', 'upcoming', 5, NULL, '2025-09-01', '2026-02-28', NULL, '2024-08-01 12:00:00', '2024-08-01 12:00:00'),
(350, 'microbiome-disease-association', 'Microbiome Disease Association', 'Link gut microbiome to disease states', 'Identify microbiome signatures associated with various disease conditions.', NULL, 'https://codalab.lisn.upsaclay.fr/competitions/', 'upcoming', 9, NULL, '2025-10-15', '2026-04-15', NULL, '2024-09-01 14:30:00', '2024-09-01 14:30:00'),
(400, 'synthetic-biology-design', 'Synthetic Biology Design Challenge', 'Design optimal genetic circuits', 'Create novel genetic circuits for synthetic biology applications.', NULL, 'https://eternagame.org/challenges/', 'upcoming', 13, NULL, '2026-01-15', '2026-06-30', NULL, '2024-10-15 16:00:00', '2024-10-15 16:00:00');

-- Update the sequence to continue from the highest ID
SELECT setval('challenge_id_seq', (SELECT MAX(id) FROM challenge));
