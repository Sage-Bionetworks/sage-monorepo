import pytest
from tests import NoneType
from flaskr.database import return_dataset_query


def test_dataset_with_samples(app, dataset):
    app()

    query = return_dataset_query('samples')
    result = query.filter_by(name=dataset).first()

    if result.samples:
        assert isinstance(result.samples, list)
        # Don't need to iterate through every result.
        for sample in result.samples[0:2]:
            assert type(sample.name) is str
    assert result.name == dataset
    assert type(result.display) is str or NoneType
    assert repr(result) == '<Dataset %r>' % dataset


def test_dataset_with_dataset_sample_assoc(app, dataset):
    app()

    query = return_dataset_query('dataset_sample_assoc')
    result = query.filter_by(name=dataset).first()

    if result.dataset_sample_assoc:
        assert isinstance(result.dataset_sample_assoc, list)
        # Don't need to iterate through every result.
        for dataset_sample_rel in result.dataset_sample_assoc[0:2]:
            assert dataset_sample_rel.dataset_id == result.id


def test_dataset_no_relations(app, dataset):
    app()

    query = return_dataset_query()
    result = query.filter_by(name=dataset).first()

    assert result.dataset_sample_assoc == []
    assert result.samples == []
    assert type(result.id) is int
    assert result.name == dataset
    assert type(result.display) is str or NoneType
