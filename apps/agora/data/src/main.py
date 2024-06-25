from csv import reader
from json import load
from os import getenv, getcwd, makedirs, path
from pymongo import MongoClient
from typing import Optional
import synapseclient
import synapseutils

# Get config from the environment variables
DB_USER = getenv("DB_USER")
DB_PASS = getenv("DB_PASS")
DB_NAME = getenv("DB_NAME")
DB_PORT = getenv("DB_PORT")
DB_HOST = getenv("DB_HOST")
DB_URI = f"mongodb://{DB_USER}:{DB_PASS}@{DB_HOST}:{DB_PORT}"

DATA_FILE = getenv("DATA_FILE")
DATA_VERSION = getenv("DATA_VERSION")
TEAM_IMAGES_ID = getenv("TEAM_IMAGES_ID")

print(f"DATA_FILE: {DATA_FILE}")
print(f"DATA_VERSION: {DATA_VERSION}")
print(f"TEAM_IMAGES_ID: {TEAM_IMAGES_ID}")


def main() -> None:
    """Main function to execute preceding functions"""
    DATA_DIR = f"{getcwd()}/local/data"
    TEAM_IMAGES_DIR = f"{DATA_DIR}/team_images"
    COLLECTIONS_FILEPATH = f"{getcwd()}/src/data/collections.csv"

    print("===> login to synapse")
    syn = synapseclient.login()

    print("===> make directory for team images")
    makedirs(TEAM_IMAGES_DIR, exist_ok=True)

    print("===> download manifest file from Synapse")
    manifest_entity = syn.get(
        DATA_FILE,
        version=DATA_VERSION,
        downloadLocation=DATA_DIR,
        ifcollision="overwrite.local",
    )

    print("==> download all files referenced in manifest from Synapse")
    MANIFEST_FILEPATH = path.join(DATA_DIR, manifest_entity.name)
    with open(MANIFEST_FILEPATH, "r") as manifest_file:
        manifest_reader = reader(manifest_file)
        next(manifest_reader)  # skip header row
        for row in manifest_reader:
            syn_id = row[0]
            version = row[1]
            print(f"===> downloading {syn_id}.{version}")
            syn.get(
                syn_id,
                version=version,
                downloadLocation=DATA_DIR,
                ifcollision="overwrite.local",
            )

    print("==> download team images")
    synapseutils.syncFromSynapse(
        syn, TEAM_IMAGES_ID, TEAM_IMAGES_DIR, followLink=False, manifest="all"
    )

    print("==> update mongo db")
    client = MongoClient(DB_URI)
    try:
        db = client.get_database(DB_NAME)

        print("==> create data version collection")
        data_version_collection = db.get_collection("dataversion")
        data_version_collection.drop()
        dataversion = {
            "data-file": DATA_FILE,
            "data-version": DATA_VERSION,
            "team-images-id": TEAM_IMAGES_ID,
        }
        data_version_collection.insert_one(dataversion)

        print("==> import collections")
        with open(COLLECTIONS_FILEPATH, "r") as collections_file:
            collections_reader = reader(collections_file)
            for row in collections_reader:
                collection_name = row[0]
                collection_filename = row[1]
                print(f"===> import {collection_name} from {collection_filename}")

                # read json
                collection_filepath = path.join(DATA_DIR, collection_filename)
                with open(collection_filepath, "r") as collection_file:
                    documents = load(collection_file)

                # import into collection
                collection = db.get_collection(collection_name)
                collection.drop()
                collection.insert_many(documents)

        # TODO: create indexes - convert scripts/mongo-create-Indexes.js to use pymongo
        # TODO: import images - convert last section of scripts/import-data.sh to use pymongo

        client.close()
    except Exception as e:
        raise Exception("Error", e)


if __name__ == "__main__":
    main()
