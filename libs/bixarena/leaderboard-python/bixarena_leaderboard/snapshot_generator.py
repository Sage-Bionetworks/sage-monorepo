"""
Leaderboard snapshot generation orchestration.

Encapsulates the full snapshot workflow:
  1. Fetch all models and battle evaluations from DB
  2. Compute Bradley-Terry rankings with bootstrap confidence intervals
  3. Filter models below the minimum evaluation threshold
  4. Insert the snapshot and entries into the DB
  5. Publish the snapshot (set visibility = 'public')
"""

import logging
from datetime import UTC, datetime

from bixarena_leaderboard.db_helper import (
    fetch_all_models,
    fetch_battle_evaluation_stats,
    fetch_battle_evaluations,
    fetch_leaderboard_ids,
    fetch_leaderboards,
    get_db_connection,
    insert_leaderboard_entries,
    insert_leaderboard_snapshot,
    update_leaderboard_snapshot_by_id,
)
from bixarena_leaderboard.rank_battle import compute_leaderboard_bt

logger = logging.getLogger(__name__)


class SnapshotRunError(RuntimeError):
    """Raised at the end of generate_all_snapshots if any slug failed.

    The summary attribute carries the full per-leaderboard outcome breakdown
    so callers (worker handler, CLI) can log structured detail before exiting.
    """

    def __init__(self, message: str, summary: dict) -> None:
        super().__init__(message)
        self.summary = summary


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

        all_models = fetch_all_models(conn)
        # 'overall' aggregates across all biomedical battles regardless of
        # category; any other slug filters to battles tagged with that
        # BiomedicalCategory (slug == category by convention).
        category_slug = None if leaderboard_slug == "overall" else leaderboard_slug
        all_evaluations = fetch_battle_evaluations(conn, category_slug=category_slug)

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


def generate_all_snapshots(
    num_bootstrap: int = 1000,
    min_evals: int = 10,
    significant: bool = False,
    min_leaderboard_battles: int = 30,
    min_leaderboard_models: int = 3,
    dry_run: bool = False,
) -> dict:
    """
    Generate snapshots for every leaderboard in the database.

    Iterates all api.leaderboard rows. For each slug, gates by the per-
    leaderboard sparse-data thresholds (min_leaderboard_battles,
    min_leaderboard_models); below either threshold, the leaderboard is
    skipped (no snapshot written, no exception raised). Above thresholds,
    delegates to generate_snapshot. Per-leaderboard exceptions are caught
    so one bad slug does not block the rest of the run.

    Args:
        num_bootstrap: Forwarded to generate_snapshot (1-5000).
        min_evals: Forwarded to generate_snapshot (per-model filter).
        significant: Forwarded to generate_snapshot.
        min_leaderboard_battles: Skip the leaderboard when its qualifying
            battle count falls below this. Bootstrap CIs are too noisy
            below ~30 evaluations.
        min_leaderboard_models: Skip the leaderboard when its qualifying
            model count falls below this. Bradley-Terry needs >= 3 distinct
            models to produce a non-trivial ranking.
        dry_run: Forwarded to generate_snapshot (computes but does not write).

    Returns:
        Summary dict with keys 'generated', 'skipped', 'failed', and 'total'.
        Each list contains one dict per leaderboard with at minimum a 'slug'
        key plus outcome-specific detail (snapshot_id, entry_count, reason,
        error, etc.).

    Raises:
        ValueError: If thresholds are negative.
        SnapshotRunError: At end of run when one or more slugs failed. The
            summary attribute carries the same dict that would otherwise be
            returned.
    """
    if min_leaderboard_battles < 0:
        raise ValueError(
            f"min_leaderboard_battles must be >= 0, got {min_leaderboard_battles}"
        )
    if min_leaderboard_models < 0:
        raise ValueError(
            f"min_leaderboard_models must be >= 0, got {min_leaderboard_models}"
        )

    skipped: list[dict] = []
    candidates: list[dict] = []

    with get_db_connection() as conn:
        leaderboards = fetch_leaderboards(conn)
        for lb in leaderboards:
            slug = lb["slug"]
            category = None if slug == "overall" else slug
            stats = fetch_battle_evaluation_stats(conn, category_slug=category)
            if stats["battle_count"] < min_leaderboard_battles:
                entry = {
                    "slug": slug,
                    "reason": "insufficient_battles",
                    "battle_count": stats["battle_count"],
                    "model_count": stats["model_count"],
                    "min_battles": min_leaderboard_battles,
                }
                skipped.append(entry)
                logger.info("Skipping leaderboard %s: %s", slug, entry)
                continue
            if stats["model_count"] < min_leaderboard_models:
                entry = {
                    "slug": slug,
                    "reason": "insufficient_models",
                    "battle_count": stats["battle_count"],
                    "model_count": stats["model_count"],
                    "min_models": min_leaderboard_models,
                }
                skipped.append(entry)
                logger.info("Skipping leaderboard %s: %s", slug, entry)
                continue
            candidates.append(
                {
                    "slug": slug,
                    "name": lb["name"],
                    "battle_count": stats["battle_count"],
                    "model_count": stats["model_count"],
                }
            )

    generated: list[dict] = []
    failed: list[dict] = []
    for cand in candidates:
        slug = cand["slug"]
        try:
            result = generate_snapshot(
                leaderboard_slug=slug,
                num_bootstrap=num_bootstrap,
                min_evals=min_evals,
                significant=significant,
                dry_run=dry_run,
            )
            generated.append({"slug": slug, **result})
            logger.info(
                "Generated leaderboard %s: %d entries from %d evaluations",
                slug,
                result.get("entry_count", 0),
                result.get("evaluation_count", 0),
            )
        except Exception as exc:  # noqa: BLE001 — intentional broad catch
            failed.append({"slug": slug, "error": str(exc)})
            logger.exception("Failed to generate leaderboard %s", slug)

    summary = {
        "total": len(leaderboards),
        "generated": generated,
        "skipped": skipped,
        "failed": failed,
    }
    logger.info(
        "Snapshot run complete: %d generated, %d skipped, %d failed (of %d total)",
        len(generated),
        len(skipped),
        len(failed),
        len(leaderboards),
    )

    if failed:
        raise SnapshotRunError(
            f"{len(failed)} leaderboard(s) failed to generate",
            summary=summary,
        )
    return summary
