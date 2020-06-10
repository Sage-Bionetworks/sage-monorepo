import pytest
from tests import app, NoneType
from flaskr.database import return_mutation_query
from flaskr.db_models import Mutation


def test_Mutation(app):
    app()
    gene_id = 77
    string_representation_list = []
    separator = ', '

    query = return_mutation_query(
        'gene', 'mutation_code', 'mutation_type', 'samples')
    results = query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        mutation_id = result.id
        string_representation = '<Mutation %r>' % mutation_id
        string_representation_list.append(string_representation)
        if type(result.gene) is not NoneType:
            assert result.gene.id == gene_id
        if type(result.mutation_code) is not NoneType:
            assert result.mutation_code.id == result.mutation_code_id
        if type(result.mutation_type) is not NoneType:
            assert result.mutation_type.id == result.mutation_type_id
        if type(result.samples) is not NoneType:
            assert isinstance(result.samples, list)
            for sample in result.samples:
                assert type(sample.id) is int
        assert result.gene_id == gene_id
        assert type(result.gene_id) is int
        assert type(result.mutation_code_id) is int
        assert type(result.mutation_type_id) is int or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
