import pytest

from explorers_data_loader.settings import DatabaseSettings, ReleaseSettings

AGORA_DATABASE_ENV = {
    "DB_USER": "root",
    "DB_PASS": "changeme",
    "DB_NAME": "agora",
    "DB_HOST": "agora-mongo",
    "DB_PORT": "27017",
}


def test_database_settings_build_both_uris_from_env(monkeypatch):
    for name, value in AGORA_DATABASE_ENV.items():
        monkeypatch.setenv(name, value)

    db_settings = DatabaseSettings.from_env()

    assert db_settings.client_uri == "mongodb://root:changeme@agora-mongo:27017"
    assert (
        db_settings.import_uri
        == "mongodb://root:changeme@agora-mongo:27017/agora?authSource=admin"
    )


def test_database_settings_report_every_missing_variable(monkeypatch):
    for name in AGORA_DATABASE_ENV:
        monkeypatch.delenv(name, raising=False)
    monkeypatch.setenv("DB_USER", AGORA_DATABASE_ENV["DB_USER"])

    with pytest.raises(ValueError, match="DB_PASS, DB_NAME, DB_HOST, DB_PORT"):
        DatabaseSettings.from_env()


def test_release_settings_reject_an_empty_data_version(monkeypatch):
    monkeypatch.setenv("DATA_FILE", "syn13363290")
    monkeypatch.setenv("DATA_VERSION", "")

    with pytest.raises(ValueError, match="DATA_VERSION"):
        ReleaseSettings.from_env()
