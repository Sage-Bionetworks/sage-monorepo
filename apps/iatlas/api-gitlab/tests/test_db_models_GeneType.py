import pytest
from tests import app, NoneType
from flaskr.database import return_gene_type_query


def test_gene_type(app):
    app()
    gene_type_name = 'extra_cellular_network'

    query = return_gene_type_query()
    result = query.filter_by(name=gene_type_name).first()

    if type(result.genes) is not NoneType:
        assert isinstance(result.genes, list)
        for gene in result.genes:
            assert type(gene.entrez) is int
    assert result.name == gene_type_name
    assert type(result.display) is str or NoneType

    assert repr(result) == '<GeneType %r>' % gene_type_name
