import pytest
from tests import NoneType
from api.database import return_tag_query


@pytest.fixture(scope='module')
def tag_name():
    return 'ACC'


def test_Tag_no_relations(app, tag_name):
    query = return_tag_query()
    result = query.filter_by(name=tag_name).one_or_none()

    assert result
    assert result.related_tags == []
    assert result.samples == []
    assert result.tags == []
    assert result.copy_number_results == []
    assert result.driver_results == []
    assert result.node_tag_assoc == []
    assert result.nodes == []
    assert type(result.id) is int
    assert result.name == tag_name
    assert type(result.characteristics) is str
    assert type(result.display) is str or NoneType
    assert type(result.color) is str or NoneType


def test_Tag_with_relations(app, tag_name):
    relations_to_load = ['related_tags', 'samples']

    query = return_tag_query(*relations_to_load)
    result = query.filter_by(name=tag_name).one_or_none()

    assert result
    assert isinstance(result.related_tags, list)
    assert len(result.related_tags) > 0
    # Don't need to iterate through every result.
    for related_tag in result.related_tags[0:2]:
        assert type(related_tag.name) is str
    assert isinstance(result.samples, list)
    assert len(result.samples) > 0
    # Don't need to iterate through every result.
    for sample in result.samples[0:2]:
        assert type(sample.name) is str
    assert result.name == tag_name
    assert type(result.characteristics) is str
    assert type(result.display) is str or NoneType
    assert type(result.color) is str or NoneType
    assert repr(result) == '<Tag %r>' % tag_name


def test_Tag_with_copy_number_results(app, tag_name):
    query = return_tag_query('copy_number_results')
    result = query.filter_by(name=tag_name).one_or_none()

    assert result
    assert isinstance(result.copy_number_results, list)
    assert len(result.copy_number_results) > 0
    # Don't need to iterate through every result.
    for copy_number_result in result.copy_number_results[0:2]:
        assert copy_number_result.tag_id == result.id


def test_Tag_with_datasets(app, related):
    query = return_tag_query('datasets')
    result = query.filter_by(name=related).one_or_none()

    assert result
    assert isinstance(result.datasets, list)
    assert len(result.datasets) > 0
    # Don't need to iterate through every result.
    for dataset in result.datasets[0:2]:
        assert type(dataset.name) is str


def test_Tag_with_driver_results(app, tag_name):
    query = return_tag_query('driver_results')
    result = query.filter_by(name=tag_name).one_or_none()

    assert result
    assert isinstance(result.driver_results, list)
    assert len(result.driver_results) > 0
    # Don't need to iterate through every result.
    for driver_result in result.driver_results[0:2]:
        assert driver_result.tag_id == result.id


def test_Tag_with_nodes(app, tag_name):
    query = return_tag_query('nodes')
    result = query.filter_by(name=tag_name).one_or_none()

    assert result
    assert isinstance(result.nodes, list)
    assert len(result.nodes) > 0
    # Don't need to iterate through every result.
    for node in result.nodes[0:2]:
        assert type(node.name) is str


def test_Tag_with_node_tag_assoc(app, tag_name):
    query = return_tag_query('node_tag_assoc')
    result = query.filter_by(name=tag_name).one_or_none()

    assert result
    assert isinstance(result.node_tag_assoc, list)
    assert len(result.node_tag_assoc) > 0
    # Don't need to iterate through every result.
    for node_tag_rel in result.node_tag_assoc[0:2]:
        assert node_tag_rel.tag_id == result.id


def test_Tag_with_tags(app, related):
    query = return_tag_query('tags')
    result = query.filter_by(name=related).one_or_none()

    assert result
    assert isinstance(result.tags, list)
    assert len(result.tags) > 0
    # Don't need to iterate through every result.
    for tag in result.tags[0:2]:
        assert type(tag.name) is str
