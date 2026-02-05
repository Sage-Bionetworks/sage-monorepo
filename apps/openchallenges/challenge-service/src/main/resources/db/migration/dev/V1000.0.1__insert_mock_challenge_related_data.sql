-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Challenge Related Data for Testing
-- ============================================================================
-- This migration inserts mock data for challenge-related tables:
-- - challenge_contribution
-- - challenge_incentive
-- - challenge_submission_type
-- - challenge_input_data_type
-- - challenge_category
--
-- This data complements the mock challenges from V1000.0.0
--
-- Migration naming: V1000+ ensures this runs AFTER all common migrations
-- Location: db/migration/dev/ (dev profile only)
-- ============================================================================

-- ============================================================================
-- Insert Mock Challenge Contributions
-- ============================================================================
-- Links challenges to organizations (IDs reference organization service)
-- Roles: challenge_organizer, data_contributor, sponsor
-- ============================================================================

INSERT INTO challenge_contribution (id, challenge_id, organization_id, role) VALUES
-- Challenge 1: Network Topology
(1, 1, 1, 'challenge_organizer'),

-- Challenge 2: Breast Cancer Prognosis
(2, 2, 1, 'challenge_organizer'),
(3, 2, 15, 'sponsor'),

-- Challenge 9: ICGC-TCGA Mutation Calling
(9, 9, 1, 'challenge_organizer'),
(10, 9, 150, 'data_contributor'),
(11, 9, 41, 'data_contributor'),

-- Challenge 12: Alzheimer's Disease
(12, 12, 1, 'challenge_organizer'),
(13, 12, 15, 'sponsor'),
(14, 12, 31, 'sponsor'),

-- Challenge 15: ALS Stratification
(15, 15, 1, 'challenge_organizer'),

-- Challenge 20: Disease Module Identification
(20, 20, 1, 'challenge_organizer'),
(21, 20, 15, 'sponsor'),

-- Challenge 24: Digital Mammography
(24, 24, 1, 'challenge_organizer'),
(25, 24, 134, 'data_contributor'),

-- Challenge 200: Cell Painting (Active)
(200, 200, 1, 'challenge_organizer'),
(201, 200, 41, 'data_contributor'),

-- Challenge 250: Drug Response (Active)
(250, 250, 1, 'challenge_organizer'),
(251, 250, 150, 'sponsor'),
(252, 250, 58, 'data_contributor');

-- ============================================================================
-- Insert Mock Challenge Incentives
-- ============================================================================
-- Types: monetary, publication, speaking_engagement, other
-- ============================================================================

INSERT INTO challenge_incentive (id, name, challenge_id, created_at) VALUES
(1, 'publication', 1, '2023-11-15 22:40:15'),
(2, 'publication', 2, '2023-11-14 20:36:32'),
(3, 'monetary', 9, '2023-06-23 00:00:00'),
(4, 'publication', 9, '2023-06-23 00:00:00'),
(5, 'publication', 12, '2023-06-23 00:00:00'),
(6, 'monetary', 15, '2023-06-23 00:00:00'),
(7, 'publication', 15, '2023-06-23 00:00:00'),
(8, 'publication', 20, '2023-11-01 22:21:32'),
(9, 'monetary', 24, '2023-06-23 00:00:00'),
(10, 'publication', 24, '2023-06-23 00:00:00'),
(11, 'speaking_engagement', 24, '2023-06-23 00:00:00'),
(200, 'monetary', 200, '2024-01-10 15:30:00'),
(201, 'publication', 200, '2024-01-10 15:30:00'),
(250, 'monetary', 250, '2024-05-15 10:00:00'),
(251, 'publication', 250, '2024-05-15 10:00:00'),
(252, 'speaking_engagement', 250, '2024-05-15 10:00:00');

-- ============================================================================
-- Insert Mock Challenge Submission Types
-- ============================================================================
-- Types: container_image, prediction_file, notebook, mlcube, other
-- ============================================================================

INSERT INTO challenge_submission_type (id, name, challenge_id, created_at) VALUES
(1, 'prediction_file', 1, '2023-11-15 22:40:15'),
(2, 'prediction_file', 2, '2023-11-14 20:36:32'),
(9, 'container_image', 9, '2023-06-23 00:00:00'),
(10, 'prediction_file', 9, '2023-06-23 00:00:00'),
(12, 'prediction_file', 12, '2023-06-23 00:00:00'),
(13, 'notebook', 12, '2023-06-23 00:00:00'),
(15, 'prediction_file', 15, '2023-06-23 00:00:00'),
(20, 'container_image', 20, '2023-11-01 22:21:32'),
(21, 'prediction_file', 20, '2023-11-01 22:21:32'),
(24, 'container_image', 24, '2023-06-23 00:00:00'),
(100, 'container_image', 100, '2023-11-01 22:25:32'),
(150, 'prediction_file', 150, '2023-11-01 22:27:15'),
(200, 'container_image', 200, '2024-01-10 15:30:00'),
(201, 'notebook', 200, '2024-01-10 15:30:00'),
(250, 'container_image', 250, '2024-05-15 10:00:00'),
(251, 'prediction_file', 250, '2024-05-15 10:00:00'),
(252, 'notebook', 250, '2024-05-15 10:00:00');

-- ============================================================================
-- Insert Mock Challenge Input Data Types
-- ============================================================================
-- Links challenges to EDAM concepts (data types)
-- Note: EDAM concept IDs reference the edam_concept table
-- ============================================================================

INSERT INTO challenge_input_data_type (id, challenge_id, edam_concept_id, created_at) VALUES
-- Using some common EDAM concept IDs from V1.0.2
(1, 1, 100, '2023-11-15 22:40:15'),
(2, 2, 200, '2023-11-14 20:36:32'),
(3, 2, 201, '2023-11-14 20:36:32'),
(9, 9, 300, '2023-06-23 00:00:00'),
(10, 9, 301, '2023-06-23 00:00:00'),
(12, 12, 400, '2023-06-23 00:00:00'),
(15, 15, 500, '2023-06-23 00:00:00'),
(20, 20, 600, '2023-11-01 22:21:32'),
(24, 24, 700, '2023-06-23 00:00:00'),
(100, 100, 800, '2023-11-01 22:25:32'),
(150, 150, 900, '2023-11-01 22:27:15'),
(200, 200, 1000, '2024-01-10 15:30:00'),
(250, 250, 1100, '2024-05-15 10:00:00'),
(251, 250, 1101, '2024-05-15 10:00:00');

-- ============================================================================
-- Insert Mock Challenge Categories
-- ============================================================================
-- Categories: featured, benchmark, hackathon
-- ============================================================================

INSERT INTO challenge_category (id, challenge_id, name) VALUES
(1, 1, 'featured'),
(2, 2, 'featured'),
(3, 9, 'featured'),
(4, 9, 'benchmark'),
(5, 12, 'featured'),
(6, 20, 'benchmark'),
(7, 24, 'featured'),
(200, 200, 'featured'),
(250, 250, 'featured'),
(251, 250, 'benchmark');
