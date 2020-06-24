import pytest
from api.database import return_sample_to_tag_query


@pytest.fixture(scope='module')
def sample_id():
    return 1


def test_SampleToTag_with_relations(app, sample_id):
    string_representation_list = []
    separator = ', '

    query = return_sample_to_tag_query('samples', 'tags')
    results = query.filter_by(sample_id=sample_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<SampleToTag %r>' % sample_id
        string_representation_list.append(string_representation)
        if result.samples:
            assert isinstance(result.samples, list)
            # Don't need to iterate through every result.
            for sample in result.samples[0:2]:
                assert sample.id == sample_id
        if result.tags:
            assert isinstance(result.tags, list)
            # Don't need to iterate through every result.
            for tag in result.tags[0:2]:
                assert type(tag.name) is str
        assert result.sample_id == sample_id
        assert type(result.tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_SampleToTag_no_relations(app, sample_id):
    query = return_sample_to_tag_query()
    results = query.filter_by(sample_id=sample_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.samples == []
        assert result.tags == []
        assert result.sample_id == sample_id
        assert type(result.tag_id) is int
