import pytest
from tests import app
from flaskr.database import return_gene_family_query

name = 'Butyrophilins'


def test_GeneFamily_with_relations(app):
    app()

    query = return_gene_family_query('genes')
    result = query.filter_by(name=name).first()

    assert isinstance(result.genes, list)
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.name == name
    assert repr(result) == '<GeneFamily %r>' % name


def test_GeneFamily_no_relations(app):
    app()

    query = return_gene_family_query()
    result = query.filter_by(name=name).first()

    assert result.genes == []
    assert result.name == name
