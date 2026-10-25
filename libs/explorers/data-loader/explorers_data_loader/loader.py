from dataclasses import dataclass, field
from enum import StrEnum
import logging
from os import getcwd
from time import monotonic
from uuid import uuid4

from pymongo import MongoClient

from explorers_data_loader.logs import (
    configure_logging,
    elapsed_ms,
    event_fields,
    read_log_format,
    release_label,
)
from explorers_data_loader.collection_config import (
    DATA_VERSION_COLLECTION,
    read_collection_configs,
)
from explorers_data_loader.mongo import create_collections_indexes, import_collection
from explorers_data_loader.settings import (
    DatabaseSettings,
    LoaderPaths,
    LOAD_TEAM_IMAGES_ENV,
    ProductSettings,
    ReleaseSettings,
    TeamImagesSettings,
)
from explorers_data_loader.staging import (
    SwapError,
    drop_orphaned_collections,
    drop_staging_leftovers,
    log_database_size,
    swap_staging_collections,
    verify_staging_collections,
)
from explorers_data_loader.synapse import (
    download_synapse_data,
    download_team_images,
    login_to_synapse,
)
from explorers_data_loader.team_images import (
    TEAM_IMAGES_COLLECTION_NAMES,
    upload_team_images_to_staging,
)

logger = logging.getLogger(__name__)


class Step(StrEnum):
    """The loader's steps, in order, as reported by run_completed's failed_step"""

    READ_SETTINGS = "read_settings"
    READ_COLLECTIONS = "read_collections"
    DOWNLOAD = "download"
    DROP_STAGING_LEFTOVERS = "drop_staging_leftovers"
    IMPORT_COLLECTIONS = "import_collections"
    CREATE_INDEXES = "create_indexes"
    UPLOAD_TEAM_IMAGES = "upload_team_images"
    VERIFY_STAGING = "verify_staging"
    SWAP = "swap"
    DROP_ORPHANS = "drop_orphans"


class LiveRelease(StrEnum):
    """Which release the site serves once a run ends, as reported by run_completed"""

    NEW = "new"
    PREVIOUS = "previous"
    MIXED = "mixed"


@dataclass
class RunSummary:
    step: Step = Step.READ_SETTINGS
    collections_imported: list[str] = field(default_factory=list)
    collections_swapped: list[str] = field(default_factory=list)
    collections_not_swapped: list[str] = field(default_factory=list)
    orphans_dropped: list[str] = field(default_factory=list)
    orphans_failed: list[str] = field(default_factory=list)

    def failed_live_release(self) -> LiveRelease:
        return LiveRelease.MIXED if self.collections_swapped else LiveRelease.PREVIOUS

    def collection_fields(self) -> dict[str, list[str]]:
        return {
            "collections_imported": self.collections_imported,
            "collections_swapped": self.collections_swapped,
            "collections_not_swapped": self.collections_not_swapped,
            "orphans_dropped": self.orphans_dropped,
            "orphans_failed": self.orphans_failed,
        }


def run(product: ProductSettings, working_dir: str | None = None) -> None:
    """Download a data release from Synapse and load it into MongoDB"""
    run_context = {"run_id": str(uuid4()), "product": product.name}
    configure_logging(read_log_format(), run_context)
    summary = RunSummary()
    started = monotonic()
    try:
        load_release(product, working_dir or getcwd(), run_context, summary)
    except Exception as error:
        logger.error(
            "Data load for %s failed during step '%s'",
            release_label(run_context),
            summary.step,
            exc_info=True,
            extra=event_fields(
                "run_completed",
                outcome="failed",
                failed_step=summary.step,
                error=str(error),
                live_release=summary.failed_live_release(),
                duration_ms=elapsed_ms(started),
                **summary.collection_fields(),
            ),
        )
        raise
    logger.info(
        "Data load completed for %s",
        release_label(run_context),
        extra=event_fields(
            "run_completed",
            outcome="succeeded",
            failed_step=None,
            live_release=LiveRelease.NEW,
            duration_ms=elapsed_ms(started),
            **summary.collection_fields(),
        ),
    )


def load_release(
    product: ProductSettings,
    working_dir: str,
    run_context: dict[str, str],
    summary: RunSummary,
) -> None:
    paths = LoaderPaths.from_working_dir(working_dir)
    db_settings = DatabaseSettings.from_env()
    release = ReleaseSettings.from_env()
    team_images = TeamImagesSettings.from_env() if product.loads_team_images else None
    run_context["data_file"] = release.data_file
    run_context["data_version"] = release.data_version
    logger.info(
        "Data load started for %s",
        release_label(run_context),
        extra=event_fields("run_started"),
    )
    if product.loads_team_images and team_images is None:
        logger.info(
            "Skipping team images because %s is false",
            LOAD_TEAM_IMAGES_ENV,
            extra=event_fields("team_images_skipped"),
        )

    summary.step = Step.READ_COLLECTIONS
    # Read before downloading, so an invalid collection list fails in seconds
    file_collections = read_collection_configs(paths.collections_filepath)
    imported_collections = [*file_collections, DATA_VERSION_COLLECTION]
    file_collection_names = [collection.name for collection in file_collections]
    # dataversion goes last: the APIs clear their caches when it changes, so it
    # must only be swapped in once every other collection is live
    swapped_names = [
        *file_collection_names,
        *(TEAM_IMAGES_COLLECTION_NAMES if team_images else ()),
        DATA_VERSION_COLLECTION.name,
    ]
    # The team images collections stay part of the release when this run
    # skips them, so dropping orphans never removes the photos already loaded
    release_names = [
        *file_collection_names,
        *(TEAM_IMAGES_COLLECTION_NAMES if product.loads_team_images else ()),
        DATA_VERSION_COLLECTION.name,
    ]

    summary.step = Step.DOWNLOAD
    syn = login_to_synapse()
    download_synapse_data(syn, release, paths.download_dir)
    team_image_paths = (
        download_team_images(syn, team_images.folder_id, paths.team_images_dir)
        if team_images
        else []
    )

    with MongoClient(db_settings.client_uri) as client:
        db = client.get_database(db_settings.name)
        log_database_size(db, "before_import")

        summary.step = Step.DROP_STAGING_LEFTOVERS
        drop_staging_leftovers(db)

        summary.step = Step.IMPORT_COLLECTIONS
        for collection in imported_collections:
            import_collection(db_settings, collection, paths.download_dir)
            summary.collections_imported.append(collection.name)

        summary.step = Step.CREATE_INDEXES
        create_collections_indexes(db, file_collections)

        if team_image_paths:
            summary.step = Step.UPLOAD_TEAM_IMAGES
            upload_team_images_to_staging(db, team_image_paths)

        summary.step = Step.VERIFY_STAGING
        verify_staging_collections(db, swapped_names)

        summary.step = Step.SWAP
        summary.collections_not_swapped = list(swapped_names)
        try:
            summary.collections_swapped = swap_staging_collections(db, swapped_names)
        except SwapError as error:
            summary.collections_swapped = error.swapped
            summary.collections_not_swapped = error.not_swapped
            raise
        summary.collections_not_swapped = []

        summary.step = Step.DROP_ORPHANS
        orphan_cleanup = drop_orphaned_collections(db, release_names)
        summary.orphans_dropped = orphan_cleanup.dropped
        summary.orphans_failed = orphan_cleanup.failed

        log_database_size(db, "after_load")
