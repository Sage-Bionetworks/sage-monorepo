import pytest
from api.database import return_sample_to_mutation_query
from api.db_models import SampleToMutation
from api.enums import status_enum


@pytest.fixture(scope='module')
def sample_id():
    return 489


def test_SampleToMutation_with_relations(app, sample_id):
    string_representation_list = []
    separator = ', '

    query = return_sample_to_mutation_query('samples', 'mutations')
    results = query.filter_by(sample_id=sample_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<SampleToMutation %r>' % sample_id
        string_representation_list.append(string_representation)
        if result.mutations:
            assert isinstance(result.mutations, list)
            # Don't need to iterate through every result.
            for mutation in result.mutations[0:2]:
                assert type(mutation.id) is int
        if result.samples:
            assert isinstance(result.samples, list)
            # Don't need to iterate through every result.
            for sample in result.samples[0:2]:
                assert sample.id == sample_id
        assert result.sample_id == sample_id
        assert type(result.mutation_id) is int
        assert result.status in status_enum.enums
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_SampleToMutation_no_relations(app, sample_id):
    query = return_sample_to_mutation_query()
    results = query.filter_by(sample_id=sample_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.mutations == []
        assert result.samples == []
        assert result.sample_id == sample_id
        assert type(result.mutation_id) is int
        assert result.status in status_enum.enums
