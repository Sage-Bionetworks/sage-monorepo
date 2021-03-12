import pytest
from tests import NoneType
from api.database import return_dataset_to_tag_query


def test_DatasetToTag_with_relations(app, data_set, data_set_id):
    relationships_to_join = ['data_sets', 'tags']

    query = return_dataset_to_tag_query(*relationships_to_join)
    results = query.filter_by(dataset_id=data_set_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        data_sets = result.data_sets
        tags = result.tags
        assert result.dataset_id == data_set_id
        assert isinstance(data_sets, list)
        assert len(data_sets) == 1
        for current_data_set in data_sets:
            assert current_data_set.name == data_set
        assert isinstance(tags, list)
        assert len(tags) > 0
        for tag in tags:
            assert type(tag.name) is str
        assert repr(result) == '<DatasetToTag %r>' % data_set_id
    assert repr(results) == '[<DatasetToTag %r>]' % data_set_id


def test_DatasetToTag_no_relations(app, data_set_id):
    query = return_dataset_to_tag_query()
    results = query.filter_by(dataset_id=data_set_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert result.data_sets == []
        assert result.tags == []
        assert result.dataset_id == data_set_id
        assert type(result.tag_id) is int
