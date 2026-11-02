import json

import pytest

from explorers_data_loader.releases import (
    ReleaseConfigError,
    ReleaseSettings,
    read_release,
    retired_release_env_names,
)

MODEL_AD_RELEASE = {
    "$comment": "TODO(EX-146): deployed releases still read the data manager",
    "dataFile": "syn74702351",
    "dataVersion": "11",
}
RETIRED_ENV_NAMES = ("DATA_FILE", "DATA_VERSION", "TEAM_IMAGES_ID")


@pytest.fixture(autouse=True)
def clear_release_env(monkeypatch):
    for name in RETIRED_ENV_NAMES:
        monkeypatch.delenv(name, raising=False)


def write_release(tmp_path, release=MODEL_AD_RELEASE):
    release_filepath = tmp_path / "data-release.json"
    release_filepath.write_text(json.dumps(release))
    return str(release_filepath)


def test_read_release_reads_the_release_and_skips_annotations(tmp_path):
    assert read_release(write_release(tmp_path)) == ReleaseSettings(
        data_file=MODEL_AD_RELEASE["dataFile"],
        data_version=MODEL_AD_RELEASE["dataVersion"],
    )


def test_read_release_ignores_the_retired_release_variables(tmp_path, monkeypatch):
    monkeypatch.setenv("DATA_VERSION", "191")

    release = read_release(write_release(tmp_path))

    assert release.data_version == MODEL_AD_RELEASE["dataVersion"]
    assert retired_release_env_names() == ["DATA_VERSION"]


def test_read_release_reads_the_team_images_folder(tmp_path):
    agora_release = {
        "dataFile": "syn13363290",
        "dataVersion": "117",
        "teamImagesId": "syn12861877",
    }

    release = read_release(write_release(tmp_path, agora_release))

    assert release.team_images_id == agora_release["teamImagesId"]


def test_read_release_rejects_a_file_that_is_not_an_object(tmp_path):
    with pytest.raises(ReleaseConfigError, match="must hold a JSON object"):
        read_release(write_release(tmp_path, [MODEL_AD_RELEASE]))


def test_read_release_rejects_an_invalid_release(tmp_path):
    release = {"dataFile": "syn74702351", "data_version": "11"}

    with pytest.raises(ReleaseConfigError) as error:
        read_release(write_release(tmp_path, release))

    assert "'dataVersion' must be a non-empty string" in str(error.value)
    assert "unknown key 'data_version'" in str(error.value)
