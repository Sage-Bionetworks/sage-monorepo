"""
Leaderboard snapshot generation orchestration.

Encapsulates the full snapshot workflow:
  1. Fetch active models and battle evaluations from DB
  2. Compute Bradley-Terry rankings with bootstrap confidence intervals
  3. Filter models below the minimum evaluation threshold
  4. Insert the snapshot and entries into the DB
  5. Publish the snapshot (set visibility = 'public')
"""

from datetime import UTC, datetime

from bixarena_leaderboard.db_helper import (
    fetch_active_models,
    fetch_battle_evaluations,
    fetch_leaderboard_ids,
    fetch_leaderboards,
    get_db_connection,
    insert_leaderboard_entries,
    insert_leaderboard_snapshot,
    update_leaderboard_snapshot_by_id,
)
from bixarena_leaderboard.rank_battle import compute_leaderboard_bt

SNAPSHOT_IDENTIFIER_FORMAT = "snapshot_%Y-%m-%d_%H-%M"


def generate_snapshot(
    leaderboard_slug: str = "overall",
    num_bootstrap: int = 1000,
    min_evals: int = 10,
    significant: bool = False,
    dry_run: bool = False,
) -> dict:
    """
    Generate and publish a leaderboard snapshot.

    Mirrors the manual CLI flow:
      bixarena leaderboard snapshot add --min 10
      bixarena leaderboard snapshot update <id> --visibility public

    Args:
        leaderboard_slug: Slug of the leaderboard to generate (default: 'overall').
        num_bootstrap: Number of bootstrap iterations for confidence intervals.
        min_evals: Minimum evaluations per model to include in the snapshot.
        significant: If True, rank by statistical significance (CI overlap).
                     If False, rank by BT score only.
        dry_run: If True, compute rankings but do not write to the database.

    Returns:
        dict with keys:
          - snapshot_id: UUID string of created snapshot (None if dry_run)
          - snapshot_identifier: human-readable identifier string
          - entry_count: number of ranked model entries
          - evaluation_count: total evaluations used
          - leaderboard_name: display name of the leaderboard

    Raises:
        ValueError: If num_bootstrap is out of range, min_evals is negative,
                    the leaderboard slug is not found, or no evaluations exist.
    """
    if not (1 <= num_bootstrap <= 5000):
        raise ValueError(
            f"num_bootstrap must be between 1 and 5000, got {num_bootstrap}"
        )
    if min_evals < 0:
        raise ValueError(f"min_evals must be >= 0, got {min_evals}")

    with get_db_connection() as conn:
        # Validate leaderboard slug
        available_slugs = fetch_leaderboard_ids(conn)
        if leaderboard_slug not in available_slugs:
            raise ValueError(
                f"Leaderboard '{leaderboard_slug}' not found. "
                f"Available: {', '.join(available_slugs)}"
            )

        all_models = fetch_active_models(conn)
        all_evaluations = fetch_battle_evaluations(conn)

        if not all_evaluations:
            raise ValueError("No battle evaluations found in database.")

        # Find leaderboard record
        leaderboards = fetch_leaderboards(conn)
        target_leaderboard = next(
            (lb for lb in leaderboards if lb["slug"] == leaderboard_slug), None
        )
        if target_leaderboard is None:
            raise ValueError(f"Leaderboard '{leaderboard_slug}' not found.")

        models_by_name = {model["name"]: model for model in all_models}

        # Compute Bradley-Terry rankings
        leaderboard_entries = compute_leaderboard_bt(
            evaluations=all_evaluations,
            models=models_by_name,
            num_bootstrap=num_bootstrap,
            significant=significant,
        )

        # Filter by minimum evaluations
        if min_evals > 0:
            leaderboard_entries = [
                e for e in leaderboard_entries if e["voteCount"] >= min_evals
            ]
            # Reassign sequential ranks after filtering (BT score order only)
            if not significant:
                for new_rank, entry in enumerate(leaderboard_entries, start=1):
                    entry["rank"] = new_rank

        snapshot_identifier = datetime.now(UTC).strftime(SNAPSHOT_IDENTIFIER_FORMAT)
        description = (
            f"Leaderboard snapshot with {len(all_evaluations)} evaluations "
            f"and {len(leaderboard_entries)} ranked models"
        )

        if dry_run:
            return {
                "snapshot_id": None,
                "snapshot_identifier": snapshot_identifier,
                "entry_count": len(leaderboard_entries),
                "evaluation_count": len(all_evaluations),
                "leaderboard_name": target_leaderboard["name"],
            }

        # Insert snapshot (private by default)
        snapshot_id = insert_leaderboard_snapshot(
            conn,
            target_leaderboard["id"],
            snapshot_identifier,
            description,
        )

        # Insert entries
        insert_leaderboard_entries(
            conn,
            target_leaderboard["id"],
            snapshot_id,
            leaderboard_entries,
        )

        # Publish: set visibility to public using UUID (safer than identifier lookup)
        update_leaderboard_snapshot_by_id(
            conn,
            snapshot_id,
            visibility="public",
        )

        return {
            "snapshot_id": snapshot_id,
            "snapshot_identifier": snapshot_identifier,
            "entry_count": len(leaderboard_entries),
            "evaluation_count": len(all_evaluations),
            "leaderboard_name": target_leaderboard["name"],
        }
