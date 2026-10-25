import json

import pytest

from explorers_data_loader.releases import (
    DATA_RELEASE_ENV,
    ReleaseConfigError,
    ReleaseSettings,
    read_release,
    retired_release_env_names,
)

MODEL_AD_RELEASES = {
    "$comment": "TODO(EX-146): deployed entries mirror the data managers",
    "local": {"dataFile": "syn73774820", "dataVersion": "221"},
    "staging": {"dataFile": "syn73774820", "dataVersion": "191"},
    "prod": {"dataFile": "syn74702351", "dataVersion": "9"},
}
RETIRED_ENV_NAMES = ("DATA_FILE", "DATA_VERSION", "TEAM_IMAGES_ID")


@pytest.fixture(autouse=True)
def clear_release_env(monkeypatch):
    for name in (DATA_RELEASE_ENV, *RETIRED_ENV_NAMES):
        monkeypatch.delenv(name, raising=False)


def write_releases(tmp_path, releases=MODEL_AD_RELEASES):
    releases_filepath = tmp_path / "data-releases.json"
    releases_filepath.write_text(json.dumps(releases))
    return str(releases_filepath)


def test_read_release_uses_the_local_entry_by_default(tmp_path):
    assert read_release(write_releases(tmp_path)) == ReleaseSettings(
        name="local", data_file="syn73774820", data_version="221"
    )


def test_read_release_uses_the_entry_data_release_names(tmp_path, monkeypatch):
    monkeypatch.setenv(DATA_RELEASE_ENV, "prod")

    release = read_release(write_releases(tmp_path))

    assert (release.name, release.data_file, release.data_version) == (
        "prod",
        "syn74702351",
        "9",
    )


def test_read_release_lists_the_releases_when_data_release_names_none(
    tmp_path, monkeypatch
):
    monkeypatch.setenv(DATA_RELEASE_ENV, "develop")

    with pytest.raises(ReleaseConfigError, match="only has: local, staging, prod"):
        read_release(write_releases(tmp_path))


def test_read_release_ignores_the_retired_release_variables(tmp_path, monkeypatch):
    monkeypatch.setenv("DATA_VERSION", "191")

    assert read_release(write_releases(tmp_path)).data_version == "221"
    assert retired_release_env_names() == ["DATA_VERSION"]


def test_read_release_reads_the_team_images_folder(tmp_path):
    agora_releases = {
        "local": {
            "dataFile": "syn13363290",
            "dataVersion": "117",
            "teamImagesId": "syn12861877",
        }
    }

    assert read_release(write_releases(tmp_path, agora_releases)).team_images_id == (
        "syn12861877"
    )


def test_read_release_rejects_an_invalid_entry(tmp_path):
    releases = {"local": {"dataFile": "syn73774820", "data_version": "221"}}

    with pytest.raises(ReleaseConfigError) as error:
        read_release(write_releases(tmp_path, releases))

    assert "'dataVersion' must be a non-empty string" in str(error.value)
    assert "unknown key 'data_version'" in str(error.value)
