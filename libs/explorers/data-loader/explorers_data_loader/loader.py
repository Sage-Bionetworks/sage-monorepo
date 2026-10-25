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
from explorers_data_loader.mongo import (
    DATA_VERSION_COLLECTION_FILENAME,
    DATA_VERSION_COLLECTION_NAME,
    create_collections_indexes,
    import_collection,
    read_collections,
)
from explorers_data_loader.settings import (
    DatabaseSettings,
    LoaderPaths,
    ProductSettings,
    ReleaseSettings,
)
from explorers_data_loader.staging import (
    SwapError,
    verify_staging_collections,
    drop_staging_leftovers,
    log_database_size,
    swap_staging_collections,
)
from explorers_data_loader.synapse import download_synapse_data

logger = logging.getLogger(__name__)


class Step(StrEnum):
    """The loader's steps, in order, as reported by run_completed's failed_step"""

    READ_SETTINGS = "read_settings"
    READ_COLLECTIONS = "read_collections"
    DOWNLOAD = "download"
    DROP_STAGING_LEFTOVERS = "drop_staging_leftovers"
    IMPORT_COLLECTIONS = "import_collections"
    CREATE_INDEXES = "create_indexes"
    VERIFY_STAGING = "verify_staging"
    SWAP = "swap"


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

    def failed_live_release(self) -> LiveRelease:
        return LiveRelease.MIXED if self.collections_swapped else LiveRelease.PREVIOUS

    def collection_fields(self) -> dict[str, list[str]]:
        return {
            "collections_imported": self.collections_imported,
            "collections_swapped": self.collections_swapped,
            "collections_not_swapped": self.collections_not_swapped,
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
    run_context["data_file"] = release.data_file
    run_context["data_version"] = release.data_version
    logger.info(
        "Data load started for %s",
        release_label(run_context),
        extra=event_fields("run_started"),
    )

    summary.step = Step.READ_COLLECTIONS
    # Read before downloading, so an invalid collection list fails in seconds.
    # dataversion goes last: the APIs clear their caches when it changes, so it
    # must only be swapped in once every other collection is live.
    release_collections = [
        *read_collections(paths.collections_filepath),
        (DATA_VERSION_COLLECTION_NAME, DATA_VERSION_COLLECTION_FILENAME),
    ]
    collection_names = [name for name, _ in release_collections]

    summary.step = Step.DOWNLOAD
    download_synapse_data(release, paths.download_dir)

    with MongoClient(db_settings.client_uri) as client:
        db = client.get_database(db_settings.name)
        log_database_size(db, "before_import")

        summary.step = Step.DROP_STAGING_LEFTOVERS
        drop_staging_leftovers(db)

        summary.step = Step.IMPORT_COLLECTIONS
        for collection_name, collection_filename in release_collections:
            import_collection(
                db_settings,
                product,
                collection_name,
                collection_filename,
                paths.download_dir,
            )
            summary.collections_imported.append(collection_name)

        summary.step = Step.CREATE_INDEXES
        create_collections_indexes(db, paths.collections_indexes_filepath)

        summary.step = Step.VERIFY_STAGING
        verify_staging_collections(db, collection_names)

        summary.step = Step.SWAP
        summary.collections_not_swapped = list(collection_names)
        try:
            summary.collections_swapped = swap_staging_collections(db, collection_names)
        except SwapError as error:
            summary.collections_swapped = error.swapped
            summary.collections_not_swapped = error.not_swapped
            raise
        summary.collections_not_swapped = []

        log_database_size(db, "after_swap")
