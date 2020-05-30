import pytest
import os
from tests import app, TestConfig
from config import Config, get_database_uri


def test_get_database_uri(monkeypatch):
    monkeypatch.setenv("POSTGRES_USER", "TestingUser")
    monkeypatch.setenv("POSTGRES_PASSWORD", "TestingPassword")
    monkeypatch.setenv("POSTGRES_DB", "TestingDB")
    monkeypatch.setenv("POSTGRES_HOST", "TestingHost")

    monkeypatch.delenv("POSTGRES_PORT", raising=False)
    assert get_database_uri() == 'postgresql://TestingUser:TestingPassword@TestingHost/TestingDB'

    monkeypatch.setenv("POSTGRES_PORT", "4242")
    assert get_database_uri(
    ) == 'postgresql://TestingUser:TestingPassword@TestingHost:4242/TestingDB'

    DATABASE_URI = "postgresql://SomeUser:SomePassword@SomeHost/SomeDB"
    monkeypatch.setenv("DATABASE_URI", DATABASE_URI)
    assert get_database_uri() == DATABASE_URI


def test_testing_config(app):
    app = app(TestConfig)
    if os.getenv("FLASK_ENV") == "development":
        assert app.config["DEBUG"]
    else:
        assert not app.config["DEBUG"]
    assert app.config["TESTING"]
    assert app.config["SQLALCHEMY_DATABASE_URI"] == get_database_uri()
    assert app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] == False


def test_config(app):
    app = app(Config)
    if os.getenv("FLASK_ENV") == "development":
        assert app.config["DEBUG"]
    else:
        assert not app.config["DEBUG"]
    assert not app.config["TESTING"]
    assert app.config["SQLALCHEMY_DATABASE_URI"] == get_database_uri()
    assert app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] == False
