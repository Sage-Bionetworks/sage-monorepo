import pytest
from tests import app, NoneType
from flaskr.database import return_mutation_query
from flaskr.db_models import Mutation


def test_Mutation_with_relations(app):
    app()
    gene_id = 77
    string_representation_list = []
    separator = ', '
    relationships_to_load = [
        'gene', 'mutation_code', 'mutation_type', 'samples']

    query = return_mutation_query('sample_mutation_assoc')
    result = query.filter_by(gene_id=gene_id).first()

    if type(result.sample_mutation_assoc) is not NoneType:
        assert isinstance(result.sample_mutation_assoc, list)
        # Don't need to iterate through every result.
        for sample_mutation_rel in result.sample_mutation_assoc[0:2]:
            assert sample_mutation_rel.mutation_id == result.id

    query = return_mutation_query(*relationships_to_load)
    results = query.filter_by(gene_id=gene_id).limit(3).all()

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
            # Don't need to iterate through every result.
            for sample in result.samples[0:2]:
                assert type(sample.id) is int
        assert result.gene_id == gene_id
        assert type(result.gene_id) is int
        assert type(result.mutation_code_id) is int
        assert type(result.mutation_type_id) is int or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_Mutation_no_relations(app):
    app()
    gene_id = 77
    string_representation_list = []
    separator = ', '

    query = return_mutation_query()
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.id) is int
        assert result.gene_id == gene_id
        assert type(result.gene_id) is int
        assert type(result.mutation_code_id) is int
        assert type(result.mutation_type_id) is int or NoneType
