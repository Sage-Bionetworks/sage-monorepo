import pytest
import os
from tests import app, TestConfig
from config import Config, get_database_uri
from flaskr.resolvers.hello_resolver import resolve_hello


def test_resolve_hello():
    # app = app(TestConfig)
    # resolve_hello(info={})
    # assert app.config["DEBUG"]
    # assert app.config["TESTING"]
    # assert app.config["SQLALCHEMY_DATABASE_URI"] == get_database_uri()
    # assert app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] == False
    pass
