from csv import reader
from datetime import datetime
from glob import glob
from gridfs import GridFS
from json import load
from os import getenv, getcwd, makedirs, path
from pymongo import MongoClient, database
import synapseclient

# Get config from the environment variables
DB_USER = getenv("DB_USER")
DB_PASS = getenv("DB_PASS")
DB_NAME = getenv("DB_NAME")
DB_PORT = getenv("DB_PORT")
DB_HOST = getenv("DB_HOST")

DATA_FILE = getenv("DATA_FILE")
DATA_VERSION = getenv("DATA_VERSION")


def print_with_timestamp(message: str) -> None:
    """Print message with timestamp prefix"""
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"[{timestamp}] {message}")


def download_synapse_data(local_data_dir: str) -> None:
    print_with_timestamp("INFO - Logging in to Synapse")
    syn = synapseclient.login()
    print_with_timestamp("INFO - Logged in to Synapse successfully")

    print_with_timestamp("INFO - Creating local data directory")
    makedirs(local_data_dir, exist_ok=True)
    print_with_timestamp("INFO - Local data directory created successfully")

    print_with_timestamp("INFO - Downloading manifest file from Synapse")
    manifest_entity = syn.get(
        DATA_FILE,
        version=DATA_VERSION,
        downloadLocation=local_data_dir,
        ifcollision="overwrite.local",
    )
    print_with_timestamp("INFO - Manifest file downloaded successfully")

    print_with_timestamp("INFO - Processing manifest and downloading referenced files")
    manifest_filepath = path.join(local_data_dir, manifest_entity.name)
    with open(manifest_filepath, "r") as manifest_file:
        manifest_reader = reader(manifest_file)
        next(manifest_reader)  # skip header row
        for row in manifest_reader:
            syn_id = row[0]
            version = row[1]
            print_with_timestamp(f"INFO - Downloading {syn_id} version {version}")
            syn.get(
                syn_id,
                version=version,
                downloadLocation=local_data_dir,
                ifcollision="overwrite.local",
            )
            print_with_timestamp(
                f"INFO - Downloaded {syn_id} version {version} successfully"
            )
    print_with_timestamp("INFO - All manifest files downloaded successfully")


def create_data_version_collection(db: database.Database) -> None:
    """Create collection that contains data version info"""
    print_with_timestamp("INFO - Creating data version collection")
    data_version_collection = db.get_collection("dataversion")
    data_version_collection.drop()
    dataversion = {
        "data_file": DATA_FILE,
        "data_version": DATA_VERSION,
        "team_images_id": "NOT_SET",
    }
    data_version_collection.insert_one(dataversion)
    print_with_timestamp("INFO - Data version collection created successfully")


def import_collections_data(
    db: database.Database, collections_filepath: str, local_data_dir: str
) -> None:
    """Import collections into MongoDB using local definition CSV and local data"""
    print_with_timestamp("INFO - Starting collections import process")
    with open(collections_filepath, "r") as collections_file:
        collections_reader = reader(collections_file)
        for row in collections_reader:
            collection_name = row[0]
            collection_filename = row[1]
            print_with_timestamp(
                f"INFO - Importing collection '{collection_name}' from '{collection_filename}'"
            )

            # read json
            collection_filepath = path.join(local_data_dir, collection_filename)
            with open(collection_filepath, "r") as collection_file:
                documents = load(collection_file)

            # import into collection
            collection = db.get_collection(collection_name)
            collection.drop()
            collection.insert_many(documents)
            print_with_timestamp(
                f"INFO - Successfully imported {len(documents)} documents to collection '{collection_name}'"
            )
    print_with_timestamp("INFO - All collections imported successfully")


def create_collections_indexes(
    db: database.Database, collections_indexes_filepath: str
) -> None:
    """Create indexes for MongoDB collections based on JSON file"""
    print_with_timestamp("INFO - Starting index creation process")
    with open(collections_indexes_filepath, "r") as collection_indexes_file:
        collections_indexes_data = load(collection_indexes_file)
        for collection_index_data in collections_indexes_data:
            collection_name = collection_index_data["name"]
            print_with_timestamp(
                f"INFO - Creating indexes for collection '{collection_name}'"
            )
            indexes = collection_index_data["indexes"]
            collection = db.get_collection(collection_name)
            for index in indexes:
                collection.create_index(list(index.items()))
            print_with_timestamp(
                f"INFO - Created {len(indexes)} indexes for collection '{collection_name}' successfully"
            )
    print_with_timestamp("INFO - All indexes created successfully")


def main() -> None:
    """Main function to execute preceding functions"""
    local_data_dir = path.join(getcwd(), "local", "data")
    collections_filepath = path.join(getcwd(), "src", "data", "collections.csv")
    collections_indexes_filepath = path.join(
        getcwd(), "src", "data", "collections-indexes.json"
    )

    print_with_timestamp(f"CONFIG - DATA_FILE: {DATA_FILE}")
    print_with_timestamp(f"CONFIG - DATA_VERSION: {DATA_VERSION}")

    print_with_timestamp("INFO - Starting Synapse data download process")
    download_synapse_data(local_data_dir)
    print_with_timestamp("INFO - Synapse data download completed successfully")

    print_with_timestamp("INFO - Starting MongoDB update process")
    db_uri = f"mongodb://{DB_USER}:{DB_PASS}@{DB_HOST}:{DB_PORT}"
    client = MongoClient(db_uri)
    try:
        db = client.get_database(DB_NAME)
        create_data_version_collection(db)
        import_collections_data(db, collections_filepath, local_data_dir)
        create_collections_indexes(db, collections_indexes_filepath)
        client.close()
    except Exception as e:
        raise Exception("Error", e)
    print_with_timestamp("INFO - MongoDB update completed successfully")


if __name__ == "__main__":
    main()
