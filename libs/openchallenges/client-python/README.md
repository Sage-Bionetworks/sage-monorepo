# OpenChallenges Client (Python)

Unified Python client & CLI for discovering challenges and organizations on the OpenChallenges platform.

Status: MVP scaffolding (list challenges, list organizations).

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

Planned Next Steps:

- Add filtering flags and status normalization.
- Add JSON/YAML field selection.
- Robust error mapping & retries.

License: Apache 2.0 (see repository root LICENSE).
