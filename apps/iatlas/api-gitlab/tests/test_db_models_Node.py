import pytest
from tests import app, NoneType
from flaskr.database import return_node_query


def test_Node(app):
    app()
    gene_id = 30749
    string_representation_list = []
    separator = ', '

    query = return_node_query()
    results = query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        node_id = result.id
        string_representation = '<Node %r>' % node_id
        string_representation_list.append(string_representation)
        assert result.gene_id == gene_id
        assert type(result.feature_id) is NoneType
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert type(result.x) is float or NoneType
        assert type(result.y) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
