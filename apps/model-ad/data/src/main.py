from csv import reader
from json import load
import logging
import logging.config as logging_config
from os import getenv, getcwd, makedirs, path
from pymongo import MongoClient, database
import subprocess
import synapseclient

# Get config from the environment variables
DB_USER = getenv("DB_USER")
DB_PASS = getenv("DB_PASS")
DB_NAME = getenv("DB_NAME")
DB_PORT = getenv("DB_PORT")
DB_HOST = getenv("DB_HOST")

DATA_FILE = getenv("DATA_FILE")
DATA_VERSION = getenv("DATA_VERSION")

# Configure logging
logging_config.dictConfig(
    {
        "version": 1,
        "disable_existing_loggers": False,
        "formatters": {
            "detailed": {
                "format": "%(asctime)s [%(module)s:%(lineno)d - %(levelname)s]: %(message)s"
            },
        },
        "handlers": {
            "console": {
                "level": "DEBUG",
                "class": "logging.StreamHandler",
                "formatter": "detailed",
            },
        },
        "loggers": {
            "model-ad.data": {
                "handlers": ["console"],
                "level": "DEBUG",
                "propagate": False,
            },
        },
    }
)

logger = logging.getLogger("model-ad.data")


def download_synapse_data(local_data_dir: str) -> None:
    logger.debug("Logging in to Synapse")
    syn = synapseclient.login()
    logger.debug("Logged in to Synapse successfully")

    logger.debug("Creating local data directory")
    makedirs(local_data_dir, exist_ok=True)
    logger.debug("Local data directory created successfully")

    logger.debug("Downloading manifest file from Synapse")
    manifest_entity = syn.get(
        DATA_FILE,
        version=DATA_VERSION,
        downloadLocation=local_data_dir,
        ifcollision="overwrite.local",
    )
    logger.debug("Manifest file downloaded successfully")

    logger.debug("Processing manifest and downloading referenced files")
    manifest_filepath = path.join(local_data_dir, manifest_entity.name)
    with open(manifest_filepath) as manifest_file:
        manifest_reader = reader(manifest_file)
        next(manifest_reader)  # skip header row
        for row in manifest_reader:
            syn_id = row[0]
            version = row[1]
            logger.debug("Downloading %s version %s", syn_id, version)
            syn.get(
                syn_id,
                version=version,
                downloadLocation=local_data_dir,
                ifcollision="overwrite.local",
            )
            logger.debug("Downloaded %s version %s successfully", syn_id, version)
    logger.debug("All manifest files downloaded successfully")


def import_collections_data(collections_filepath: str, local_data_dir: str) -> None:
    """Import collections into MongoDB using local definition CSV and local data"""
    logger.debug("Starting collections import process")
    with open(collections_filepath) as collections_file:
        collections_reader = reader(collections_file)
        for row in collections_reader:
            collection_name = row[0]
            collection_filename = row[1]
            collection_filepath = path.join(local_data_dir, collection_filename)

            logger.debug(
                "Importing collection '%s' from '%s'",
                collection_name,
                collection_filename,
            )

            # Use mongoimport instead of pymongo for performance with large datasets
            cmd = [
                "mongoimport",
                f"--uri=mongodb://{DB_USER}:{DB_PASS}@{DB_HOST}:{DB_PORT}/{DB_NAME}?authSource=admin",
                f"--collection={collection_name}",
                f"--file={collection_filepath}",
                "--jsonArray",
                "--drop",
            ]

            # Only maintain insertion order for ui_config collection
            if collection_name == "ui_config":
                cmd.append("--maintainInsertionOrder")
            else:
                # Use parallel workers for better performance on other collections
                cmd.append("--numInsertionWorkers=4")

            result = subprocess.run(cmd, capture_output=True, text=True)
            if result.returncode != 0:
                logger.error("mongoimport failed: %s", result.stderr)
                raise Exception(f"Failed to import {collection_name}")

            logger.debug("Successfully imported collection '%s'", collection_name)
    logger.debug("All collections imported successfully")


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
            indexes = collection_index_data["indexes"]
            collection = db.get_collection(collection_name)
            for index in indexes:
                collection.create_index(list(index.items()))
            logger.debug(
                "Created %d indexes for collection '%s' successfully",
                len(indexes),
                collection_name,
            )
    logger.debug("All indexes created successfully")


def main() -> None:
    """Main function to execute preceding functions"""
    local_data_dir = path.join(getcwd(), "local", "data")
    collections_filepath = path.join(getcwd(), "src", "data", "collections.csv")
    collections_indexes_filepath = path.join(
        getcwd(), "src", "data", "collections-indexes.json"
    )

    logger.debug("DATA_FILE: %s", DATA_FILE)
    logger.debug("DATA_VERSION: %s", DATA_VERSION)

    logger.debug("Starting Synapse data download process")
    download_synapse_data(local_data_dir)
    logger.debug("Synapse data download completed successfully")

    logger.debug("Starting MongoDB import process")
    import_collections_data(collections_filepath, local_data_dir)

    # Create indexes using pymongo
    db_uri = f"mongodb://{DB_USER}:{DB_PASS}@{DB_HOST}:{DB_PORT}"
    client = MongoClient(db_uri)
    try:
        db = client.get_database(DB_NAME)
        create_collections_indexes(db, collections_indexes_filepath)
        client.close()
    except Exception as e:
        logger.error("Error during MongoDB update: %s", e)
        raise Exception("Error", e)
    logger.debug("MongoDB import completed successfully")


if __name__ == "__main__":
    main()
