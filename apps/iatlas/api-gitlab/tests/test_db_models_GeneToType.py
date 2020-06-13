import pytest
from tests import app, NoneType
from flaskr.database import return_gene_to_type_query


def test_GeneToType_with_relations(app):
    app()
    gene_id = 160
    relationships_to_join = ['genes', 'types']

    query = return_gene_to_type_query(*relationships_to_join)
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.gene_id == gene_id
        assert isinstance(result.genes, list)
        # Don't need to iterate through every result.
        for gene in result.genes[0:2]:
            assert gene.id == gene_id
            assert type(gene.entrez) is int
        assert isinstance(result.types, list)
        # Don't need to iterate through every result.
        for gene_type in result.types[0:2]:
            assert type(gene_type.name) is str
        assert type(result.type_id) is int
        assert repr(result) == '<GeneToType %r>' % gene_id
    assert repr(results) == '[<GeneToType %r>]' % gene_id


def test_GeneToType_no_relations(app):
    app()
    gene_id = 160

    query = return_gene_to_type_query()
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.gene_id == gene_id
        assert result.genes == []
        assert result.types == []
        assert type(result.type_id) is int
