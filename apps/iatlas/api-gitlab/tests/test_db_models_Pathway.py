import pytest
from flaskr.database import return_pathway_query

name = 'Antigen'


def test_Pathway_with_relations(app):
    app()

    query = return_pathway_query('genes')
    result = query.filter_by(name=name).first()

    assert isinstance(result.genes, list)
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.name == name
    assert repr(result) == '<Pathway %r>' % name


def test_Pathway_no_relations(app):
    app()

    query = return_pathway_query()
    result = query.filter_by(name=name).first()

    assert result.genes == []
    assert result.name == name
