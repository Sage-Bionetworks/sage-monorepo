import pytest
from tests import app, NoneType
from flaskr.database import return_node_to_tag_query


def test_NodeToTag_with_relations(app):
    app()
    node_id = 1
    string_representation_list = []
    separator = ', '
    relationships_to_load = ['nodes', 'tags']

    query = return_node_to_tag_query(*relationships_to_load)
    results = query.filter_by(node_id=node_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<NodeToTag %r>' % node_id
        string_representation_list.append(string_representation)
        if type(result.nodes) is not NoneType:
            assert isinstance(result.nodes, list)
            # Don't need to iterate through every result.
            for node in result.nodes[0:2]:
                assert node.id == node_id
        if type(result.tags) is not NoneType:
            assert isinstance(result.tags, list)
            # Don't need to iterate through every result.
            for tag in result.tags[0:2]:
                assert type(tag.name) is str
        assert result.node_id == node_id
        assert type(result.tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_NodeToTag_no_relations(app):
    app()
    node_id = 1

    query = return_node_to_tag_query()
    results = query.filter_by(node_id=node_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.node_id == node_id
        assert type(result.tag_id) is int
