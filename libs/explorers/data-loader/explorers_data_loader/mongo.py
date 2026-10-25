from csv import reader
from json import load
import logging
from os import path
import subprocess

from pymongo import database
from pymongo.collation import Collation

from explorers_data_loader.settings import DatabaseSettings, ProductSettings

logger = logging.getLogger(__name__)

DATA_VERSION_COLLECTION_NAME = "dataversion"
DATA_VERSION_COLLECTION_FILENAME = "dataversion.json"

CASE_INSENSITIVE_COLLATION = Collation("en", strength=2)


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

    result = subprocess.run(cmd, capture_output=True, text=True)
    if result.returncode != 0:
        logger.error("mongoimport failed: %s", result.stderr)
        raise Exception(f"Failed to import {collection_name}")

    logger.debug("Successfully imported collection '%s'", collection_name)


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
        for index in collection_index_data.get("indexes", []):
            collection.create_index(list(index.items()))
        for index in collection_index_data.get("collatedIndexes", []):
            collection.create_index(
                list(index.items()), collation=CASE_INSENSITIVE_COLLATION
            )
        logger.debug(
            "Created indexes for collection '%s' successfully",
            collection_name,
        )
    logger.debug("All indexes created successfully")
