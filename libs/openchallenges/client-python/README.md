# OpenChallenges Client (Python)

Unified Python client & CLI for discovering challenges and organizations on the OpenChallenges platform.

Status: Core feature set implemented (challenge & org listing, validation, pagination, retries, NDJSON streaming, column selection).

Install (development, inside monorepo):

    uv run openchallenges --help

CLI Examples:

    openchallenges challenges list --limit 5
    openchallenges orgs list --limit 5 --output json

Environment Variables:

    OC_API_URL   (default http://localhost:8082/api/v1)
    OC_API_KEY   (API key authentication)

Library Usage:

```python
from openchallenges_client import OpenChallengesClient

client = OpenChallengesClient()
for ch in client.list_challenges(limit=5):
	print(ch.id, ch.name)
```

Architecture Layers:

1. CLI (Typer) -> Services (use cases) -> Gateways (SDK adapters) -> Generated API client
2. Domain models provide a stable, simplified surface.
3. Config merging: flags > env > defaults.

Feature Checklist (✅ done / 🔜 planned):

Core Data Access:
✅ List challenges (paged)  
✅ List organizations (paged)  
✅ Shared pagination + exponential backoff retries  
✅ Per-item validation with structured warnings (skips invalid items)

Output & Formatting:
✅ Table / JSON / YAML output  
✅ NDJSON streaming output (line-delimited, flush-friendly)  
✅ Wide mode (start/end dates, duration_days)  
✅ Column selection & discovery via --columns help  
✅ Blank substitution for specific nullable display fields (platform, short_name)  
🔜 Human-friendly date & duration formatting (e.g. --human-dates)

CLI UX & Diagnostics:
✅ Status normalization (case-insensitive)  
✅ Early column help short-circuit (no API call)  
🔜 Verbose summary / metrics (retries, skipped, emitted counts)  
🔜 Paging controls (--page / --page-size) if required

Architecture & Reuse:
✅ Shared pagination utility (\_shared_paging)  
🔜 Extract common helpers to a cross-project support library  
🔜 Plugin-style hooks / caching layer

Quality:
✅ Unit tests for table/json basics  
✅ Tests for blank substitutions  
✅ NDJSON streaming tests (count, wide mode, column filtering)  
✅ Column selection tests (subset, ordering, unknown & wide-only errors)  
✅ Organization parity tests for NDJSON and columns  
🔜 Coverage for verbose metrics once implemented

Roadmap Focus (near-term): human-friendly formatting, verbose metrics, potential pagination flag exposure.

Architecture Notes:

- Gateways own API client interaction and per-item validation; they emit domain summaries immunity to generated model churn.
- `_shared_paging.py` centralizes page iteration + retry strategy with exponential backoff + jitter; intended for future extraction.
- CLI layer performs projection (row shaping), column selection, and output dispatch; formatting kept minimal & pure for reuse.
- NDJSON emitter streams without buffering entire result set, suitable for piping into jq or ingestion tools.
- Column selection occurs after row shaping; wide-only columns are guarded behind --wide for explicit opt-in.

Extraction Candidate (future):

```
openchallenges_client/gateways/_shared_paging.py       # paging + retry
openchallenges_client/cli/_shared_columns.py (planned) # column selection helpers
openchallenges_client/output/formatters.py             # pure formatters
```

These could migrate to a `openchallenges-core` support package consumed by multiple domain-specific clients (datasets, submissions, etc.).

Development Tips:

- Use `--columns help` before expensive operations to explore available projection fields.
- Use `--output ndjson` with `--wide` for large result sets to optimize downstream processing.
- Combine shell tools: `openchallenges challenges stream --output ndjson | jq -r '.id'`.

License: Apache 2.0 (see repository root LICENSE).
