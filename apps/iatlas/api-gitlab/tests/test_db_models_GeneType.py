import pytest
from tests import app, NoneType
from flaskr.database import return_gene_type_query


def test_gene_type_with_relations(app):
    app()
    gene_type_name = 'extra_cellular_network'
    relationships_to_load = ['gene_type_assoc', 'genes']

    query = return_gene_type_query()
    result = query.filter_by(name=gene_type_name).first()

    if type(result.gene_type_assoc) is not NoneType:
        assert isinstance(result.gene_type_assoc, list)
        # Don't need to iterate through every result.
        for gene_type_rel in result.gene_type_assoc[0:2]:
            assert gene_type_rel.type_id == result.id
    if type(result.genes) is not NoneType:
        assert isinstance(result.genes, list)
        # Don't need to iterate through every result.
        for gene in result.genes[0:2]:
            assert type(gene.entrez) is int
    assert result.name == gene_type_name
    assert type(result.display) is str or NoneType
    assert repr(result) == '<GeneType %r>' % gene_type_name


def test_gene_type_no_relations(app):
    app()
    gene_type_name = 'extra_cellular_network'

    query = return_gene_type_query()
    result = query.filter_by(name=gene_type_name).first()

    assert result.gene_type_assoc == []
    assert result.genes == []
    assert type(result.id) is int
    assert result.name == gene_type_name
    assert type(result.display) is str or NoneType
