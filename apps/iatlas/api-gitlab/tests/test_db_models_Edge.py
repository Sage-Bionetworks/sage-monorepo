import pytest
from tests import app, NoneType
from flaskr.db_models import Edge


def test_Edge(app):
    app()
    node_1_id = 30749
    string_representation_list = []
    separator = ', '

    results = Edge.query.filter_by(node_1_id=node_1_id).all()

    assert isinstance(results, list)
    for result in results:
        edge_id = result.id
        string_representation = '<Edge %r>' % node_id
        string_representation_list.append(string_representation)
        assert result.node_1_id == node_1_id
        assert type(result.node_2_id) is int
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert repr(result) == string_representations
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
