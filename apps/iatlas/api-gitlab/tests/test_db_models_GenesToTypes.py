import pytest
from tests import app, NoneType
from flaskr.database import return_gene_to_type_query


def test_GenesToTypes(app):
    app()
    gene_id = 160

    query = return_gene_to_type_query()
    results = query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        assert result.gene_id == gene_id
        assert type(result.type_id) is int
        assert repr(result) == '<GeneToType %r>' % gene_id
    assert repr(results) == '[<GeneToType %r>]' % gene_id
