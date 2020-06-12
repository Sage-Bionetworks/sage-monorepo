import pytest
from flaskr.resolvers.resolver_helpers import build_option_args, get_child_value, get_value


class Parent:
    def __init__(self, value, value2):
        self.name = value
        self.other = value2


class MockSelectionSet:
    def __init__(self, selections):
        self.selections = selections


class MockSelection:
    def __init__(self, name):
        self.name = name


class MockNameNode:
    def __init__(self, value):
        self.value = value


def test_build_option_args():
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


def test_get_child_value():
    name = "test"
    other = "test2"
    parent = Parent(name, other)
    assert get_child_value(parent) == name
    assert get_child_value(parent, "other") == other
    assert get_child_value(None) == None
    assert get_child_value() == None


def test_get_value():
    name = "test"
    parent = Parent(name, 'unused')
    assert get_value(parent, 'name') == name
    assert get_value(parent, 'nothing') == None
