-- Validation script for BixArena data migration
-- This script checks data integrity and foreign key relationships after migration
--
-- Usage:
-- psql -h localhost -p 21000 -U postgres -d bixarena -f validate-migration.sql

\echo '=== BixArena Migration Validation ==='
\echo ''

-- Table row counts
\echo '=== Table Row Counts ==='
\echo ''

SELECT
    'api.leaderboard' as table_name,
    COUNT(*) as row_count
FROM api.leaderboard
UNION ALL
SELECT 'api.model', COUNT(*) FROM api.model
UNION ALL
SELECT 'api.example_prompt', COUNT(*) FROM api.example_prompt
UNION ALL
SELECT 'api.leaderboard_snapshot', COUNT(*) FROM api.leaderboard_snapshot
UNION ALL
SELECT 'api.leaderboard_entry', COUNT(*) FROM api.leaderboard_entry
UNION ALL
SELECT 'api.message', COUNT(*) FROM api.message
UNION ALL
SELECT 'api.battle', COUNT(*) FROM api.battle
UNION ALL
SELECT 'api.battle_round', COUNT(*) FROM api.battle_round
UNION ALL
SELECT 'api.battle_evaluation', COUNT(*) FROM api.battle_evaluation
UNION ALL
SELECT 'api.model_error', COUNT(*) FROM api.model_error
UNION ALL
SELECT 'auth.user', COUNT(*) FROM auth.user
UNION ALL
SELECT 'auth.external_account', COUNT(*) FROM auth.external_account
ORDER BY table_name;

\echo ''
\echo '=== Foreign Key Integrity Checks ==='
\echo ''

-- Check leaderboard_snapshot references
\echo 'Checking leaderboard_snapshot → leaderboard references...'
SELECT COUNT(*) as orphaned_snapshots
FROM api.leaderboard_snapshot ls
LEFT JOIN api.leaderboard l ON ls.leaderboard_id = l.id
WHERE l.id IS NULL;

-- Check leaderboard_entry references
\echo 'Checking leaderboard_entry → leaderboard references...'
SELECT COUNT(*) as orphaned_entries_leaderboard
FROM api.leaderboard_entry le
LEFT JOIN api.leaderboard l ON le.leaderboard_id = l.id
WHERE l.id IS NULL;

\echo 'Checking leaderboard_entry → model references...'
SELECT COUNT(*) as orphaned_entries_model
FROM api.leaderboard_entry le
LEFT JOIN api.model m ON le.model_id = m.id
WHERE m.id IS NULL;

\echo 'Checking leaderboard_entry → leaderboard_snapshot references...'
SELECT COUNT(*) as orphaned_entries_snapshot
FROM api.leaderboard_entry le
LEFT JOIN api.leaderboard_snapshot ls ON le.snapshot_id = ls.id
WHERE ls.id IS NULL;

-- Check battle references
\echo 'Checking battle → model1 references...'
SELECT COUNT(*) as orphaned_battles_model1
FROM api.battle b
LEFT JOIN api.model m ON b.model1_id = m.id
WHERE m.id IS NULL;

\echo 'Checking battle → model2 references...'
SELECT COUNT(*) as orphaned_battles_model2
FROM api.battle b
LEFT JOIN api.model m ON b.model2_id = m.id
WHERE m.id IS NULL;

-- Check battle_round references
\echo 'Checking battle_round → battle references...'
SELECT COUNT(*) as orphaned_rounds
FROM api.battle_round br
LEFT JOIN api.battle b ON br.battle_id = b.id
WHERE b.id IS NULL;

\echo 'Checking battle_round → message (prompt) references...'
SELECT COUNT(*) as orphaned_rounds_prompt
FROM api.battle_round br
LEFT JOIN api.message m ON br.prompt_message_id = m.id
WHERE m.id IS NULL;

\echo 'Checking battle_round → message (model1) references...'
SELECT COUNT(*) as orphaned_rounds_model1_msg
FROM api.battle_round br
WHERE br.model1_message_id IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM api.message m WHERE m.id = br.model1_message_id);

\echo 'Checking battle_round → message (model2) references...'
SELECT COUNT(*) as orphaned_rounds_model2_msg
FROM api.battle_round br
WHERE br.model2_message_id IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM api.message m WHERE m.id = br.model2_message_id);

-- Check battle_evaluation references
\echo 'Checking battle_evaluation → battle references...'
SELECT COUNT(*) as orphaned_evaluations
FROM api.battle_evaluation be
LEFT JOIN api.battle b ON be.battle_id = b.id
WHERE b.id IS NULL;

-- Check model_error references
\echo 'Checking model_error → model references...'
SELECT COUNT(*) as orphaned_errors_model
FROM api.model_error me
LEFT JOIN api.model m ON me.model_id = m.id
WHERE m.id IS NULL;

\echo 'Checking model_error → battle references...'
SELECT COUNT(*) as orphaned_errors_battle
FROM api.model_error me
WHERE me.battle_id IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM api.battle b WHERE b.id = me.battle_id);

\echo 'Checking model_error → battle_round references...'
SELECT COUNT(*) as orphaned_errors_round
FROM api.model_error me
WHERE me.round_id IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM api.battle_round br WHERE br.id = me.round_id);

-- Check external_account references
\echo 'Checking external_account → user references...'
SELECT COUNT(*) as orphaned_external_accounts
FROM auth.external_account ea
LEFT JOIN auth.user u ON ea.user_id = u.id
WHERE u.id IS NULL;

\echo ''
\echo '=== Data Quality Checks ==='
\echo ''

-- Check for active models
\echo 'Active models count:'
SELECT COUNT(*) as active_models FROM api.model WHERE active = true;

-- Check for active prompts
\echo 'Active prompts count:'
SELECT COUNT(*) as active_prompts FROM api.example_prompt WHERE active = true;

-- Check for public leaderboard snapshots
\echo 'Public leaderboard snapshots:'
SELECT COUNT(*) as public_snapshots FROM api.leaderboard_snapshot WHERE visibility = 'public';

-- Check for enabled users
\echo 'Enabled users count:'
SELECT COUNT(*) as enabled_users FROM auth.user WHERE enabled = true;

-- Check for verified emails
\echo 'Verified users count:'
SELECT COUNT(*) as verified_users FROM auth.user WHERE email_verified = true;

\echo ''
\echo '=== Sample Data Verification ==='
\echo ''

-- Show sample leaderboards
\echo 'Sample leaderboards:'
SELECT id, slug, name FROM api.leaderboard LIMIT 5;

\echo ''
\echo 'Sample models:'
SELECT id, slug, name, active FROM api.model LIMIT 5;

\echo ''
\echo 'Sample users:'
SELECT id, username, email, enabled FROM auth.user LIMIT 5;

\echo ''
\echo '=== Validation Complete ==='
\echo 'Review the results above to ensure:'
\echo '1. All orphaned counts are 0'
\echo '2. Row counts match expectations'
\echo '3. Sample data looks correct'
\echo ''
