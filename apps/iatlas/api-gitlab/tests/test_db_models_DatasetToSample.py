import pytest
from flaskr.database import return_dataset_to_sample_query


def test_DatasetToSample_with_relations(app, dataset_id):
    app()
    relationships_to_join = ['datasets', 'samples']

    query = return_dataset_to_sample_query(*relationships_to_join)
    results = query.filter_by(dataset_id=dataset_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.dataset_id == dataset_id
        assert isinstance(result.datasets, list)
        # Don't need to iterate through every result.
        for dataset in result.datasets[0:2]:
            assert type(dataset.name) is str
        assert isinstance(result.samples, list)
        # Don't need to iterate through every result.
        for sample in result.samples[0:2]:
            assert type(sample.id) is int
            assert type(sample.name) is str
        assert type(result.sample_id) is int
        assert repr(result) == '<DatasetToSample %r>' % dataset_id
    assert repr(results) == '[<DatasetToSample %r>]' % dataset_id


def test_DatasetToSample_no_relations(app, dataset_id):
    app()

    query = return_dataset_to_sample_query()
    results = query.filter_by(dataset_id=dataset_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.datasets == []
        assert result.samples == []
        assert result.dataset_id == dataset_id
        assert type(result.sample_id) is int
