# bixarena-leaderboard

Shared Python library for BixArena leaderboard snapshot generation.

Extracted from `bixarena-tools` to be reusable by both the CLI and the automated Lambda function.

## Modules

- **`rank_battle`** — Bradley-Terry ranking algorithm with bootstrap confidence intervals
- **`db_helper`** — PostgreSQL connection management and data access functions
- **`snapshot_generator`** — Orchestrates the full generation + auto-publish workflow
