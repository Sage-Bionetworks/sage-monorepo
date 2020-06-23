import pytest
from tests import NoneType
from flaskr.database import return_gene_type_query

gene_type_name = 'extra_cellular_network'


def test_gene_type_with_relations(app):
    app()

    query = return_gene_type_query('genes')
    result = query.filter_by(name=gene_type_name).first()

    if result.genes:
        assert isinstance(result.genes, list)
        # Don't need to iterate through every result.
        for gene in result.genes[0:2]:
            assert type(gene.entrez) is int
    assert result.gene_type_assoc == []
    assert result.name == gene_type_name
    assert type(result.display) is str or NoneType
    assert repr(result) == '<GeneType %r>' % gene_type_name


def test_gene_type_with_gene_type_assoc(app):
    app()

    query = return_gene_type_query('gene_type_assoc')
    result = query.filter_by(name=gene_type_name).first()

    if result.gene_type_assoc:
        assert isinstance(result.gene_type_assoc, list)
        # Don't need to iterate through every result.
        for gene_type_rel in result.gene_type_assoc[0:2]:
            assert gene_type_rel.type_id == result.id


def test_gene_type_no_relations(app):
    app()

    query = return_gene_type_query()
    result = query.filter_by(name=gene_type_name).first()

    assert result.gene_type_assoc == []
    assert result.genes == []
    assert type(result.id) is int
    assert result.name == gene_type_name
    assert type(result.display) is str or NoneType
