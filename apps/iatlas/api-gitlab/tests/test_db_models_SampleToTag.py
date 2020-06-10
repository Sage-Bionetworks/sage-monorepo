import pytest
from tests import app, NoneType
from flaskr.database import return_sample_to_tag_query


def test_SampleToTag(app):
    app()
    sample_id = 1
    string_representation_list = []
    separator = ', '

    query = return_sample_to_tag_query('samples', 'tags')
    results = query.filter_by(sample_id=sample_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<SampleToTag %r>' % sample_id
        string_representation_list.append(string_representation)
        if type(result.samples) is not NoneType:
            assert isinstance(result.samples, list)
            for sample in result.samples:
                assert sample.id == sample_id
        if type(result.tags) is not NoneType:
            assert isinstance(result.tags, list)
            for tag in result.tags:
                assert type(tag.name) is str
        assert result.sample_id == sample_id
        assert type(result.tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
