import logging
from pathlib import Path
from unittest import mock

import pytest

from explorers_data_loader.logs import BYTES_PER_MEGABYTE
from explorers_data_loader.settings import ReleaseSettings
from explorers_data_loader.synapse import (
    download_synapse_data,
    download_team_images,
    login_to_synapse,
)

MODEL_AD_RELEASE = ReleaseSettings(data_file="syn73774820", data_version="221")
MANIFEST_FILENAME = "data_manifest.csv"
RELEASE_FILES = {
    "syn73774525": ("4", "ui_config.json", 2 * BYTES_PER_MEGABYTE),
    "syn73774821": ("3", "dataversion.json", BYTES_PER_MEGABYTE // 2),
}


@pytest.fixture(autouse=True)
def capture_loader_records(caplog):
    caplog.set_level(logging.DEBUG, logger="explorers_data_loader")


def fake_synapse_client(download_dir: Path, folders=None):
    """A Synapse client whose downloads write placeholder files of known sizes.

    folders maps Synapse IDs to (version, name) for manifest entries that are
    folders, which Synapse returns without downloading anything.
    """
    folders = folders or {}
    manifest_entries = [
        *[(syn_id, version) for syn_id, (version, _) in folders.items()],
        *[(syn_id, version) for syn_id, (version, _, _) in RELEASE_FILES.items()],
    ]
    manifest_rows = ["id,version"] + [
        f"{syn_id},{version}" for syn_id, version in manifest_entries
    ]

    def get(syn_id, **_):
        if syn_id in folders:
            folder = mock.Mock(spec=["name", "concreteType"])
            folder.name = folders[syn_id][1]
            folder.concreteType = "org.sagebionetworks.repo.model.Folder"
            return folder
        if syn_id == MODEL_AD_RELEASE.data_file:
            filename, contents = MANIFEST_FILENAME, "\n".join(manifest_rows) + "\n"
        else:
            _, filename, size = RELEASE_FILES[syn_id]
            contents = "x" * size
        file_path = download_dir / filename
        file_path.write_text(contents)
        entity = mock.Mock(path=str(file_path))
        # Mock(name=...) names the mock itself, so the attribute is set after
        entity.name = filename
        return entity

    return mock.Mock(get=mock.Mock(side_effect=get))


def events(caplog, event):
    return [r for r in caplog.records if getattr(r, "event", None) == event]


def test_download_logs_its_start_each_file_and_its_completion(tmp_path, caplog):
    with mock.patch(
        "explorers_data_loader.synapse.synapseclient.Synapse",
        return_value=fake_synapse_client(tmp_path),
    ):
        download_synapse_data(login_to_synapse(), MODEL_AD_RELEASE, str(tmp_path))

    assert len(events(caplog, "download_started")) == 1
    downloaded = [record.fields for record in events(caplog, "file_downloaded")]
    assert [
        {name: fields[name] for name in ("synapse_id", "version", "file", "size_mb")}
        for fields in downloaded
    ] == [
        {
            "synapse_id": "syn73774525",
            "version": "4",
            "file": "ui_config.json",
            "size_mb": 2.0,
        },
        {
            "synapse_id": "syn73774821",
            "version": "3",
            "file": "dataversion.json",
            "size_mb": 0.5,
        },
    ]
    assert [(f["entry_number"], f["entries"]) for f in downloaded] == [(1, 2), (2, 2)]
    (completed,) = events(caplog, "download_completed")
    assert completed.fields["files"] == len(RELEASE_FILES)
    assert completed.fields["skipped_entries"] == 0


def test_download_skips_manifest_entries_that_are_not_files(tmp_path, caplog):
    reports_folder_id = "syn52948668"
    syn = fake_synapse_client(
        tmp_path, folders={reports_folder_id: ("1", "Great Expectations Reports")}
    )

    with mock.patch(
        "explorers_data_loader.synapse.synapseclient.Synapse", return_value=syn
    ):
        download_synapse_data(login_to_synapse(), MODEL_AD_RELEASE, str(tmp_path))

    (skipped,) = events(caplog, "manifest_entry_skipped")
    assert skipped.getMessage() == (
        "Skipped manifest entry 1 of 3: 'Great Expectations Reports' is a folder, "
        "not a file"
    )
    assert skipped.fields == {
        "synapse_id": reports_folder_id,
        "version": "1",
        "name": "Great Expectations Reports",
        "entity_type": "Folder",
    }
    (completed,) = events(caplog, "download_completed")
    assert completed.fields["files"] == 2
    assert completed.fields["skipped_entries"] == 1


def test_download_logs_in_silently_so_client_output_cannot_split_records(tmp_path):
    with mock.patch(
        "explorers_data_loader.synapse.synapseclient.Synapse",
        return_value=fake_synapse_client(tmp_path),
    ) as synapse:
        download_synapse_data(login_to_synapse(), MODEL_AD_RELEASE, str(tmp_path))

    synapse.assert_called_once_with(silent=True)
    synapse.return_value.login.assert_called_once_with(silent=True)


TEAM_IMAGES_FOLDER_ID = "syn12861877"


def synced_entities(team_images_dir: Path, filenames):
    """Fake syncFromSynapse results whose files exist on disk"""
    entities = []
    for filename in filenames:
        file_path = team_images_dir / filename
        file_path.write_bytes(b"\x89PNG" * 256)
        entity = mock.Mock(path=str(file_path))
        entity.name = filename
        entities.append(entity)
    return entities


def test_download_team_images_returns_only_the_images(tmp_path, caplog):
    team_images_dir = tmp_path / "team_images"

    def sync(_syn, folder_id, path, manifest):
        assert (folder_id, manifest) == (TEAM_IMAGES_FOLDER_ID, "suppress")
        return synced_entities(
            Path(path), ["john_roe.png", "jane_doe.jpg", "notes.txt", "kim_lee.JPEG"]
        )

    with mock.patch(
        "explorers_data_loader.synapse.synapseutils.syncFromSynapse", side_effect=sync
    ):
        image_paths = download_team_images(
            mock.Mock(), TEAM_IMAGES_FOLDER_ID, str(team_images_dir)
        )

    assert [Path(image).name for image in image_paths] == [
        "jane_doe.jpg",
        "john_roe.png",
        "kim_lee.JPEG",
    ]
    (record,) = events(caplog, "team_images_downloaded")
    assert record.fields["images"] == 3
    assert record.fields["synapse_id"] == TEAM_IMAGES_FOLDER_ID


def test_download_team_images_clears_images_from_an_earlier_download(tmp_path):
    team_images_dir = tmp_path / "team_images"
    team_images_dir.mkdir()
    removed_from_synapse = team_images_dir / "former_member.jpg"
    removed_from_synapse.write_bytes(b"\xff\xd8")

    with mock.patch(
        "explorers_data_loader.synapse.synapseutils.syncFromSynapse",
        side_effect=lambda _syn, _id, path, manifest: synced_entities(
            Path(path), ["jane_doe.jpg"]
        ),
    ):
        download_team_images(mock.Mock(), TEAM_IMAGES_FOLDER_ID, str(team_images_dir))

    assert not removed_from_synapse.exists()


def test_download_team_images_fails_when_the_folder_has_no_images(tmp_path):
    with (
        mock.patch(
            "explorers_data_loader.synapse.synapseutils.syncFromSynapse",
            side_effect=lambda _syn, _id, path, manifest: synced_entities(
                Path(path), ["notes.txt"]
            ),
        ),
        pytest.raises(ValueError, match="No team images found"),
    ):
        download_team_images(
            mock.Mock(), TEAM_IMAGES_FOLDER_ID, str(tmp_path / "team_images")
        )
