import pytest
from tests import app, NoneType
from flaskr.db_models import SampleToMutation
from flaskr.enums import status_enum


def test_SampleToMutation(app):
    app()
    sample_id = 481
    string_representation_list = []
    separator = ', '

    results = SampleToMutation.query.filter_by(sample_id=sample_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<SampleToMutation %r>' % sample_id
        string_representation_list.append(string_representation)
        assert result.sample_id == sample_id
        assert type(result.mutation_id) is int
        assert result.status in status_enum.enums
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
