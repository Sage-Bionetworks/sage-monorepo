import pytest

from explorers_data_loader.releases import ReleaseSettings
from explorers_data_loader.settings import (
    LOAD_TEAM_IMAGES_ENV,
    DatabaseSettings,
    TeamImagesSettings,
)

AGORA_DATABASE_ENV = {
    "DB_USER": "root",
    "DB_PASS": "password",
    "DB_NAME": "agora",
    "DB_HOST": "agora-mongo",
    "DB_PORT": "27017",
}


def test_database_settings_build_both_uris_from_env(monkeypatch):
    for name, value in AGORA_DATABASE_ENV.items():
        monkeypatch.setenv(name, value)

    db_settings = DatabaseSettings.from_env()

    assert db_settings.client_uri == "mongodb://root:password@agora-mongo:27017"
    assert (
        db_settings.import_uri
        == "mongodb://root:password@agora-mongo:27017/agora?authSource=admin"
    )


def test_database_settings_report_every_missing_variable(monkeypatch):
    for name in AGORA_DATABASE_ENV:
        monkeypatch.delenv(name, raising=False)
    monkeypatch.setenv("DB_USER", AGORA_DATABASE_ENV["DB_USER"])

    with pytest.raises(ValueError, match="DB_PASS, DB_NAME, DB_HOST, DB_PORT"):
        DatabaseSettings.from_env()


AGORA_TEAM_IMAGES_FOLDER_ID = "syn12861877"
AGORA_LOCAL_RELEASE = ReleaseSettings(
    name="local",
    data_file="syn13363290",
    data_version="117",
    team_images_id=AGORA_TEAM_IMAGES_FOLDER_ID,
)


def test_team_images_load_from_the_release_folder_by_default(monkeypatch):
    monkeypatch.delenv(LOAD_TEAM_IMAGES_ENV, raising=False)

    assert TeamImagesSettings.from_release(AGORA_LOCAL_RELEASE) == TeamImagesSettings(
        folder_id=AGORA_TEAM_IMAGES_FOLDER_ID
    )


@pytest.mark.parametrize("disabled", ["false", "False", " FALSE "])
def test_team_images_turn_off_with_load_team_images_false(monkeypatch, disabled):
    monkeypatch.setenv(LOAD_TEAM_IMAGES_ENV, disabled)

    assert TeamImagesSettings.from_release(AGORA_LOCAL_RELEASE) is None


def test_team_images_require_a_folder_in_the_release_when_turned_on(monkeypatch):
    monkeypatch.setenv(LOAD_TEAM_IMAGES_ENV, "true")
    release_without_images = ReleaseSettings(
        name="local", data_file="syn13363290", data_version="117"
    )

    with pytest.raises(ValueError, match="no teamImagesId"):
        TeamImagesSettings.from_release(release_without_images)


def test_load_team_images_rejects_a_value_that_is_not_true_or_false(monkeypatch):
    monkeypatch.setenv(LOAD_TEAM_IMAGES_ENV, "no")

    with pytest.raises(ValueError, match="must be true or false"):
        TeamImagesSettings.from_release(AGORA_LOCAL_RELEASE)
