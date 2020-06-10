import pytest
from tests import app, NoneType
from flaskr.database import return_gene_to_sample_query


def test_GeneToSample(app):
    app()
    gene_id = 1
    string_representation_list = []
    separator = ', '

    query = return_gene_to_sample_query()
    results = query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<GeneToSample %r>' % gene_id
        string_representation_list.append(string_representation)
        assert result.gene_id == gene_id
        assert type(result.sample_id) is int
        assert type(result.rna_seq_expr) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
