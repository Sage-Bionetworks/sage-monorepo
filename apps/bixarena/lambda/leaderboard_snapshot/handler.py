"""
Lambda handler for automated leaderboard snapshot generation.

Triggered by:
  - EventBridge scheduled rule (daily at 2 AM UTC)

Environment variables (injected by CDK in AWS):
  POSTGRES_HOST        - RDS instance hostname
  POSTGRES_PORT        - RDS port
  POSTGRES_DB          - Database name
  DATABASE_SECRET_ARN  - ARN of Secrets Manager secret with 'username'/'password'

Local dev (.env): set POSTGRES_USER and POSTGRES_PASSWORD directly.
  DATABASE_SECRET_ARN is not set locally; credentials are read from env vars.

Optional environment variables:
  LEADERBOARD_SLUG  - Leaderboard to generate
  NUM_BOOTSTRAP     - Bootstrap iterations for BT ranking
  MIN_EVALS         - Minimum evaluations per model to include
  SIGNIFICANT       - Rank by statistical significance if "true"
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

_secrets_client = boto3.client("secretsmanager")


def _load_db_credentials() -> None:
    """Fetch DB credentials from Secrets Manager and set as env vars.

    Only runs when DATABASE_SECRET_ARN is set (i.e. in AWS).
    Local dev uses POSTGRES_USER/POSTGRES_PASSWORD from .env directly.
    """
    secret_arn = os.getenv("DATABASE_SECRET_ARN")
    if not secret_arn:
        return
    response = _secrets_client.get_secret_value(SecretId=secret_arn)
    secret = json.loads(response["SecretString"])
    os.environ["POSTGRES_USER"] = secret["username"]
    os.environ["POSTGRES_PASSWORD"] = secret["password"]


_load_db_credentials()


def lambda_handler(event: dict, context) -> dict:
    """
    Generate and publish a leaderboard snapshot.

    Accepts an optional event payload:
      {
        "leaderboard_slug": "overall",
        "num_bootstrap": 1000,
        "min_evals": 10,
        "significant": false
      }

    Returns the result from generate_snapshot() plus params used:
      {
        "snapshot_id": "a1b2c3d4-...",
        "snapshot_identifier": "snapshot_2026-03-04_02-00",
        "entry_count": 42,
        "evaluation_count": 1500,
        "leaderboard_name": "Overall",
        "params": {
          "leaderboard_slug": "overall",
          "num_bootstrap": 1000,
          "min_evals": 10,
          "significant": false
        }
      }
    """
    correlation_id = str(uuid.uuid4())
    start = time.monotonic()

    leaderboard_slug = event.get(
        "leaderboard_slug", os.getenv("LEADERBOARD_SLUG", "overall")
    )
    num_bootstrap = int(event.get("num_bootstrap", os.getenv("NUM_BOOTSTRAP", "1000")))
    min_evals = int(event.get("min_evals", os.getenv("MIN_EVALS", "10")))
    significant = (
        str(event.get("significant", os.getenv("SIGNIFICANT", "false"))).lower()
        == "true"
    )

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

    try:
        result = generate_snapshot(
            leaderboard_slug=leaderboard_slug,
            num_bootstrap=num_bootstrap,
            min_evals=min_evals,
            significant=significant,
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

    result["params"] = {
        "leaderboard_slug": leaderboard_slug,
        "num_bootstrap": num_bootstrap,
        "min_evals": min_evals,
        "significant": significant,
    }
    return result
