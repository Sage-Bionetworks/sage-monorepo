import json
import pytest
from tests import app
from flaskr.resolvers.resolver_helpers import get_name


class Parent:
    def __init__(self, name):
        self.name = name


def test_get_name(app):
    name = "test"
    parent = Parent(name)
    assert get_name(parent) == name
    assert get_name(None) == None
    assert get_name() == None
