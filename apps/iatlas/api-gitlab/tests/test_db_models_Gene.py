import pytest
from tests import app, NoneType
from flaskr.db_models import Gene


def test_Gene(app):
    app()
    entrez = 1
    hgnc = 'A1BG'

    result = Gene.query.filter_by(entrez=entrez).first()

    assert result.entrez == entrez
    assert result.hgnc == hgnc
    assert type(result.description) is str or NoneType
    assert type(result.io_landscape_name) is str or NoneType
    assert isinstance(result.references, list) or NoneType
    assert type(result.node_type_id) is int or NoneType
    assert type(result.pathway_id) is int or NoneType
    assert type(result.therapy_type_id) is int or NoneType
    assert repr(result) == '<Gene %r>' % entrez
