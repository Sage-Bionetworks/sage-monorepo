import pytest
from tests import app
from flaskr.database import return_gene_function_query


def test_GeneFunction_with_relations(app):
    app()
    name = 'Granzyme'

    query = return_gene_function_query('genes')
    result = query.filter_by(name=name).first()

    assert isinstance(result.genes, list)
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.name == name
    assert repr(result) == '<GeneFunction %r>' % name


def test_GeneFunction_no_relations(app):
    app()
    name = 'Granzyme'

    query = return_gene_function_query()
    result = query.filter_by(name=name).first()

    assert result.genes == []
    assert result.name == name
