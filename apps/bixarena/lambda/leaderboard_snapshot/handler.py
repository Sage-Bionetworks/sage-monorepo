"""
Lambda handler for automated leaderboard snapshot generation.

Triggered by:
  - EventBridge scheduled rule (daily at 2 AM UTC)
  - Manual invocation via the admin API endpoint (async)

Environment variables (all required, injected by CDK from Secrets Manager):
  POSTGRES_HOST     - RDS instance hostname
  POSTGRES_PORT     - RDS port (default: 5432)
  POSTGRES_DB       - Database name
  POSTGRES_USER     - Database username
  POSTGRES_PASSWORD - Database password

Optional environment variables:
  LEADERBOARD_SLUG  - Leaderboard to generate (default: 'overall')
  N_BOOTSTRAP       - Bootstrap iterations for BT ranking (default: 1000)
  SNS_TOPIC_ARN     - SNS topic ARN for success/failure notifications
"""

import json
import logging
import os
import time
import uuid

import boto3
from bixarena_leaderboard.snapshot_generator import generate_snapshot

logger = logging.getLogger()
logger.setLevel(logging.INFO)


def _publish_sns(topic_arn: str, subject: str, message: dict) -> None:
    """Publish a notification to SNS. Non-fatal — errors are logged and swallowed."""
    try:
        sns = boto3.client("sns")
        sns.publish(
            TopicArn=topic_arn,
            Subject=subject,
            Message=json.dumps(message, default=str),
        )
    except Exception as exc:  # noqa: BLE001
        logger.warning("SNS publish failed (non-fatal): %s", exc)


def lambda_handler(event: dict, context) -> dict:
    """
    Generate and publish a leaderboard snapshot.

    Accepts an optional event payload:
      {
        "source": "manual",           # or "scheduled" (default)
        "user_email": "user@org.com", # present for manual invocations
        "leaderboard_slug": "overall",
        "n_bootstrap": 1000
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
    user_email = event.get("user_email")
    leaderboard_slug = event.get(
        "leaderboard_slug", os.getenv("LEADERBOARD_SLUG", "overall")
    )
    n_bootstrap = int(event.get("n_bootstrap", os.getenv("N_BOOTSTRAP", "1000")))
    sns_topic_arn = os.getenv("SNS_TOPIC_ARN", "")

    # Validate inputs
    allowed_slugs = {"overall"}
    if leaderboard_slug not in allowed_slugs:
        raise ValueError(
            f"Invalid leaderboard_slug '{leaderboard_slug}'. "
            f"Allowed: {', '.join(sorted(allowed_slugs))}"
        )
    if not (100 <= n_bootstrap <= 5000):
        raise ValueError(f"n_bootstrap must be between 100 and 5000, got {n_bootstrap}")

    logger.info(
        json.dumps(
            {
                "event": "start",
                "correlation_id": correlation_id,
                "source": source,
                "user_email": user_email,
                "leaderboard_slug": leaderboard_slug,
                "n_bootstrap": n_bootstrap,
            }
        )
    )

    try:
        result = generate_snapshot(
            leaderboard_slug=leaderboard_slug,
            num_bootstrap=n_bootstrap,
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
        if sns_topic_arn:
            _publish_sns(
                sns_topic_arn,
                subject="[BixArena] Leaderboard snapshot FAILED",
                message={
                    "correlation_id": correlation_id,
                    "source": source,
                    "user_email": user_email,
                    "leaderboard_slug": leaderboard_slug,
                    "error": str(exc),
                    "duration_s": duration_s,
                },
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

    if sns_topic_arn:
        _publish_sns(
            sns_topic_arn,
            subject="[BixArena] Leaderboard snapshot generated",
            message={
                "correlation_id": correlation_id,
                "source": source,
                "user_email": user_email,
                "leaderboard_name": result["leaderboard_name"],
                "snapshot_id": result["snapshot_id"],
                "snapshot_identifier": result["snapshot_identifier"],
                "entry_count": result["entry_count"],
                "evaluation_count": result["evaluation_count"],
                "duration_s": duration_s,
            },
        )

    body = {**result, "correlation_id": correlation_id, "duration_s": duration_s}
    return {"statusCode": 200, "body": body}
