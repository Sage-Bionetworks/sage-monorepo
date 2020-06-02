import json
import os
import pytest
from tests import app, NoneType
from flaskr.db_models import Node


def test_Node(app):
    app()
    gene_id = 30749

    results = Node.query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        assert result.gene_id == gene_id
        assert type(result.feature_id) is NoneType
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert type(result.x) is float or NoneType
        assert type(result.y) is float or NoneType
        assert repr(result) == '<Node %r>' % result.id
