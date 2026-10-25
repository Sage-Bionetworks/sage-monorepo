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
from explorers_data_loader.synapse import download_synapse_data

logger = logging.getLogger(__name__)


class Step(StrEnum):
    """The loader's steps, in order, as reported by run_completed's failed_step"""

    READ_SETTINGS = "read_settings"
    READ_COLLECTIONS = "read_collections"
    DOWNLOAD = "download"
    IMPORT_COLLECTIONS = "import_collections"
    CREATE_INDEXES = "create_indexes"
    IMPORT_DATA_VERSION = "import_data_version"


@dataclass
class RunSummary:
    step: Step = Step.READ_SETTINGS
    collections_imported: list[str] = field(default_factory=list)


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
            "Data load failed during step '%s'",
            summary.step,
            exc_info=True,
            extra=event_fields(
                "run_completed",
                outcome="failed",
                failed_step=summary.step,
                error=str(error),
                duration_ms=elapsed_ms(started),
                collections_imported=summary.collections_imported,
            ),
        )
        raise
    logger.info(
        "Data load completed",
        extra=event_fields(
            "run_completed",
            outcome="succeeded",
            failed_step=None,
            duration_ms=elapsed_ms(started),
            collections_imported=summary.collections_imported,
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
    logger.info("Data load started", extra=event_fields("run_started"))

    summary.step = Step.READ_COLLECTIONS
    # Read before downloading, so an invalid collection list fails in seconds
    collections = read_collections(paths.collections_filepath)

    summary.step = Step.DOWNLOAD
    download_synapse_data(release, paths.download_dir)

    summary.step = Step.IMPORT_COLLECTIONS
    for collection_name, collection_filename in collections:
        import_collection(
            db_settings,
            product,
            collection_name,
            collection_filename,
            paths.download_dir,
        )
        summary.collections_imported.append(collection_name)

    summary.step = Step.CREATE_INDEXES
    with MongoClient(db_settings.client_uri) as client:
        db = client.get_database(db_settings.name)
        create_collections_indexes(db, paths.collections_indexes_filepath)

    # The APIs clear their caches when dataversion changes, so it must only
    # change once every other collection is loaded and indexed
    summary.step = Step.IMPORT_DATA_VERSION
    import_collection(
        db_settings,
        product,
        DATA_VERSION_COLLECTION_NAME,
        DATA_VERSION_COLLECTION_FILENAME,
        paths.download_dir,
    )
    summary.collections_imported.append(DATA_VERSION_COLLECTION_NAME)
