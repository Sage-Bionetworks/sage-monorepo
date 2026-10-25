import logging
from pathlib import Path
from unittest import mock

import pytest

from explorers_data_loader.logs import BYTES_PER_MEGABYTE
from explorers_data_loader.settings import ReleaseSettings
from explorers_data_loader.synapse import download_synapse_data

MODEL_AD_RELEASE = ReleaseSettings(data_file="syn73774820", data_version="221")
MANIFEST_FILENAME = "data_manifest.csv"
RELEASE_FILES = {
    "syn73774525": ("4", "ui_config.json", 2 * BYTES_PER_MEGABYTE),
    "syn73774821": ("3", "dataversion.json", BYTES_PER_MEGABYTE // 2),
}


@pytest.fixture(autouse=True)
def capture_loader_records(caplog):
    caplog.set_level(logging.DEBUG, logger="explorers_data_loader")


def fake_synapse_client(download_dir: Path):
    """A Synapse client whose downloads write placeholder files of known sizes"""
    manifest_rows = ["id,version"] + [
        f"{syn_id},{version}" for syn_id, (version, _, _) in RELEASE_FILES.items()
    ]

    def get(syn_id, **_):
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
        download_synapse_data(MODEL_AD_RELEASE, str(tmp_path))

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
    assert [(f["file_number"], f["files"]) for f in downloaded] == [(1, 2), (2, 2)]
    (completed,) = events(caplog, "download_completed")
    assert completed.fields["files"] == len(RELEASE_FILES)


def test_download_logs_in_silently_so_client_output_cannot_split_records(tmp_path):
    with mock.patch(
        "explorers_data_loader.synapse.synapseclient.Synapse",
        return_value=fake_synapse_client(tmp_path),
    ) as synapse:
        download_synapse_data(MODEL_AD_RELEASE, str(tmp_path))

    synapse.assert_called_once_with(silent=True)
    synapse.return_value.login.assert_called_once_with(silent=True)
