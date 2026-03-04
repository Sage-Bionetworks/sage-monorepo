"""
Lambda handler for automated leaderboard snapshot generation.

Triggered by:
  - EventBridge scheduled rule (daily at 2 AM UTC)
  - Manual invocation via the admin API endpoint (async)

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
        "source": "manual",           # or "scheduled" (default)
        "leaderboard_slug": "overall",
        "num_bootstrap": 1000
      }

    Returns:
      {
        "statusCode": 200,
        "body": { snapshot_id, snapshot_identifier, entry_count,
                  evaluation_count, leaderboard_name, correlation_id,
                  duration_s }
      }
    """
    correlation_id = str(uuid.uuid4())
    start = time.monotonic()

    source = event.get("source", "scheduled")
    leaderboard_slug = event.get(
        "leaderboard_slug", os.getenv("LEADERBOARD_SLUG", "overall")
    )
    num_bootstrap = int(event.get("num_bootstrap", os.getenv("NUM_BOOTSTRAP", "1000")))

    # Validate inputs
    allowed_slugs = {"overall"}
    if leaderboard_slug not in allowed_slugs:
        raise ValueError(
            f"Invalid leaderboard_slug '{leaderboard_slug}'. "
            f"Allowed: {', '.join(sorted(allowed_slugs))}"
        )
    if not (100 <= num_bootstrap <= 5000):
        raise ValueError(
            f"num_bootstrap must be between 100 and 5000, got {num_bootstrap}"
        )

    logger.info(
        json.dumps(
            {
                "event": "start",
                "correlation_id": correlation_id,
                "source": source,
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

    body = {**result, "correlation_id": correlation_id, "duration_s": duration_s}
    return {"statusCode": 200, "body": body}
