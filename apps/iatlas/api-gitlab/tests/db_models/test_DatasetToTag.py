import pytest
from tests import NoneType
from api.database import return_dataset_to_tag_query


def test_DatasetToTag_with_relations(app, dataset, dataset_id):
    relationships_to_join = ['datasets', 'tags']

    query = return_dataset_to_tag_query(*relationships_to_join)
    results = query.filter_by(dataset_id=dataset_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        datasets = result.datasets
        tags = result.tags
        assert result.dataset_id == dataset_id
        assert isinstance(datasets, list)
        assert len(datasets) == 1
        for dataset in datasets:
            assert dataset.name == dataset
        assert isinstance(tags, list)
        assert len(tags) > 0
        for tag in tags:
            assert type(tag.name) is str
        assert repr(result) == '<DatasetToTag %r>' % dataset_id
    assert repr(results) == '[<DatasetToTag %r>]' % dataset_id


def test_DatasetToTag_no_relations(app, dataset_id):
    query = return_dataset_to_tag_query()
    results = query.filter_by(dataset_id=dataset_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert result.datasets == []
        assert result.tags == []
        assert result.dataset_id == dataset_id
        assert type(result.tag_id) is int
