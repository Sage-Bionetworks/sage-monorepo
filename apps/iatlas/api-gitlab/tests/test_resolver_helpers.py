import json
import pytest
from tests import app
from flaskr.resolvers.resolver_helpers import build_option_args, get_name


class Parent:
    def __init__(self, name):
        self.name = name


class MockSelectionSet:
    def __init__(self, selections):
        self.selections = selections


class MockSelection:
    def __init__(self, name):
        self.name = name


class MockNameNode:
    def __init__(self, value):
        self.value = value


def test_build_option_args(app):
    valid_nodes_1 = {'test1': 'nice',
                     'test2': 'good'}
    valid_nodes_2 = {'test0': 'oh',
                     'test2': 'good'}
    selection_set = MockSelectionSet([
        MockSelection(MockNameNode('test1')),
        MockSelection(MockNameNode('test2'))
    ])
    assert build_option_args(selection_set, valid_nodes_1) == ['nice', 'good']
    assert build_option_args(selection_set, valid_nodes_2) == ['good']
    assert build_option_args(None, valid_nodes_1) == []
    assert build_option_args(selection_set, {}) == []
    assert build_option_args(selection_set) == []
    assert build_option_args() == []


def test_get_name(app):
    name = "test"
    parent = Parent(name)
    assert get_name(parent) == name
    assert get_name(None) == None
    assert get_name() == None
