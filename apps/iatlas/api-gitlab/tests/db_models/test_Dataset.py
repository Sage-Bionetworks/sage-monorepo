import pytest
from tests import NoneType
from api.database import return_dataset_query


def test_dataset_with_samples(app, data_set):
    query = return_dataset_query('samples')
    result = query.filter_by(name=data_set).first()

    assert isinstance(result.samples, list)
    assert len(result.samples) > 0
    # Don't need to iterate through every result.
    for sample in result.samples[0:2]:
        assert type(sample.name) is str
    assert result.name == data_set
    assert type(result.display) is str or NoneType
    assert repr(result) == '<Dataset %r>' % data_set


def test_dataset_with_data_set_sample_assoc(app, data_set):
    query = return_dataset_query('dataset_sample_assoc')
    result = query.filter_by(name=data_set).first()

    assert isinstance(result.dataset_sample_assoc, list)
    assert len(result.dataset_sample_assoc) > 0
    # Don't need to iterate through every result.
    for dataset_sample_rel in result.dataset_sample_assoc[0:2]:
        assert dataset_sample_rel.dataset_id == result.id


def test_dataset_with_tags(app, data_set):
    query = return_dataset_query('tags')
    result = query.filter_by(name=data_set).first()

    assert isinstance(result.tags, list)
    assert len(result.tags) > 0
    # Don't need to iterate through every result.
    for tag in result.tags[0:2]:
        assert type(tag.name) is str
    assert result.name == data_set
    assert type(result.display) is str or NoneType
    assert repr(result) == '<Dataset %r>' % data_set


def test_dataset_with_dataset_sample_assoc(app, data_set):
    query = return_dataset_query('dataset_tag_assoc')
    result = query.filter_by(name=data_set).first()

    assert isinstance(result.dataset_tag_assoc, list)
    assert len(result.dataset_tag_assoc) > 0
    # Don't need to iterate through every result.
    for dataset_tag_rel in result.dataset_tag_assoc[0:2]:
        assert dataset_tag_rel.dataset_id == result.id


def test_dataset_no_relations(app, data_set):
    query = return_dataset_query()
    result = query.filter_by(name=data_set).first()

    assert result.dataset_sample_assoc == []
    assert result.samples == []
    assert type(result.id) is int
    assert result.name == data_set
    assert type(result.display) is str or NoneType
