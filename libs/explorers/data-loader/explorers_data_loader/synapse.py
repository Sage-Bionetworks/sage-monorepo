from csv import reader
import logging
from os import makedirs, path
import shutil
from time import monotonic

import synapseclient
import synapseutils

from explorers_data_loader.logs import elapsed_ms, event_fields, total_size_mb
from explorers_data_loader.settings import ReleaseSettings

logger = logging.getLogger(__name__)


TEAM_IMAGE_EXTENSIONS = (".jpg", ".jpeg", ".png")


def login_to_synapse() -> synapseclient.Synapse:
    logger.debug("Logging in to Synapse")
    # silent turns off the client's progress bars and messages, which would
    # otherwise interleave with the loader's structured records
    syn = synapseclient.Synapse(silent=True)
    syn.login(silent=True)
    logger.debug("Logged in to Synapse successfully")
    return syn


def download_synapse_data(
    syn: synapseclient.Synapse, release: ReleaseSettings, download_dir: str
) -> None:
    """Download the release manifest and every file it lists"""
    started = monotonic()
    logger.info(
        "Downloading the release from Synapse", extra=event_fields("download_started")
    )

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
        manifest_rows = list(manifest_reader)

    downloaded_files = skipped_entries = 0
    for entry_number, (syn_id, version, *_) in enumerate(manifest_rows, start=1):
        logger.debug("Downloading %s version %s", syn_id, version)
        file_started = monotonic()
        entity = syn.get(
            syn_id,
            version=version,
            downloadLocation=download_dir,
            ifcollision="overwrite.local",
        )
        # A manifest can list entities that aren't files, such as agora's folder
        # of data validation reports. Getting one downloads nothing to import.
        entity_path = getattr(entity, "path", None)
        if not entity_path:
            skipped_entries += 1
            # e.g. "org.sagebionetworks.repo.model.Folder" -> "Folder"
            entity_type = entity.concreteType.rsplit(".", 1)[-1]
            logger.info(
                "Skipped manifest entry %d of %d: '%s' is a %s, not a file",
                entry_number,
                len(manifest_rows),
                entity.name,
                entity_type.lower(),
                extra=event_fields(
                    "manifest_entry_skipped",
                    synapse_id=syn_id,
                    version=version,
                    name=entity.name,
                    entity_type=entity_type,
                ),
            )
            continue
        downloaded_files += 1
        logger.info(
            "Downloaded file %d of %d: '%s'",
            entry_number,
            len(manifest_rows),
            entity.name,
            extra=event_fields(
                "file_downloaded",
                synapse_id=syn_id,
                version=version,
                file=entity.name,
                entry_number=entry_number,
                entries=len(manifest_rows),
                size_mb=total_size_mb([entity_path]),
                duration_ms=elapsed_ms(file_started),
            ),
        )
    logger.info(
        "Downloaded the release from Synapse",
        extra=event_fields(
            "download_completed",
            files=downloaded_files,
            skipped_entries=skipped_entries,
            duration_ms=elapsed_ms(started),
        ),
    )


def download_team_images(
    syn: synapseclient.Synapse, folder_id: str, team_images_dir: str
) -> list[str]:
    """Download the team images folder, returning the paths of its images.

    The folder is emptied first, so an image removed from Synapse isn't
    uploaded again from an earlier run's download.
    """
    started = monotonic()
    shutil.rmtree(team_images_dir, ignore_errors=True)
    makedirs(team_images_dir)
    entities = synapseutils.syncFromSynapse(
        syn, folder_id, path=team_images_dir, manifest="suppress"
    )
    image_paths = sorted(
        entity.path
        for entity in entities
        if entity.path.lower().endswith(TEAM_IMAGE_EXTENSIONS)
    )
    if not image_paths:
        raise ValueError(f"No team images found in Synapse folder {folder_id}")
    logger.info(
        "Downloaded %d team images from Synapse",
        len(image_paths),
        extra=event_fields(
            "team_images_downloaded",
            synapse_id=folder_id,
            images=len(image_paths),
            size_mb=total_size_mb(image_paths),
            duration_ms=elapsed_ms(started),
        ),
    )
    return image_paths
