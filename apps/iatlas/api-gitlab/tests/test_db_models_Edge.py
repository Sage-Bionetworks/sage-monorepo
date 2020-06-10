import pytest
from tests import app, NoneType
from flaskr.database import return_edge_query


def test_Edge(app):
    app()
    node_1_id = 42
    string_representation_list = []
    separator = ', '

    query = return_edge_query()
    results = query.filter_by(node_1_id=node_1_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<Edge %r>' % result.id
        string_representation_list.append(string_representation)
        assert result.node_1_id == node_1_id
        assert type(result.node_2_id) is int
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
