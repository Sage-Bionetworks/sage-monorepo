"""Load a release beside the live collections, then swap it in.

Every collection is imported and indexed as `<name>_staging`, so the live
collections keep serving the previous release until the swap. A failure at any
point before the swap leaves the previous release fully live.
"""

from dataclasses import dataclass, field
import logging

from pymongo import database
from pymongo.errors import PyMongoError

from explorers_data_loader.logs import BYTES_PER_MEGABYTE, event_fields

logger = logging.getLogger(__name__)

STAGING_SUFFIX = "_staging"

# MongoDB's own collections, such as system.views, belong to no release
SYSTEM_COLLECTION_PREFIX = "system."


def staging_collection_name(collection_name: str) -> str:
    return f"{collection_name}{STAGING_SUFFIX}"


class StagingVerificationError(Exception):
    pass


class SwapError(Exception):
    """A swap that stopped partway, with each collection's release recorded"""

    def __init__(self, swapped: list[str], not_swapped: list[str]):
        self.swapped = swapped
        self.not_swapped = not_swapped
        if swapped:
            message = (
                f"Swap failed after {len(swapped)} of "
                f"{len(swapped) + len(not_swapped)} collections. On the new release: "
                f"{', '.join(swapped)}. Still on the previous release: "
                f"{', '.join(not_swapped)}. dataversion was not updated, so the API "
                "caches were not cleared. Re-run the import to finish the release"
            )
        else:
            message = (
                "Swap failed before any collection was swapped, so the previous "
                "release is still fully live"
            )
        super().__init__(message)


def drop_staging_leftovers(db: database.Database) -> list[str]:
    """Drop the staging collections a failed run left behind"""
    leftovers = sorted(
        name for name in db.list_collection_names() if name.endswith(STAGING_SUFFIX)
    )
    for name in leftovers:
        db.drop_collection(name)
    logger.log(
        logging.WARNING if leftovers else logging.INFO,
        "Dropped %d leftover staging collections",
        len(leftovers),
        extra=event_fields("staging_leftovers_dropped", collections=leftovers),
    )
    return leftovers


def verify_staging_collections(
    db: database.Database, collection_names: list[str]
) -> None:
    """Stop before the swap if any staging collection is missing or empty"""
    empty_or_missing = [
        name
        for name in collection_names
        if db.get_collection(staging_collection_name(name)).find_one(
            {}, projection={"_id": 1}
        )
        is None
    ]
    if empty_or_missing:
        logger.error(
            "Staging collections are missing or empty, so the previous release "
            "stays live",
            extra=event_fields(
                "staging_verification_failed", empty_or_missing=empty_or_missing
            ),
        )
        raise StagingVerificationError(
            f"Staging collections are missing or empty: {', '.join(empty_or_missing)}"
        )
    logger.info(
        "All staging collections contain documents",
        extra=event_fields(
            "staging_collections_verified", collections=len(collection_names)
        ),
    )


def swap_staging_collections(
    db: database.Database, collection_names: list[str]
) -> list[str]:
    """Rename each staging collection over its live collection, in order.

    Callers list dataversion last: the APIs clear their caches when it changes,
    so it must only change once every other collection is live. Each rename
    replaces its live collection in one operation, indexes included, so no
    collection is ever missing or unindexed.
    """
    swapped: list[str] = []
    for name in collection_names:
        try:
            db.get_collection(staging_collection_name(name)).rename(
                name, dropTarget=True
            )
        except PyMongoError as error:
            logger.error(
                "Failed to swap in collection '%s'",
                name,
                extra=event_fields(
                    "collection_swap_failed", collection=name, error=str(error)
                ),
            )
            raise SwapError(swapped, collection_names[len(swapped) :]) from error
        swapped.append(name)
        logger.info(
            "Swapped in collection '%s'",
            name,
            extra=event_fields("collection_swapped", collection=name),
        )
    return swapped


@dataclass
class OrphanCleanup:
    dropped: list[str] = field(default_factory=list)
    failed: list[str] = field(default_factory=list)


def drop_orphaned_collections(
    db: database.Database, release_collection_names: list[str]
) -> OrphanCleanup:
    """Drop live collections the release doesn't include.

    Runs only after a successful swap, so a failed import never removes a
    collection the previous release still reads. The new release is already
    live by then, so a collection that can't be dropped is a warning that the
    next run retries, not a failed run.
    """
    cleanup = OrphanCleanup()
    try:
        existing_names = db.list_collection_names()
    except PyMongoError as error:
        logger.warning(
            "Could not list collections, so no orphaned collections were dropped",
            extra=event_fields("orphan_cleanup_failed", error=str(error)),
        )
        return cleanup

    release_names = set(release_collection_names)
    orphaned_names = sorted(
        name
        for name in existing_names
        if name not in release_names
        and not name.startswith(SYSTEM_COLLECTION_PREFIX)
        and not name.endswith(STAGING_SUFFIX)
    )
    for name in orphaned_names:
        try:
            db.drop_collection(name)
        except PyMongoError as error:
            cleanup.failed.append(name)
            logger.warning(
                "Failed to drop orphaned collection '%s'",
                name,
                extra=event_fields(
                    "orphan_drop_failed", collection=name, error=str(error)
                ),
            )
            continue
        cleanup.dropped.append(name)
        logger.warning(
            "Dropped orphaned collection '%s'",
            name,
            extra=event_fields("orphan_dropped", collection=name),
        )
    logger.info(
        "Dropped %d orphaned collections",
        len(cleanup.dropped),
        extra=event_fields(
            "orphan_cleanup_completed",
            dropped=cleanup.dropped,
            failed=cleanup.failed,
        ),
    )
    return cleanup


def log_database_size(db: database.Database, stage: str) -> None:
    """Log storage use, which staging roughly doubles until the swap"""
    try:
        stats = db.command("dbStats", scale=BYTES_PER_MEGABYTE)
    except PyMongoError as error:
        logger.warning(
            "Could not read database storage stats (%s)",
            stage,
            extra=event_fields(
                "database_size_unavailable", stage=stage, error=str(error)
            ),
        )
        return
    logger.info(
        "Database storage (%s)",
        stage,
        extra=event_fields(
            "database_size_measured",
            stage=stage,
            collections=stats.get("collections"),
            data_size_mb=round(stats.get("dataSize", 0)),
            storage_size_mb=round(stats.get("storageSize", 0)),
            index_size_mb=round(stats.get("indexSize", 0)),
        ),
    )
