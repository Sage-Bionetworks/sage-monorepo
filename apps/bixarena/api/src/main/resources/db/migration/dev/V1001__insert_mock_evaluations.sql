-- ============================================================================
-- DEV ENVIRONMENT ONLY: Mock Battle Evaluations for Leaderboard Testing
-- ============================================================================
-- Inserts one evaluation per battle with randomised outcomes so that
-- leaderboard snapshot generation has data to work with locally.
-- ============================================================================

INSERT INTO api.battle_evaluation (battle_id, outcome)
SELECT
    id,
    (ARRAY['model1', 'model2', 'tie'])[floor(random() * 3 + 1)::int]
FROM api.battle
ON CONFLICT (battle_id) DO NOTHING;
