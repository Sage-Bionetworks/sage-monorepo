from csv import reader
from json import load
import logging
from os import path
import re
import subprocess
from time import monotonic

from pymongo import database
from pymongo.collation import Collation

from explorers_data_loader.logs import elapsed_ms, event_fields
from explorers_data_loader.settings import DatabaseSettings, ProductSettings

logger = logging.getLogger(__name__)

DATA_VERSION_COLLECTION_NAME = "dataversion"
DATA_VERSION_COLLECTION_FILENAME = "dataversion.json"

CASE_INSENSITIVE_COLLATION = Collation("en", strength=2)

# mongoimport ends with e.g. "1169360 document(s) imported successfully."
IMPORTED_DOCUMENTS_PATTERN = re.compile(r"(\d+) document\(s\) imported successfully")


def read_collections(collections_filepath: str) -> list[tuple[str, str]]:
    """Read the (collection name, file name) pairs a release imports"""
    with open(collections_filepath) as collections_file:
        collections = [(name, filename) for name, filename in reader(collections_file)]

    if any(name == DATA_VERSION_COLLECTION_NAME for name, _ in collections):
        raise ValueError(
            f"'{DATA_VERSION_COLLECTION_NAME}' must not be listed in "
            f"{collections_filepath}: it is imported last, once every other "
            "collection is loaded and indexed"
        )
    return collections


def import_collection(
    db_settings: DatabaseSettings,
    product: ProductSettings,
    collection_name: str,
    collection_filename: str,
    download_dir: str,
) -> None:
    """Import a single collection into MongoDB from local data"""
    collection_filepath = path.join(download_dir, collection_filename)

    logger.debug(
        "Importing collection '%s' from '%s'",
        collection_name,
        collection_filename,
    )

    # Use mongoimport instead of pymongo for performance with large datasets
    cmd = [
        "mongoimport",
        f"--uri={db_settings.import_uri}",
        f"--collection={collection_name}",
        f"--file={collection_filepath}",
        "--jsonArray",
        "--drop",
    ]

    if collection_name == product.insertion_ordered_collection_name:
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
            collection_name,
            extra=event_fields(
                "collection_import_failed",
                collection=collection_name,
                file=collection_filename,
                duration_ms=duration_ms,
                error=result.stderr.strip(),
            ),
        )
        raise Exception(f"Failed to import {collection_name}")

    logger.info(
        "Imported collection '%s'",
        collection_name,
        extra=event_fields(
            "collection_imported",
            collection=collection_name,
            file=collection_filename,
            documents=read_imported_document_count(result.stderr),
            duration_ms=duration_ms,
        ),
    )


def read_imported_document_count(mongoimport_output: str) -> int | None:
    """Read the document count from mongoimport's summary line, if it printed one"""
    match = IMPORTED_DOCUMENTS_PATTERN.search(mongoimport_output)
    return int(match.group(1)) if match else None


def create_collections_indexes(
    db: database.Database, collections_indexes_filepath: str
) -> None:
    """Create indexes for MongoDB collections based on JSON file"""
    logger.debug("Starting index creation process")
    with open(collections_indexes_filepath) as collection_indexes_file:
        collections_indexes_data = load(collection_indexes_file)
    for collection_index_data in collections_indexes_data:
        collection_name = collection_index_data["name"]
        logger.debug("Creating indexes for collection '%s'", collection_name)
        collection = db.get_collection(collection_name)
        indexes = collection_index_data.get("indexes", [])
        collated_indexes = collection_index_data.get("collatedIndexes", [])
        for index in indexes:
            collection.create_index(list(index.items()))
        for index in collated_indexes:
            collection.create_index(
                list(index.items()), collation=CASE_INSENSITIVE_COLLATION
            )
        logger.info(
            "Created indexes for collection '%s'",
            collection_name,
            extra=event_fields(
                "indexes_created",
                collection=collection_name,
                indexes=len(indexes),
                collated_indexes=len(collated_indexes),
            ),
        )
    logger.debug("All indexes created successfully")
