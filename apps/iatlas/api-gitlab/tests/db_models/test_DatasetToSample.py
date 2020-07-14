import pytest
from api.database import return_dataset_to_sample_query


def test_DatasetToSample_with_relations(app, data_set_id):
    relationships_to_join = ['data_sets', 'samples']

    query = return_dataset_to_sample_query(*relationships_to_join)
    results = query.filter_by(dataset_id=data_set_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result.dataset_id == data_set_id
        assert isinstance(result.data_sets, list)
        assert len(result.data_sets) == 1
        for data_set in result.data_sets:
            assert type(data_set.name) is str
        assert isinstance(result.samples, list)
        assert len(result.samples) > 0
        # Don't need to iterate through every result.
        for sample in result.samples[0:2]:
            assert type(sample.id) is int
            assert type(sample.name) is str
        assert type(result.sample_id) is int
        assert repr(result) == '<DatasetToSample %r>' % data_set_id
    assert repr(results) == '[<DatasetToSample %r>]' % data_set_id


def test_DatasetToSample_no_relations(app, data_set_id):
    query = return_dataset_to_sample_query()
    results = query.filter_by(dataset_id=data_set_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert result.data_sets == []
        assert result.samples == []
        assert result.dataset_id == data_set_id
        assert type(result.sample_id) is int
