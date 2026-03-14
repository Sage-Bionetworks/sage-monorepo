-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Quest Posts for Testing
-- ============================================================================
-- Inserts sample posts to test quest post management features including
-- unlock gates (publish date, progress gating, tier gating).
--
-- Posts:
-- - Post 0: Public, no gates
-- - Post 1: Public, no gates
-- - Post 2: Public, no gates
-- - Post 3: Progress-gated (requires 10 battles — unlocked, quest has 35 blocks)
-- - Post 4: Progress-gated (requires 100 battles)
-- - Post 5: Tier-gated (requires knight tier)
-- - Post 6: Scheduled for future publication
-- ============================================================================

UPDATE api.quest
SET active_post_index = 2, updated_at = now()
WHERE quest_id = 'build-bioarena-together';

-- Post 0: Public (no gates)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 0, '2026-02-03', 'Mock Post 0',
    'This is mock post 0 for local development testing.',
    '["https://example.com/img/post0.jpg"]'::jsonb,
    NULL, NULL, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 1: Public (no gates)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 1, '2026-02-13', 'Mock Post 1',
    'This is mock post 1 for local development testing.',
    '["https://example.com/img/post1a.jpg", "https://example.com/img/post1b.jpg"]'::jsonb,
    NULL, NULL, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 2: Public (no gates)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 2, '2026-02-20', 'Mock Post 2',
    'This is mock post 2 for local development testing.',
    '["https://example.com/img/post2.jpg"]'::jsonb,
    NULL, NULL, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 3: Progress-gated (requires 10 total battles — already unlocked since quest has 35 blocks)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 3, '2026-02-27', 'Mock Post 3 (Unlocked Reward)',
    'This progress-gated post is now unlocked because the community reached 10 blocks.',
    '["https://example.com/img/post3.jpg"]'::jsonb,
    NULL, 10, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 4: Progress-gated (requires 100 total battles)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 4, '2026-03-02', 'Mock Post 4 (Progress-Gated)',
    'This post is only unlocked when 100 battles have been completed.',
    '["https://example.com/img/post4.jpg"]'::jsonb,
    NULL, 100, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 5: Tier-gated (requires knight tier)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 5, '2026-03-06', 'Mock Post 5 (Knight-Gated)',
    'This post is only unlocked for knights and champions.',
    '["https://example.com/img/post5.jpg"]'::jsonb,
    NULL, NULL, 'knight'
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 6: Scheduled for future publication
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 6, '2026-12-01', 'Mock Post 6 (Future-Scheduled)',
    'This post should not appear until its publish date.',
    '["https://example.com/img/post6.jpg"]'::jsonb,
    '2026-12-01 09:00:00+00', NULL, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';
