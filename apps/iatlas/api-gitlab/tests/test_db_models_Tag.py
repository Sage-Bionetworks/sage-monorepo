import pytest
from tests import app, NoneType
from flaskr.database import return_tag_query
from flaskr.db_models import Tag

name = 'ACC'


def test_Tag_with_relations(app):
    app()
    relations_to_load = ['related_tags', 'samples', 'tags']

    query = return_tag_query(*relations_to_load)
    result = query.filter_by(name=name).first()

    if type(result.related_tags) is not NoneType:
        assert isinstance(result.related_tags, list)
        # Don't need to iterate through every result.
        for related_tag in result.related_tags[0:2]:
            assert type(related_tag.name) is str
    if type(result.samples) is not NoneType:
        assert isinstance(result.samples, list)
        # Don't need to iterate through every result.
        for sample in result.samples[0:2]:
            assert type(sample.name) is str
    if type(result.tags) is not NoneType:
        assert isinstance(result.tags, list)
        # Don't need to iterate through every result.
        for tag in result.tags[0:2]:
            assert type(tag.name) is str
    assert result.name == name
    assert type(result.characteristics) is str
    assert type(result.display) is str or NoneType
    assert type(result.color) is str or NoneType
    assert repr(result) == '<Tag %r>' % name


def test_Tag_with_copy_number_results(app):
    app()

    query = return_tag_query(['copy_number_results'])
    result = query.filter_by(name=name).first()

    if type(result.copy_number_results) is not NoneType:
        assert isinstance(result.copy_number_results, list)
        # Don't need to iterate through every result.
        for copy_number_result in result.copy_number_results[0:2]:
            assert copy_number_result.tag_id == result.id


def test_Tag_with_driver_results(app):
    app()

    query = return_tag_query(['driver_results'])
    result = query.filter_by(name=name).first()

    if type(result.driver_results) is not NoneType:
        assert isinstance(result.driver_results, list)
        # Don't need to iterate through every result.
        for driver_result in result.driver_results[0:2]:
            assert driver_result.tag_id == result.id


def test_Tag_with_nodes(app):
    app()

    query = return_tag_query('nodes')
    result = query.filter_by(name=name).first()

    if type(result.nodes) is not NoneType:
        assert isinstance(result.nodes, list)
        # Don't need to iterate through every result.
        for node in result.nodes[0:2]:
            assert type(tag.node) is str


def test_Tag_with_node_tag_assoc(app):
    app()


def test_Tag_no_relations(app):
    app()

    query = return_tag_query()
    result = query.filter_by(name=name).first()

    assert result.related_tags == []
    assert result.samples == []
    assert result.tags == []
    assert result.copy_number_results == []
    assert result.driver_results == []
    assert result.node_tag_assoc == []
    assert result.nodes == []
    assert type(result.id) is int
    assert result.name == name
    assert type(result.characteristics) is str
    assert type(result.display) is str or NoneType
    assert type(result.color) is str or NoneType
