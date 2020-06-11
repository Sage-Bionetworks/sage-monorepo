import pytest
from tests import app, NoneType
from flaskr.database import return_gene_to_type_query


def test_GeneToType(app):
    app()
    gene_id = 160

    query = return_gene_to_type_query()
    results = query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        assert result.gene_id == gene_id
        assert isinstance(result.genes, list)
        for gene in result.genes:
            assert gene.id == gene_id
            assert type(gene.entrez) is int
        assert isinstance(result.types, list)
        for gene_type in result.types:
            assert type(gene_type.name) is str
        assert type(result.type_id) is int
        assert repr(result) == '<GeneToType %r>' % gene_id
    assert repr(results) == '[<GeneToType %r>]' % gene_id
