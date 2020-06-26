import pytest
from tests import NoneType
from api.database import return_mutation_query
from api.db_models import Mutation


@pytest.fixture(scope='module')
def gene_id():
    return 77


def test_Mutation_with_relations(app, gene_id):
    string_representation_list = []
    separator = ', '
    relationships_to_load = [
        'gene', 'mutation_code', 'mutation_type', 'samples']

    query = return_mutation_query(*relationships_to_load)
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        mutation_id = result.id
        string_representation = '<Mutation %r>' % mutation_id
        string_representation_list.append(string_representation)
        if result.gene:
            assert result.gene.id == gene_id
        if result.mutation_code:
            assert result.mutation_code.id == result.mutation_code_id
        if result.mutation_type:
            assert result.mutation_type.id == result.mutation_type_id
        if result.samples:
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


def test_Mutation_with_sample_mutation_assoc(app, gene_id):
    query = return_mutation_query('sample_mutation_assoc')
    result = query.filter_by(gene_id=gene_id).first()

    if result.sample_mutation_assoc:
        assert isinstance(result.sample_mutation_assoc, list)
        # Don't need to iterate through every result.
        for sample_mutation_rel in result.sample_mutation_assoc[0:2]:
            assert sample_mutation_rel.mutation_id == result.id


def test_Mutation_no_relations(app, gene_id):
    query = return_mutation_query()
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.gene) is NoneType
        assert type(result.mutation_code) is NoneType
        assert type(result.mutation_type) is NoneType
        assert result.samples == []
        assert type(result.id) is int
        assert result.gene_id == gene_id
        assert type(result.gene_id) is int
        assert type(result.mutation_code_id) is int
        assert type(result.mutation_type_id) is int or NoneType
