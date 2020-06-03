import pytest
from tests import app, NoneType
from flaskr.db_models import NodeToTag


def test_NodeToTag(app):
    app()
    node_id = 1
    string_representation_list = []
    separator = ', '

    results = NodeToTag.query.filter_by(node_id=node_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<NodeToTag %r>' % node_id
        string_representation_list.append(string_representation)
        assert result.node_id == node_id
        assert type(result.tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
