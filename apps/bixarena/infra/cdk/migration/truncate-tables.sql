-- Truncate all tables in BixArena database
-- This script truncates tables in reverse dependency order to respect foreign key constraints
--
-- Usage:
-- psql -h localhost -p 21000 -U postgres -d bixarena -f truncate-tables.sql

\echo '=== BixArena Database Truncation ==='
\echo ''

-- Disable foreign key checks temporarily for cleaner truncation
SET session_replication_role = 'replica';

\echo 'Truncating API schema tables (in reverse dependency order)...'

-- API Schema (reverse dependency order)
TRUNCATE TABLE api.model_error CASCADE;
\echo '✓ Truncated api.model_error'

TRUNCATE TABLE api.battle_evaluation CASCADE;
\echo '✓ Truncated api.battle_evaluation'

TRUNCATE TABLE api.battle_round CASCADE;
\echo '✓ Truncated api.battle_round'

TRUNCATE TABLE api.battle CASCADE;
\echo '✓ Truncated api.battle'

TRUNCATE TABLE api.message CASCADE;
\echo '✓ Truncated api.message'

TRUNCATE TABLE api.leaderboard_entry CASCADE;
\echo '✓ Truncated api.leaderboard_entry'

TRUNCATE TABLE api.leaderboard_snapshot CASCADE;
\echo '✓ Truncated api.leaderboard_snapshot'

TRUNCATE TABLE api.example_prompt CASCADE;
\echo '✓ Truncated api.example_prompt'

TRUNCATE TABLE api.model CASCADE;
\echo '✓ Truncated api.model'

TRUNCATE TABLE api.leaderboard CASCADE;
\echo '✓ Truncated api.leaderboard'

\echo ''
\echo 'Truncating AUTH schema tables (in reverse dependency order)...'

-- AUTH Schema (reverse dependency order)
TRUNCATE TABLE auth.external_account CASCADE;
\echo '✓ Truncated auth.external_account'

TRUNCATE TABLE auth.user CASCADE;
\echo '✓ Truncated auth.user'

-- Re-enable foreign key checks
SET session_replication_role = 'origin';

\echo ''
\echo '=== Truncation Complete ==='
\echo ''
\echo 'Verifying table counts:'

SELECT 'api.leaderboard' as table_name, COUNT(*) as row_count FROM api.leaderboard
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
