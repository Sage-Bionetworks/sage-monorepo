"""
Lambda handler for automated leaderboard snapshot generation.

Triggered by:
  - EventBridge scheduled rule (daily at 2 AM UTC)

Environment variables (all required, injected by CDK from Secrets Manager):
  POSTGRES_HOST     - RDS instance hostname
  POSTGRES_PORT     - RDS port
  POSTGRES_DB       - Database name
  POSTGRES_USER     - Database username
  POSTGRES_PASSWORD - Database password

Optional environment variables:
  LEADERBOARD_SLUG  - Leaderboard to generate
  NUM_BOOTSTRAP     - Bootstrap iterations for BT ranking
"""

import json
import logging
import os
import time
import uuid

from bixarena_leaderboard.snapshot_generator import generate_snapshot

logger = logging.getLogger()
logger.setLevel(logging.INFO)


def lambda_handler(event: dict, context) -> dict:
    """
    Generate and publish a leaderboard snapshot.

    Accepts an optional event payload:
      {
        "leaderboard_slug": "overall",
        "num_bootstrap": 1000
      }

    Returns the result from generate_snapshot():
      {
        "snapshot_id": "a1b2c3d4-...",
        "snapshot_identifier": "snapshot_2026-03-04_02-00",
        "entry_count": 42,
        "evaluation_count": 1500,
        "leaderboard_name": "Overall"
      }
    """
    correlation_id = str(uuid.uuid4())
    start = time.monotonic()

    leaderboard_slug = event.get(
        "leaderboard_slug", os.getenv("LEADERBOARD_SLUG", "overall")
    )
    num_bootstrap = int(event.get("num_bootstrap", os.getenv("NUM_BOOTSTRAP", "1000")))

    logger.info(
        json.dumps(
            {
                "event": "start",
                "correlation_id": correlation_id,
                "leaderboard_slug": leaderboard_slug,
                "num_bootstrap": num_bootstrap,
            }
        )
    )

    try:
        result = generate_snapshot(
            leaderboard_slug=leaderboard_slug,
            num_bootstrap=num_bootstrap,
            min_evals=10,
        )
    except Exception as exc:
        duration_s = round(time.monotonic() - start, 2)
        logger.error(
            json.dumps(
                {
                    "event": "error",
                    "correlation_id": correlation_id,
                    "error": str(exc),
                    "duration_s": duration_s,
                }
            )
        )
        raise

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

    return result
