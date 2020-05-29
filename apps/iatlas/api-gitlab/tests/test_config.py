import pytest
import os
from tests import app, TestConfig
from config import Config, get_database_uri


# @pytest.mark.skipif(
#     "TRAVIS" in os.environ and os.environ["TRAVIS"] == "True",
#     reason="Skipping this test on Travis CI.",
# )
def test_testing_config(app):
    app = app(TestConfig)
    assert app.config["DEBUG"]
    assert app.config["TESTING"]
    assert app.config["SQLALCHEMY_DATABASE_URI"] == get_database_uri()
    assert app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] == False


def test_config(app):
    app = app(Config)
    assert not app.config["TESTING"]
    assert app.config["SQLALCHEMY_DATABASE_URI"] == get_database_uri()
    assert app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] == False
