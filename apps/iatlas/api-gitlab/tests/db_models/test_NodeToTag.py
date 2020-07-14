import pytest
from api.database import return_node_to_tag_query


@pytest.fixture(scope='module')
def node_id():
    return 1


def test_NodeToTag_with_relations(app, node_id):
    string_representation_list = []
    separator = ', '
    relationships_to_load = ['nodes', 'tags']

    query = return_node_to_tag_query(*relationships_to_load)
    results = query.filter_by(node_id=node_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        string_representation = '<NodeToTag %r>' % node_id
        string_representation_list.append(string_representation)
        assert isinstance(result.nodes, list)
        assert len(result.nodes) > 0
        # Don't need to iterate through every result.
        for node in result.nodes[0:2]:
            assert node.id == node_id
        assert isinstance(result.tags, list)
        assert len(result.tags) > 0
        # Don't need to iterate through every result.
        for tag in result.tags[0:2]:
            assert type(tag.name) is str
        assert result.node_id == node_id
        assert type(result.tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_NodeToTag_no_relations(app, node_id):
    query = return_node_to_tag_query()
    results = query.filter_by(node_id=node_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result.nodes == []
        assert result.tags == []
        assert result.node_id == node_id
        assert type(result.tag_id) is int
