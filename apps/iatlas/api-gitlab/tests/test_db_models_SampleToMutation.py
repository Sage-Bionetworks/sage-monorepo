import pytest
from tests import app, NoneType
from flaskr.database import return_sample_to_mutation_query
from flaskr.db_models import SampleToMutation
from flaskr.enums import status_enum


def test_SampleToMutation(app):
    app()
    sample_id = 481
    string_representation_list = []
    separator = ', '

    query = return_sample_to_mutation_query('samples', 'mutations')
    results = query.filter_by(sample_id=sample_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<SampleToMutation %r>' % sample_id
        string_representation_list.append(string_representation)
        if type(result.mutations) is not NoneType:
            assert isinstance(result.mutations, list)
            for mutation in result.mutations:
                assert type(mutation.id) is int
        if type(result.samples) is not NoneType:
            assert isinstance(result.samples, list)
            for sample in result.samples:
                assert sample.id == sample_id
        assert result.sample_id == sample_id
        assert type(result.mutation_id) is int
        assert result.status in status_enum.enums
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
