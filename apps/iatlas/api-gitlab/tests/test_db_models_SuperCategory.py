import pytest
from tests import app
from flaskr.database import return_super_category_query

name = 'Receptor'


def test_SuperCategory_with_relations(app):
    app()

    query = return_super_category_query('genes')
    result = query.filter_by(name=name).first()

    assert isinstance(result.genes, list)
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.name == name
    assert repr(result) == '<SuperCategory %r>' % name


def test_SuperCategory_no_relations(app):
    app()

    query = return_super_category_query()
    result = query.filter_by(name=name).first()

    assert result.genes == []
    assert result.name == name
