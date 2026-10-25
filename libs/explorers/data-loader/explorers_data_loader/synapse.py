from csv import reader
import logging
from os import makedirs, path
from time import monotonic

import synapseclient

from explorers_data_loader.logs import elapsed_ms, event_fields
from explorers_data_loader.settings import ReleaseSettings

logger = logging.getLogger(__name__)


def download_synapse_data(release: ReleaseSettings, download_dir: str) -> None:
    """Download the release manifest and every file it lists"""
    started = monotonic()
    logger.debug("Logging in to Synapse")
    # silent turns off the client's progress bars and messages, which would
    # otherwise interleave with the loader's structured records
    syn = synapseclient.Synapse(silent=True)
    syn.login(silent=True)
    logger.debug("Logged in to Synapse successfully")

    logger.debug("Creating local data directory")
    makedirs(download_dir, exist_ok=True)
    logger.debug("Local data directory created successfully")

    logger.debug("Downloading manifest file from Synapse")
    manifest_entity = syn.get(
        release.data_file,
        version=release.data_version,
        downloadLocation=download_dir,
        ifcollision="overwrite.local",
    )
    logger.debug("Manifest file downloaded successfully")

    logger.debug("Processing manifest and downloading referenced files")
    manifest_filepath = path.join(download_dir, manifest_entity.name)
    with open(manifest_filepath) as manifest_file:
        manifest_reader = reader(manifest_file)
        next(manifest_reader)  # skip header row
        downloaded_files = 0
        for row in manifest_reader:
            syn_id = row[0]
            version = row[1]
            logger.debug("Downloading %s version %s", syn_id, version)
            syn.get(
                syn_id,
                version=version,
                downloadLocation=download_dir,
                ifcollision="overwrite.local",
            )
            logger.debug("Downloaded %s version %s successfully", syn_id, version)
            downloaded_files += 1
    logger.info(
        "Downloaded the release from Synapse",
        extra=event_fields(
            "download_completed",
            files=downloaded_files,
            duration_ms=elapsed_ms(started),
        ),
    )
