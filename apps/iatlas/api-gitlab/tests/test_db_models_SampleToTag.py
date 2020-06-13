import pytest
from tests import app, NoneType
from flaskr.database import return_sample_to_tag_query


def test_SampleToTag(app):
    app()
    sample_id = 1
    string_representation_list = []
    separator = ', '

    query = return_sample_to_tag_query('samples', 'tags')
    results = query.filter_by(sample_id=sample_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<SampleToTag %r>' % sample_id
        string_representation_list.append(string_representation)
        if type(result.samples) is not NoneType:
            assert isinstance(result.samples, list)
            # Don't need to iterate through every result.
            for sample in result.samples[0:2]:
                assert sample.id == sample_id
        if type(result.tags) is not NoneType:
            assert isinstance(result.tags, list)
            # Don't need to iterate through every result.
            for tag in result.tags[0:2]:
                assert type(tag.name) is str
        assert result.sample_id == sample_id
        assert type(result.tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
