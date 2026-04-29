"""
Handler for automated leaderboard snapshot generation.

Triggered by:
  - EventBridge scheduled Fargate task (daily at 10:00 UTC)

Environment variables (injected by CDK in AWS via Secrets Manager):
  POSTGRES_HOST        - RDS instance hostname
  POSTGRES_PORT        - RDS port
  POSTGRES_DB          - Database name
  POSTGRES_USER        - Database username
  POSTGRES_PASSWORD    - Database password

Optional environment variables:
  LEADERBOARD_SLUG          - When set, generates only that one leaderboard
                              (manual backfill / debugging). When unset, the
                              handler iterates every leaderboard in the DB.
  NUM_BOOTSTRAP             - Bootstrap iterations for BT ranking
  MIN_EVALS                 - Minimum evaluations per model to include
  SIGNIFICANT               - Rank by statistical significance if "true"
  MIN_TOTAL_BATTLES   - Iterate-all mode only: skip leaderboards with
                              fewer total battles than this
  MIN_TOTAL_MODELS    - Iterate-all mode only: skip leaderboards with
                              fewer distinct participating models than this
"""

import json
import logging
import os
import sys
import time
import uuid

from bixarena_leaderboard.snapshot_generator import (
    SnapshotRunError,
    generate_all_snapshots,
    generate_snapshot,
)

logging.basicConfig(level=logging.INFO, stream=sys.stdout)
logger = logging.getLogger(__name__)


def _int_env(name: str, default: int) -> int:
    """Read an int env var, treating set-but-empty values as missing.

    `os.getenv("X", default)` returns `""` (not the default) when the variable
    is set but empty, and `int("")` raises ValueError before any logging. This
    helper falls back to `default` for both unset and empty cases.
    """
    raw = os.getenv(name)
    if raw is None or raw == "":
        return default
    return int(raw)


def run() -> None:
    """Generate and publish leaderboard snapshots.

    Two modes:
      - Single: when LEADERBOARD_SLUG is set, generate only that slug.
      - All:    when LEADERBOARD_SLUG is unset, iterate every leaderboard
                in api.leaderboard with sparse-data gating.
    """
    correlation_id = str(uuid.uuid4())
    start = time.monotonic()

    leaderboard_slug = os.getenv("LEADERBOARD_SLUG") or None
    num_bootstrap = _int_env("NUM_BOOTSTRAP", 1000)
    min_evals = _int_env("MIN_EVALS", 10)
    significant = os.getenv("SIGNIFICANT", "false").lower() == "true"
    min_total_battles = _int_env("MIN_TOTAL_BATTLES", 30)
    min_total_models = _int_env("MIN_TOTAL_MODELS", 4)

    logger.info(
        json.dumps(
            {
                "event": "start",
                "correlation_id": correlation_id,
                "mode": "single" if leaderboard_slug else "all",
                "leaderboard_slug": leaderboard_slug,
                "num_bootstrap": num_bootstrap,
                "min_evals": min_evals,
                "significant": significant,
                "min_total_battles": min_total_battles,
                "min_total_models": min_total_models,
            }
        )
    )

    max_attempts = 2
    last_exc: Exception | None = None
    result: dict | None = None

    for attempt in range(1, max_attempts + 1):
        try:
            if leaderboard_slug:
                result = generate_snapshot(
                    leaderboard_slug=leaderboard_slug,
                    num_bootstrap=num_bootstrap,
                    min_evals=min_evals,
                    significant=significant,
                )
            else:
                result = generate_all_snapshots(
                    num_bootstrap=num_bootstrap,
                    min_evals=min_evals,
                    significant=significant,
                    min_total_battles=min_total_battles,
                    min_total_models=min_total_models,
                )
            break
        except SnapshotRunError as exc:
            # Per-leaderboard failures are surfaced via SnapshotRunError. They
            # are not transient (bad data, schema drift) and re-running the
            # full loop wastes compute and re-collides on the unique
            # snapshot-identifier constraint for slugs that already succeeded.
            # Log the structured error and exit non-zero immediately.
            duration_s = round(time.monotonic() - start, 2)
            logger.error(
                json.dumps(
                    {
                        "event": "error",
                        "correlation_id": correlation_id,
                        "error": str(exc),
                        "duration_s": duration_s,
                        "summary": exc.summary,
                    }
                )
            )
            raise
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
        # Reachable only when the generic `except Exception` branch above
        # exhausted all retries; SnapshotRunError is handled (and re-raised)
        # in its own except branch and never lands here.
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
        raise last_exc  # type: ignore[misc]

    duration_s = round(time.monotonic() - start, 2)

    if leaderboard_slug:
        # Single-slug mode: the result has direct snapshot fields.
        complete_payload = {
            "event": "complete",
            "correlation_id": correlation_id,
            "mode": "single",
            "leaderboard_slug": leaderboard_slug,
            "snapshot_id": result["snapshot_id"] if result else None,
            "snapshot_identifier": result["snapshot_identifier"] if result else None,
            "entry_count": result["entry_count"] if result else 0,
            "evaluation_count": result["evaluation_count"] if result else 0,
            "duration_s": duration_s,
        }
    else:
        # Iterate-all mode: the result is a per-leaderboard summary.
        complete_payload = {
            "event": "complete",
            "correlation_id": correlation_id,
            "mode": "all",
            "summary": result,
            "duration_s": duration_s,
        }
    logger.info(json.dumps(complete_payload))


if __name__ == "__main__":
    run()
