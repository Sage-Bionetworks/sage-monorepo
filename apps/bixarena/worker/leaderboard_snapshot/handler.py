"""
Handler for automated leaderboard snapshot generation.

Triggered by:
  - EventBridge scheduled task (daily at 10:00 UTC)

Environment variables (injected by CDK in AWS via Secrets Manager):
  POSTGRES_HOST        - RDS instance hostname
  POSTGRES_PORT        - RDS port
  POSTGRES_DB          - Database name
  POSTGRES_USER        - Database username
  POSTGRES_PASSWORD    - Database password

Optional environment variables:
  LEADERBOARD_SLUG  - Leaderboard to generate
  NUM_BOOTSTRAP     - Bootstrap iterations for BT ranking
  MIN_EVALS         - Minimum evaluations per model to include
  SIGNIFICANT       - Rank by statistical significance if "true"
"""

import json
import logging
import os
import sys
import time
import uuid

from bixarena_leaderboard.snapshot_generator import generate_snapshot

logging.basicConfig(level=logging.INFO, stream=sys.stdout)
logger = logging.getLogger(__name__)


def run() -> None:
    """Generate and publish a leaderboard snapshot."""
    correlation_id = str(uuid.uuid4())
    start = time.monotonic()

    leaderboard_slug = os.getenv("LEADERBOARD_SLUG", "overall")
    num_bootstrap = int(os.getenv("NUM_BOOTSTRAP", "1000"))
    min_evals = int(os.getenv("MIN_EVALS", "10"))
    significant = os.getenv("SIGNIFICANT", "false").lower() == "true"

    logger.info(
        json.dumps(
            {
                "event": "start",
                "correlation_id": correlation_id,
                "leaderboard_slug": leaderboard_slug,
                "num_bootstrap": num_bootstrap,
                "min_evals": min_evals,
                "significant": significant,
            }
        )
    )

    max_attempts = 2
    last_exc: Exception | None = None

    for attempt in range(1, max_attempts + 1):
        try:
            result = generate_snapshot(
                leaderboard_slug=leaderboard_slug,
                num_bootstrap=num_bootstrap,
                min_evals=min_evals,
                significant=significant,
            )
            break
        except Exception as exc:
            last_exc = exc
            if attempt < max_attempts:
                retry_delay_s = 10 * (2 ** (attempt - 1))
                logger.warning(
                    json.dumps(
                        {
                            "event": "retry",
                            "correlation_id": correlation_id,
                            "attempt": attempt,
                            "error": str(exc),
                            "retry_in_s": retry_delay_s,
                        }
                    )
                )
                time.sleep(retry_delay_s)
    else:
        duration_s = round(time.monotonic() - start, 2)
        logger.error(
            json.dumps(
                {
                    "event": "error",
                    "correlation_id": correlation_id,
                    "error": str(last_exc),
                    "duration_s": duration_s,
                }
            )
        )
        raise last_exc

    duration_s = round(time.monotonic() - start, 2)

    logger.info(
        json.dumps(
            {
                "event": "complete",
                "correlation_id": correlation_id,
                "snapshot_id": result["snapshot_id"],
                "snapshot_identifier": result["snapshot_identifier"],
                "entry_count": result["entry_count"],
                "evaluation_count": result["evaluation_count"],
                "duration_s": duration_s,
            }
        )
    )


if __name__ == "__main__":
    run()
