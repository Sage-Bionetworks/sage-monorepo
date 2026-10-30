"""Load team member photos into GridFS beside the live photos, ready to swap in.

The agora API serves the photos from GridFS's default bucket, "fs", whose
documents live in the fs.files and fs.chunks collections. Each photo is looked
up by its bare file name, e.g. "jane_doe.jpg".
"""

import logging
from os import path
from time import monotonic

from gridfs import GridFSBucket
from pymongo import database

from explorers_data_loader.logs import elapsed_ms, event_fields, total_size_mb
from explorers_data_loader.staging import STAGING_SUFFIX, staging_collection_name

logger = logging.getLogger(__name__)

LIVE_BUCKET_NAME = "fs"
STAGING_BUCKET_NAME = f"{LIVE_BUCKET_NAME}{STAGING_SUFFIX}"
GRIDFS_COLLECTION_SUFFIXES = (".files", ".chunks")

# The collections the photos occupy, verified and swapped like a release's own
TEAM_IMAGES_COLLECTION_NAMES = tuple(
    f"{LIVE_BUCKET_NAME}{suffix}" for suffix in GRIDFS_COLLECTION_SUFFIXES
)


def upload_team_images_to_staging(
    db: database.Database, image_paths: list[str]
) -> None:
    """Upload the photos into staging collections for the swap.

    GridFS names a bucket's collections <bucket>.files and <bucket>.chunks, so
    the photos go to a staging bucket, whose collections are then renamed to
    fs.files_staging and fs.chunks_staging. Swapping those in replaces the
    previous release's photos rather than adding to them.
    """
    started = monotonic()
    for suffix in GRIDFS_COLLECTION_SUFFIXES:
        db.drop_collection(f"{STAGING_BUCKET_NAME}{suffix}")

    bucket = GridFSBucket(db, bucket_name=STAGING_BUCKET_NAME)
    for image_path in image_paths:
        with open(image_path, "rb") as image_file:
            bucket.upload_from_stream(path.basename(image_path), image_file)

    for suffix in GRIDFS_COLLECTION_SUFFIXES:
        db.get_collection(f"{STAGING_BUCKET_NAME}{suffix}").rename(
            staging_collection_name(f"{LIVE_BUCKET_NAME}{suffix}"), dropTarget=True
        )

    logger.info(
        "Uploaded %d team images to GridFS",
        len(image_paths),
        extra=event_fields(
            "team_images_uploaded",
            images=len(image_paths),
            size_mb=total_size_mb(image_paths),
            duration_ms=elapsed_ms(started),
        ),
    )
