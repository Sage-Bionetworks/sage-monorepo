-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Organization Data for Testing
-- ============================================================================
-- This migration inserts mock organization data for local development and
-- testing. It will ONLY run in the dev environment.
--
-- In stage and prod environments, data managers will manually add organization
-- data using psql or Python scripts.
--
-- Migration naming: V1000+ ensures this runs AFTER all schema migrations
-- Location: db/migration/dev/ (dev profile only)
-- ============================================================================

-- ============================================================================
-- Insert Mock Organizations
-- ============================================================================
-- Sample includes diverse organization types:
-- - Challenge platforms (DREAM, CAFA, CAGI, CASP, MICCAI)
-- - Government agencies (NIH, FDA, precisionFDA)
-- - Academic/Research institutions (Broad Institute, Allen Institute, BWH)
-- - Industry partners (AWS, AstraZeneca, Biogen)
-- - International organizations (Berlin Institute of Health)
-- - Foundations (Gates Foundation, Arthritis Foundation)
-- ============================================================================

INSERT INTO organization (id, name, login, avatar_key, website_url, description, created_at, updated_at, short_name) VALUES
(1, 'Dialogue on Reverse Engineering Assessment and Methods', 'dream', 'logo/dream.png', 'https://dreamchallenges.org', 'DREAM Challenges use crowd-sourcing to solve complex biomedical research questions.  Together, we share a vision to enable individuals and groups to collaborate openly so that the "wisdom of the crowd" provides the greatest impact on science and human health.', '2025-02-17 22:59:50', '2025-02-18 22:14:43', 'DREAM'),
(3, 'Critical Assessment of protein Function Annotation', 'cafa', 'logo/cafa.png', 'https://www.biofunctionprediction.org/cafa/', 'The Critical Assessment of protein Function Annotation algorithms (CAFA) is an experiment designed to assess the performance of computational methods dedicated to predicting protein function, often using a time challenge.', '2023-06-23 00:00:00', '2023-10-20 18:39:12', 'CAFA'),
(4, 'Critical Assessment of Genome Interpretation', 'cagi', 'logo/cagi.png', 'https://genomeinterpretation.org/challenges.html', 'The Critical Assessment of Genome Interpretation (CAGI) is a community experiment to objectively assess computational methods for predicting the phenotypic impacts of genomic variation.', '2023-06-23 00:00:00', '2023-11-18 03:49:35', 'CAGI'),
(9, 'Critical Assessment of protein Structure Prediction', 'casp', 'logo/casp.png', 'https://predictioncenter.org/', 'Our goal is to help advance the methods of identifying protein structure from sequence. The CASP experiments aim at establishing the current state of the art in protein structure prediction.', '2023-06-23 00:00:00', '2023-10-20 18:39:35', 'CASP'),
(12, 'Medical Image Computing and Computer Assisted Intervention Society', 'miccai', 'logo/miccai.png', 'http://www.miccai.org/special-interest-groups/challenges/miccai-registered-challenges/', 'The MICCAI Society is dedicated to the promotion, preservation and facilitation of research, education and practice in the field of medical image computing and computer assisted medical interventions.', '2023-06-23 00:00:00', '2023-07-26 20:13:24', 'MICCAI'),
(13, 'precisionFDA', 'pfda', 'logo/precisionfda.png', 'https://precision.fda.gov/challenges', 'A secure, collaborative, high-performance computing platform that builds a community of experts around the analysis of biological datasets in order to advance precision medicine.', '2023-06-23 00:00:00', '2023-07-26 20:13:25', 'pFDA'),
(15, 'National Institutes of Health', 'nih', 'logo/nih.png', 'https://www.nih.gov/', 'The National Institutes of Health (NIH), a part of the U.S. Department of Health and Human Services, is the nation''s medical research agency — making important discoveries that improve health and save lives.', '2023-06-23 00:00:00', '2023-10-14 06:44:12', 'NIH'),
(16, 'Allen Institute', 'allen-institute', 'logo/allen-institute.svg', 'https://alleninstitute.org/', 'The Allen Institute is an independent nonprofit bioscience research institute aimed at unlocking the mysteries of human biology through foundational science that fuels the discovery of new treatments and cures.', '2023-06-23 00:00:00', '2023-07-26 20:13:27', NULL),
(20, 'Amazon Web Services', 'aws', 'logo/aws.svg', 'https://aws.amazon.com/', 'Whether you''re looking for compute power, database storage, content delivery, or other functionality, AWS has the services to help you build sophisticated applications with increased flexibility, scalability and reliability', '2023-06-23 00:00:00', '2023-07-26 20:13:31', 'AWS'),
(26, 'AstraZeneca', 'astrazeneca', 'logo/astrazeneca.png', 'https://www.astrazeneca.com/', 'We are a global, science-led, patient-focused pharmaceutical company. We are dedicated to transforming the future of healthcare by unlocking the power of what science can do for people, society and the planet.', '2023-06-23 00:00:00', '2023-07-26 20:13:36', NULL),
(30, 'Berlin Institute of Health', 'bih', 'logo/bih.jpg', 'https://www.bihealth.org/en/', 'The mission of the BIH is medical translation: The BIH aims to translate findings from biomedical research into new approaches for personalised prediction, prevention and therapy.', '2023-06-23 00:00:00', '2023-10-14 06:42:38', 'BIH'),
(31, 'Bill and Melinda Gates Foundation', 'gates-foundation', 'logo/gates-foundation.jpg', 'https://www.gatesfoundation.org/', 'Our mission is to create a world where every person has the opportunity to live a healthy, productive life.', '2023-06-23 00:00:00', '2023-07-26 20:13:40', NULL),
(32, 'Biogen', 'biogen', 'logo/biogen.png', 'https://www.biogen.com/en_us/home.html', 'Biogen is a leading global biotechnology company that pioneers science and drives innovations for complex and devastating diseases.', '2023-06-23 00:00:00', '2023-07-26 20:13:42', NULL),
(37, 'Brigham and Women''s Hospital', 'bwh', 'logo/bwh.png', 'https://www.brighamandwomens.org/', 'Brigham and Women''s Hospital is a world-class academic medical center based in Boston, Massachusetts. A major teaching hospital of Harvard Medical School.', '2023-06-23 00:00:00', '2023-07-26 20:13:47', 'BWH'),
(41, 'Broad Institute', 'broad', 'logo/broad.jpg', 'https://www.broadinstitute.org/', 'We seek to better understand the roots of disease and narrow the gap between new biological insights and impact for patients.', '2023-06-23 00:00:00', '2023-07-26 20:13:51', NULL),
(58, 'Dana-Farber Cancer Institute', 'dfci', 'logo/dfci.png', 'https://www.dana-farber.org/', 'Since its founding in 1947, Dana-Farber Cancer Institute in Boston, Massachusetts has been committed to providing adults and children with cancer with the best treatment available today while developing tomorrow''s cures through cutting-edge research.', '2023-06-23 00:00:00', '2023-07-26 20:14:04', 'DFCI'),
(75, 'Food and Drug Administration', 'fda', 'logo/fda.jpg', 'https://www.fda.gov/', 'The Food and Drug Administration is responsible for protecting the public health by ensuring the safety, efficacy, and security of human and veterinary drugs, biological products, and medical devices.', '2023-06-23 00:00:00', '2023-07-26 20:14:11', 'FDA'),
(101, 'Harvard Medical School', 'hms', 'logo/hms.jpg', 'https://hms.harvard.edu/', 'Since 1782, Harvard Medical School has prepared students and faculty to become leaders in medicine, to create and convey biomedical knowledge, and to care for individuals throughout their lifetimes.', '2023-06-23 00:00:00', '2023-07-26 20:14:21', 'HMS'),
(134, 'Massachusetts Institute of Technology', 'mit', 'logo/mit.jpg', 'https://www.mit.edu/', 'The Massachusetts Institute of Technology is a private research university in Cambridge, Massachusetts. MIT has played a key role in the development of many aspects of modern science, engineering, mathematics, and technology.', '2023-06-23 00:00:00', '2023-07-26 20:14:30', 'MIT'),
(150, 'National Cancer Institute', 'nci', 'logo/nci.jpg', 'https://www.cancer.gov/', 'The National Cancer Institute coordinates the National Cancer Program, which conducts and supports research, training, health information dissemination, and other programs with respect to the cause, diagnosis, prevention, and treatment of cancer.', '2023-06-23 00:00:00', '2023-07-26 20:14:34', 'NCI');

-- ============================================================================
-- Insert Sample Organization Categories (Featured Organizations)
-- ============================================================================
INSERT INTO organization_category (organization_id, category) VALUES
(1, 'featured'),    -- DREAM
(3, 'featured'),    -- CAFA
(15, 'featured'),   -- NIH
(41, 'featured');   -- Broad Institute
