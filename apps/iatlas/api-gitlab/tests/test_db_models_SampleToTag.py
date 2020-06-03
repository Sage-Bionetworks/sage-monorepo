import pytest
from tests import app, NoneType
from flaskr.db_models import SampleToTag


def test_SampleToTag(app):
    app()
    sample_id = 1
    string_representation_list = []
    separator = ', '

    results = SampleToTag.query.filter_by(sample_id=sample_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<SampleToTag %r>' % sample_id
        string_representation_list.append(string_representation)
        assert result.sample_id == sample_id
        assert type(result.tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
