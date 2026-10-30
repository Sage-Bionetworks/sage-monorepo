import logging
from unittest import mock

import pytest

from explorers_data_loader.staging import staging_collection_name
from explorers_data_loader.team_images import (
    STAGING_BUCKET_NAME,
    TEAM_IMAGES_COLLECTION_NAMES,
    upload_team_images_to_staging,
)

STAGING_BUCKET_COLLECTIONS = [
    f"{STAGING_BUCKET_NAME}.files",
    f"{STAGING_BUCKET_NAME}.chunks",
]


@pytest.fixture
def team_image_paths(tmp_path):
    paths = []
    for filename in ["jane_doe.jpg", "john_roe.png"]:
        image_path = tmp_path / filename
        image_path.write_bytes(b"\xff\xd8" * 512)
        paths.append(str(image_path))
    return paths


def test_upload_stores_each_image_under_its_bare_file_name(team_image_paths):
    db = mock.Mock()
    with mock.patch("explorers_data_loader.team_images.GridFSBucket") as bucket_class:
        upload_team_images_to_staging(db, team_image_paths)

    bucket_class.assert_called_once_with(db, bucket_name=STAGING_BUCKET_NAME)
    uploaded_names = [
        call.args[0]
        for call in bucket_class.return_value.upload_from_stream.call_args_list
    ]
    assert uploaded_names == ["jane_doe.jpg", "john_roe.png"]


def test_upload_clears_and_renames_the_staging_bucket_for_the_swap(
    team_image_paths, caplog
):
    caplog.set_level(logging.INFO, logger="explorers_data_loader")
    bucket_collections = {name: mock.Mock() for name in STAGING_BUCKET_COLLECTIONS}
    db = mock.Mock(get_collection=bucket_collections.__getitem__)

    with mock.patch("explorers_data_loader.team_images.GridFSBucket"):
        upload_team_images_to_staging(db, team_image_paths)

    assert [call.args[0] for call in db.drop_collection.call_args_list] == (
        STAGING_BUCKET_COLLECTIONS
    )
    for bucket_collection, live_name in zip(
        STAGING_BUCKET_COLLECTIONS, TEAM_IMAGES_COLLECTION_NAMES, strict=True
    ):
        bucket_collections[bucket_collection].rename.assert_called_once_with(
            staging_collection_name(live_name), dropTarget=True
        )
    (record,) = [r for r in caplog.records if r.event == "team_images_uploaded"]
    assert record.fields["images"] == len(team_image_paths)
