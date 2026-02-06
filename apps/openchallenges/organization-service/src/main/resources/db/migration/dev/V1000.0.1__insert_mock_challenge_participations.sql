-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Challenge Participation Data for Testing
-- ============================================================================
-- This migration inserts mock challenge participation data for local
-- development and testing. It will ONLY run in the dev environment.
--
-- In stage and prod environments, data managers will manually add participation
-- data using psql or Python scripts.
--
-- Migration naming: V1000+ ensures this runs AFTER all schema and org migrations
-- Location: db/migration/dev/ (dev profile only)
-- ============================================================================

-- ============================================================================
-- Insert Mock Challenge Participations
-- ============================================================================
-- Sample includes diverse participation types:
-- - challenge_organizer: Organizations that organize challenges
-- - data_contributor: Organizations that contribute datasets
-- - sponsor: Organizations that provide financial or other support
--
-- Note: These records reference the mock organizations from V1000.0.0
-- ============================================================================

INSERT INTO challenge_participation (id, challenge_id, organization_id, role) VALUES
-- Challenge 1 participations
(1, 1, 75, 'sponsor'),

-- Challenge 3 participations
(6, 3, 150, 'data_contributor'),

-- Challenge 4 participations
(10, 4, 150, 'data_contributor'),

-- Challenge 5 participations
(13, 5, 134, 'challenge_organizer'),
(16, 5, 101, 'data_contributor'),

-- Challenge 7 participations
(41, 7, 150, 'challenge_organizer'),

-- Challenge 8 participations
(48, 8, 37, 'challenge_organizer'),

-- Challenge 9 participations
(60, 9, 150, 'challenge_organizer'),

-- Challenge 11 participations
(79, 11, 150, 'challenge_organizer'),
(81, 11, 41, 'challenge_organizer'),
(82, 11, 58, 'challenge_organizer'),
(86, 11, 41, 'data_contributor'),
(87, 11, 58, 'data_contributor'),

-- Challenge 13 participations
(121, 13, 1, 'sponsor'),

-- Challenge 14 participations
(122, 14, 58, 'challenge_organizer'),
(132, 14, 26, 'data_contributor'),

-- Challenge 15 participations
(139, 15, 1, 'challenge_organizer'),

-- Challenge 16 participations
(148, 16, 15, 'challenge_organizer'),

-- Challenge 17 participations
(152, 17, 1, 'challenge_organizer'),

-- Challenge 18 participations
(158, 18, 1, 'challenge_organizer'),

-- Challenge 19 participations
(162, 19, 1, 'challenge_organizer'),

-- Challenge 20 participations
(167, 20, 1, 'challenge_organizer'),

-- Challenge 21 participations
(172, 21, 1, 'challenge_organizer'),

-- Challenge 22 participations
(177, 22, 1, 'challenge_organizer'),

-- Challenge 23 participations
(182, 23, 1, 'challenge_organizer'),

-- Challenge 24 participations
(187, 24, 1, 'challenge_organizer'),

-- Challenge 25 participations
(192, 25, 1, 'challenge_organizer'),

-- Challenge 26 participations
(197, 26, 1, 'challenge_organizer'),

-- Challenge 27 participations
(202, 27, 1, 'challenge_organizer'),

-- Challenge 28 participations
(207, 28, 1, 'challenge_organizer'),
(208, 28, 15, 'sponsor'),

-- Challenge 29 participations
(213, 29, 1, 'challenge_organizer'),
(214, 29, 15, 'sponsor'),

-- Challenge 30 participations
(219, 30, 1, 'challenge_organizer'),
(220, 30, 20, 'sponsor'),

-- Challenge 31 participations
(225, 31, 1, 'challenge_organizer'),
(226, 31, 31, 'sponsor'),

-- Challenge 32 participations
(231, 32, 1, 'challenge_organizer'),

-- Challenge 33 participations
(236, 33, 1, 'challenge_organizer'),

-- Challenge 34 participations
(241, 34, 1, 'challenge_organizer'),

-- Challenge 35 participations
(246, 35, 1, 'challenge_organizer'),
(247, 35, 150, 'sponsor'),

-- Challenge 50 participations (Google Health - using AWS as industry example)
(350, 50, 20, 'challenge_organizer'),

-- Challenge 100 participations (Mix of organizations and roles)
(500, 100, 41, 'challenge_organizer'),
(501, 100, 58, 'data_contributor'),
(502, 100, 101, 'data_contributor'),
(503, 100, 32, 'sponsor'),
(504, 100, 31, 'sponsor');
