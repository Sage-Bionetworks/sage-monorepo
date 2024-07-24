from csv import reader
from glob import glob
from gridfs import GridFS
from json import load
from os import getenv, getcwd, makedirs, path
from pymongo import MongoClient, database
import synapseclient
import synapseutils

# Get config from the environment variables
DB_USER = getenv("DB_USER")
DB_PASS = getenv("DB_PASS")
DB_NAME = getenv("DB_NAME")
DB_PORT = getenv("DB_PORT")
DB_HOST = getenv("DB_HOST")

DATA_FILE = getenv("DATA_FILE")
DATA_VERSION = getenv("DATA_VERSION")
TEAM_IMAGES_ID = getenv("TEAM_IMAGES_ID")


def download_synapse_data(team_images_dir: str, local_data_dir: str) -> None:
    print("===> login to synapse")
    syn = synapseclient.login()

    print("===> make directory for local data")
    makedirs(team_images_dir, exist_ok=True)

    print("===> download manifest file from Synapse")
    manifest_entity = syn.get(
        DATA_FILE,
        version=DATA_VERSION,
        downloadLocation=local_data_dir,
        ifcollision="overwrite.local",
    )

    print("==> download all files referenced in manifest from Synapse")
    manifest_filepath = path.join(local_data_dir, manifest_entity.name)
    with open(manifest_filepath, "r") as manifest_file:
        manifest_reader = reader(manifest_file)
        next(manifest_reader)  # skip header row
        for row in manifest_reader:
            syn_id = row[0]
            version = row[1]
            print(f"\tdownloading {syn_id}.{version}...")
            syn.get(
                syn_id,
                version=version,
                downloadLocation=local_data_dir,
                ifcollision="overwrite.local",
            )

    print("==> download team images")
    synapseutils.syncFromSynapse(
        syn, TEAM_IMAGES_ID, team_images_dir, followLink=False, manifest="all"
    )


def create_data_version_collection(db: database.Database) -> None:
    """Create collection that contains data version info"""
    print("    creating data version collection")
    data_version_collection = db.get_collection("dataversion")
    data_version_collection.drop()
    dataversion = {
        "data_file": DATA_FILE,
        "data_version": DATA_VERSION,
        "team_images_id": TEAM_IMAGES_ID,
    }
    data_version_collection.insert_one(dataversion)


def import_collections_data(
    db: database.Database, collections_filepath: str, local_data_dir: str
) -> None:
    """Import collections into MongoDB using local definition CSV and local data"""
    print("    importing collections from Synapse data")
    with open(collections_filepath, "r") as collections_file:
        collections_reader = reader(collections_file)
        for row in collections_reader:
            collection_name = row[0]
            collection_filename = row[1]
            print(f"\t{collection_name} from {collection_filename}...")

            # read json
            collection_filepath = path.join(local_data_dir, collection_filename)
            with open(collection_filepath, "r") as collection_file:
                documents = load(collection_file)

            # import into collection
            collection = db.get_collection(collection_name)
            collection.drop()
            collection.insert_many(documents)


def create_collections_indexes(
    db: database.Database, collections_indexes_filepath: str
) -> None:
    """Create indexes for MongoDB collections based on JSON file"""
    print("    creating indexes")
    with open(collections_indexes_filepath, "r") as collection_indexes_file:
        collections_indexes_data = load(collection_indexes_file)
        for collection_index_data in collections_indexes_data:
            collection_name = collection_index_data["name"]
            print(f"\t{collection_name}...")
            indexes = collection_index_data["indexes"]
            collection = db.get_collection(collection_name)
            for index in indexes:
                collection.create_index(list(index.items()))


def import_images(db: database.Database, images_dir: str) -> None:
    """Import images into MongoDB from local directory"""
    print("    importing images")
    fs = GridFS(db)
    file_extensions = ["*.jpg", "*.jpeg", "*.png"]
    files = []
    for ext in file_extensions:
        files.extend(glob(ext, root_dir=images_dir))
    for filename in files:
        with open(path.join(images_dir, filename), "rb") as file_data:
            print(f"\tuploading {filename}...")
            fs.put(file_data, filename=filename)


def main() -> None:
    """Main function to execute preceding functions"""
    local_data_dir = path.join(getcwd(), "local", "data")
    local_team_images_dir = path.join(local_data_dir, "team_images")
    collections_filepath = path.join(getcwd(), "src", "data", "collections.csv")
    collections_indexes_filepath = path.join(
        getcwd(), "src", "data", "collections-indexes.json"
    )

    print(f"DATA_FILE: {DATA_FILE}")
    print(f"DATA_VERSION: {DATA_VERSION}")
    print(f"TEAM_IMAGES_ID: {TEAM_IMAGES_ID}")
    print("\n")

    download_synapse_data(local_team_images_dir, local_data_dir)

    print("==> update mongo db")
    db_uri = f"mongodb://{DB_USER}:{DB_PASS}@{DB_HOST}:{DB_PORT}"
    client = MongoClient(db_uri)
    try:
        db = client.get_database(DB_NAME)
        create_data_version_collection(db)
        import_collections_data(db, collections_filepath, local_data_dir)
        create_collections_indexes(db, collections_indexes_filepath)
        import_images(db, local_team_images_dir)
        client.close()
    except Exception as e:
        raise Exception("Error", e)


if __name__ == "__main__":
    main()
