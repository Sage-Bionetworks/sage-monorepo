import pytest
from _pytest.monkeypatch import MonkeyPatch
from os import getenv
from config import get_config, get_database_uri


def test_get_database_uri(monkeypatch: MonkeyPatch):
    monkeypatch.setenv('POSTGRES_USER', 'TestingUser')
    monkeypatch.setenv('POSTGRES_PASSWORD', 'TestingPassword')
    monkeypatch.setenv('POSTGRES_DB', 'TestingDB')
    monkeypatch.setenv('POSTGRES_HOST', 'TestingHost')

    monkeypatch.delenv('POSTGRES_PORT', raising=False)
    assert get_database_uri() == 'postgresql://TestingUser:TestingPassword@TestingHost/TestingDB'

    monkeypatch.setenv('POSTGRES_PORT', '4242')
    assert get_database_uri(
    ) == 'postgresql://TestingUser:TestingPassword@TestingHost:4242/TestingDB'

    DATABASE_URI = 'postgresql://SomeUser:SomePassword@SomeHost/SomeDB'
    monkeypatch.setenv('DATABASE_URI', DATABASE_URI)
    assert get_database_uri() == DATABASE_URI


def test_testing_config(app):
    FLASK_ENV = getenv('FLASK_ENV')
    if FLASK_ENV == 'development':
        assert app.config['DEBUG']
    else:
        assert not app.config['DEBUG']
    assert not app.config['PROFILE']
    assert app.config['TESTING']
    assert app.config['SQLALCHEMY_DATABASE_URI'] == get_database_uri()
    assert app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] == False


def test_development_config(monkeypatch: MonkeyPatch):
    from api import create_app

    FLASK_ENV = 'development'
    monkeypatch.setenv('FLASK_ENV', FLASK_ENV)
    app = create_app()
    assert app.config['DEBUG']
    assert app.config['PROFILE']
    assert not app.config['TESTING']
    assert app.config['SQLALCHEMY_DATABASE_URI'] == get_database_uri()
    assert app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] == False


def test_staging_config(monkeypatch: MonkeyPatch):
    from api import create_app

    FLASK_ENV = 'staging'
    monkeypatch.setenv('FLASK_ENV', FLASK_ENV)
    app = create_app()
    assert not app.config['DEBUG']
    assert not app.config['PROFILE']
    assert not app.config['TESTING']
    assert app.config['SQLALCHEMY_DATABASE_URI'] == get_database_uri()
    assert app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] == False


def test_production_config(monkeypatch: MonkeyPatch):
    from api import create_app

    FLASK_ENV = 'production'
    monkeypatch.setenv('FLASK_ENV', FLASK_ENV)
    app = create_app()
    assert not app.config['DEBUG']
    assert not app.config['PROFILE']
    assert not app.config['TESTING']
    assert app.config['SQLALCHEMY_DATABASE_URI'] == get_database_uri()
    assert app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] == False
