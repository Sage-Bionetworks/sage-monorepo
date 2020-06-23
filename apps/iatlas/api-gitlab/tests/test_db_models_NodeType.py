import pytest
from flaskr.database import return_node_type_query

name = 'Ligand'


def test_NodeType_with_relations(app):
    app()

    query = return_node_type_query('genes')
    result = query.filter_by(name=name).first()

    assert isinstance(result.genes, list)
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.name == name
    assert repr(result) == '<NodeType %r>' % name


def test_NodeType_no_relations(app):
    app()

    query = return_node_type_query()
    result = query.filter_by(name=name).first()

    assert result.genes == []
    assert result.name == name
