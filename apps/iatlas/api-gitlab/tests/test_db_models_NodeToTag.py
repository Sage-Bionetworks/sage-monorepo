import pytest
from tests import app, NoneType
from flaskr.database import return_node_to_tag_query


def test_NodeToTag(app):
    app()
    node_id = 1
    string_representation_list = []
    separator = ', '

    query = return_node_to_tag_query()
    results = query.filter_by(node_id=node_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<NodeToTag %r>' % node_id
        string_representation_list.append(string_representation)
        assert result.node_id == node_id
        assert type(result.tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
