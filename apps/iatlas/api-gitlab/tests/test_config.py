import pytest
from _pytest.monkeypatch import MonkeyPatch
import os
from config import Config, get_database_uri


@pytest.mark.skipif(
    'CI' in os.environ and os.environ['CI'] == '1',
    reason='Skipping this test on GitLab CI.',
)
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
    if os.getenv('FLASK_ENV') == 'development':
        assert app.config['DEBUG']
    else:
        assert not app.config['DEBUG']
    assert app.config['TESTING']
    assert app.config['SQLALCHEMY_DATABASE_URI'] == get_database_uri()
    assert app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] == False


def test_config():
    from api import create_app
    app = create_app(Config)
    if os.getenv('FLASK_ENV') == 'development':
        assert app.config['DEBUG']
    else:
        assert not app.config['DEBUG']
    assert not app.config['TESTING']
    assert app.config['SQLALCHEMY_DATABASE_URI'] == get_database_uri()
    assert app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] == False
