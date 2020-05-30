import pytest
from flaskr import create_app, db
from config import Config


class TestConfig(Config):
    TESTING = True


@pytest.yield_fixture
def app():
    def _app(config_class):
        app = create_app(config_class)
        app.test_request_context().push()

        return app

    yield _app
    db.session.remove()


@pytest.fixture
def client():
    app = create_app(TestConfig)

    with app.test_client() as client:
        yield client
