-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Quest Posts for Testing
-- ============================================================================
-- This migration updates the mock quest with metadata and inserts sample
-- posts to test the quest post management features including unlock gates
-- (publish date, progress gating, tier gating).
--
-- Posts:
-- - Post 0: Public, published (launch announcement)
-- - Post 1: Public, published (chapter 1)
-- - Post 2: Public, published (chapter 2)
-- - Post 3: Progress-gated (requires 100 battles)
-- - Post 4: Tier-gated (requires knight tier)
-- - Post 5: Scheduled for future publication
-- ============================================================================

-- ============================================================================
-- Update mock quest with metadata
-- ============================================================================
UPDATE api.quest
SET
    title = 'Build BioArena Together',
    description = 'Join forces with the BioArena community to build a Minecraft arena together. Every battle you complete adds a block to our shared creation.',
    goal = 2850,
    active_post_index = 2,
    updated_at = now()
WHERE quest_id = 'build-bioarena-together';

-- ============================================================================
-- Insert mock quest posts
-- ============================================================================

-- Post 0: Public launch announcement (no gates)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 0, '2026-02-03', 'Launch: Build BioArena Together',
    'Welcome to the BioArena Community Quest! We are embarking on an exciting journey to build a Minecraft arena together. Every battle you complete adds a block to our shared creation.',
    '["https://raw.githubusercontent.com/example/minecraft-arena-launch.jpg"]'::jsonb,
    NULL, NULL, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 1: Chapter 1 (public, published)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 1, '2026-02-13', 'Chapter 1: Laying the First Stones',
    'The foundation of our arena takes shape. Contributors from around the world have begun placing the first blocks, and the outline of something magnificent is emerging.',
    '["https://raw.githubusercontent.com/example/minecraft-arena-ch1-1.jpg", "https://raw.githubusercontent.com/example/minecraft-arena-ch1-2.jpg"]'::jsonb,
    NULL, NULL, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 2: Chapter 2 (public, published)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 2, '2026-02-20', 'Chapter 2: Shadows Over Stone',
    'As the walls rise higher, mysterious shadows begin to dance across the stone. The arena is taking on a life of its own, shaped by the collective effort of our community.',
    '["https://raw.githubusercontent.com/example/minecraft-arena-ch2-1.jpg"]'::jsonb,
    NULL, NULL, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 3: Progress-gated (requires 100 total battles to unlock content)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 3, '2026-03-02', 'Chapter 3: The Hundred Block Milestone',
    'A special chapter unlocked by the community reaching 100 battles! The arena floor is now complete, revealing an intricate pattern that only becomes visible from above.',
    '["https://raw.githubusercontent.com/example/minecraft-arena-ch3-1.jpg"]'::jsonb,
    NULL, 100, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 4: Tier-gated (requires knight tier)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 4, NULL, 'Knight''s Codex - Folio 1',
    'Exclusive lore for Knights and Champions. The ancient texts reveal the true purpose of the arena — it was never just a battleground, but a convergence point for biomedical knowledge.',
    '["https://raw.githubusercontent.com/example/knights-codex-1.jpg"]'::jsonb,
    NULL, NULL, 'knight'
FROM api.quest WHERE quest_id = 'build-bioarena-together';

-- Post 5: Scheduled for future publication (should not appear in public responses)
INSERT INTO api.quest_post (quest_id, post_index, date, title, description, images, publish_date, required_progress, required_tier)
SELECT id, 5, '2026-12-01', 'Chapter 4: Rising Towers',
    'Scheduled post that should not be visible until the publish date. The towers of the arena reach toward the sky...',
    '["https://raw.githubusercontent.com/example/minecraft-arena-ch4-1.jpg"]'::jsonb,
    '2026-12-01 09:00:00+00', NULL, NULL
FROM api.quest WHERE quest_id = 'build-bioarena-together';
