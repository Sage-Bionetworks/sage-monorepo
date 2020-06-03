import pytest
from tests import app, NoneType
from flaskr.db_models import GeneType


def test_gene_type(app):
    app()
    gene_type_name = 'extra_cellular_network'
    result = GeneType.query.filter_by(name=gene_type_name).first()

    assert result.name == gene_type_name
    assert type(result.display) is str or NoneType

    assert repr(result) == '<GeneType %r>' % gene_type_name
