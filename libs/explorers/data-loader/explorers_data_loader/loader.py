import logging
import logging.config as logging_config
from os import getcwd

from pymongo import MongoClient

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

LOGGER_NAME = "explorers_data_loader"

logger = logging.getLogger(__name__)


def configure_logging() -> None:
    logging_config.dictConfig(
        {
            "version": 1,
            "disable_existing_loggers": False,
            "formatters": {
                "detailed": {
                    "format": (
                        "%(asctime)s [%(module)s:%(lineno)d - %(levelname)s]: "
                        "%(message)s"
                    )
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
                LOGGER_NAME: {
                    "handlers": ["console"],
                    "level": "DEBUG",
                    "propagate": False,
                },
            },
        }
    )


def run(product: ProductSettings, working_dir: str | None = None) -> None:
    """Download a data release from Synapse and load it into MongoDB"""
    configure_logging()
    paths = LoaderPaths.from_working_dir(working_dir or getcwd())
    db_settings = DatabaseSettings.from_env()
    release = ReleaseSettings.from_env()

    logger.debug("DATA_FILE: %s", release.data_file)
    logger.debug("DATA_VERSION: %s", release.data_version)

    # Read before downloading, so an invalid collection list fails in seconds
    collections = read_collections(paths.collections_filepath)

    logger.debug("Starting Synapse data download process")
    download_synapse_data(release, paths.download_dir)
    logger.debug("Synapse data download completed successfully")

    logger.debug("Starting MongoDB import process")
    for collection_name, collection_filename in collections:
        import_collection(
            db_settings,
            product,
            collection_name,
            collection_filename,
            paths.download_dir,
        )
    logger.debug("All collections imported successfully")

    with MongoClient(db_settings.client_uri) as client:
        db = client.get_database(db_settings.name)
        create_collections_indexes(db, paths.collections_indexes_filepath)

    # The APIs clear their caches when dataversion changes, so it must only
    # change once every other collection is loaded and indexed
    import_collection(
        db_settings,
        product,
        DATA_VERSION_COLLECTION_NAME,
        DATA_VERSION_COLLECTION_FILENAME,
        paths.download_dir,
    )
    logger.debug("MongoDB import completed successfully")
