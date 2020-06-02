import pytest
from tests import app, NoneType
from flaskr.db_models import Node


def test_Node(app):
    app()
    gene_id = 30749
    string_representation_list = []
    separator = ', '

    results = Node.query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        node_id = result.id
        string_represntation = '<Node %r>' % node_id
        string_representation_list.append(string_represntation)
        assert result.gene_id == gene_id
        assert type(result.feature_id) is NoneType
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert type(result.x) is float or NoneType
        assert type(result.y) is float or NoneType
        assert repr(result) == string_represntation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
