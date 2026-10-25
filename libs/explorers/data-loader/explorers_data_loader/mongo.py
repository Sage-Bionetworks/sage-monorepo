import logging
from os import path
import re
import subprocess
from time import monotonic

from pymongo import database
from pymongo.collation import Collation

from explorers_data_loader.collection_config import CollectionConfig
from explorers_data_loader.logs import elapsed_ms, event_fields
from explorers_data_loader.settings import DatabaseSettings
from explorers_data_loader.staging import staging_collection_name

logger = logging.getLogger(__name__)

CASE_INSENSITIVE_COLLATION = Collation("en", strength=2)

# mongoimport ends with e.g. "1169360 document(s) imported successfully."
IMPORTED_DOCUMENTS_PATTERN = re.compile(r"(\d+) document\(s\) imported successfully")


def import_collection(
    db_settings: DatabaseSettings, collection: CollectionConfig, download_dir: str
) -> None:
    """Import a collection's file into its staging collection, leaving the live
    collection serving the previous release"""
    collection_filepath = path.join(download_dir, collection.file)
    staging_name = staging_collection_name(collection.name)

    logger.debug("Importing collection '%s' from '%s'", staging_name, collection.file)

    # Use mongoimport instead of pymongo for performance with large datasets.
    # --drop only ever clears the staging collection, never the live one.
    cmd = [
        "mongoimport",
        f"--uri={db_settings.import_uri}",
        f"--collection={staging_name}",
        f"--file={collection_filepath}",
        "--jsonArray",
        "--drop",
    ]

    if collection.maintain_insertion_order:
        cmd.append("--maintainInsertionOrder")
    else:
        # Use parallel workers for better performance on other collections
        cmd.append("--numInsertionWorkers=4")

    started = monotonic()
    result = subprocess.run(cmd, capture_output=True, text=True)
    duration_ms = elapsed_ms(started)
    if result.returncode != 0:
        logger.error(
            "mongoimport failed for collection '%s'",
            collection.name,
            extra=event_fields(
                "collection_import_failed",
                collection=collection.name,
                file=collection.file,
                duration_ms=duration_ms,
                error=result.stderr.strip(),
            ),
        )
        raise Exception(f"Failed to import {collection.name}")

    logger.info(
        "Imported collection '%s'",
        staging_name,
        extra=event_fields(
            "collection_imported",
            collection=collection.name,
            staging_collection=staging_name,
            file=collection.file,
            documents=read_imported_document_count(result.stderr),
            duration_ms=duration_ms,
        ),
    )


def read_imported_document_count(mongoimport_output: str) -> int | None:
    """Read the document count from mongoimport's summary line, if it printed one"""
    match = IMPORTED_DOCUMENTS_PATTERN.search(mongoimport_output)
    return int(match.group(1)) if match else None


def create_collections_indexes(
    db: database.Database, collections: list[CollectionConfig]
) -> None:
    """Create each collection's indexes on its staging collection.

    A rename keeps a collection's indexes, so building them before the swap
    means no collection is ever served unindexed.
    """
    logger.debug("Starting index creation process")
    for config in collections:
        if not config.indexes and not config.collated_indexes:
            continue
        staging_name = staging_collection_name(config.name)
        logger.debug("Creating indexes for collection '%s'", staging_name)
        collection = db.get_collection(staging_name)
        for index in config.indexes:
            collection.create_index(list(index.items()))
        for index in config.collated_indexes:
            collection.create_index(
                list(index.items()), collation=CASE_INSENSITIVE_COLLATION
            )
        logger.info(
            "Created indexes for collection '%s'",
            config.name,
            extra=event_fields(
                "indexes_created",
                collection=config.name,
                indexes=len(config.indexes),
                collated_indexes=len(config.collated_indexes),
            ),
        )
    logger.debug("All indexes created successfully")
