import synapseclient
from os import getenv, getcwd, makedirs
from typing import Optional

# Get config from the environment variables
DB_USER = getenv("DB_USER")
DB_PASS = getenv("DB_PASS")
DB_NAME = getenv("DB_NAME")
DB_PORT = getenv("DB_PORT")

DATA_FILE = getenv("DATA_FILE")
DATA_VERSION = getenv("DATA_VERSION")
TEAM_IMAGES_ID = getenv("TEAM_IMAGES_ID")

print(f"DATA_FILE: {DATA_FILE}")
print(f"DATA_VERSION: {DATA_VERSION}")
print(f"TEAM_IMAGES_ID: {TEAM_IMAGES_ID}")


def main() -> None:
    """Main function to execute preceding functions"""

    # login synapse
    syn = synapseclient.login()

    # create directory for data and team images
    LOCAL_DATA_DIR = f"{getcwd()}/local/data"
    TEAM_IMAGES_DIR = f"{LOCAL_DATA_DIR}/team_images"
    makedirs(TEAM_IMAGES_DIR, exist_ok=True)

    # download manifest file from synapse
    syn.get(
        DATA_FILE,
        version=DATA_VERSION,
        downloadLocation=LOCAL_DATA_DIR,
        ifcollision="overwrite.local",
    )

    # download all files references in manifest from synapse
    # TODO

    # download team images
    # TODO

    # import data into mongodb with import-data.sh
    # TODO: install mongoDB tools (mongosh, mongoimport...) in dockerfile
    # TODO: copy files from Agora ("collections.csv", "mongo-create-Indexes.js") and set env variables
    # TODO: remove "serve" target or install mongoDB tools in prepare


if __name__ == "__main__":
    main()
