import pytest
from tests import NoneType
from api.database import return_tag_query


@pytest.fixture(scope='module')
def tag_name():
    return 'ACC'

@pytest.fixture(scope='module')
def tag_name_with_copy_number_results():
    return 'C1'


@pytest.fixture(scope='module')
def tag_with_publication():
    return 'AML_1'


@pytest.fixture(scope='module')
def tag_with_order():
    return 'actla4_prior_ici_rx'


@pytest.fixture(scope='module')
def tag_order():
    return 1


def test_Tag_no_relations(app, tag_name):
    query = return_tag_query()
    result = query.filter_by(name=tag_name).one_or_none()

    assert result
    assert result.related_tags == []
    assert result.samples == []
    assert result.tags == []
    assert result.copy_number_results == []
    assert result.driver_results == []
    assert type(result.id) is str
    assert result.name == tag_name
    assert type(result.description) is str
    assert type(result.color) is str or NoneType
    assert type(result.long_display) is str or NoneType
    assert type(result.short_display) is str or NoneType
    assert result.tag_type == 'group'
    assert type(result.order) is int or NoneType


def test_Tag_with_order(app, tag_with_order, tag_order):
    query = return_tag_query()
    result = query.filter_by(name=tag_with_order).one_or_none()
    assert result.name == tag_with_order
    assert result.tag_type == 'group'
    assert result.order == tag_order


def test_Tag_with_copy_number_results(app, tag_name_with_copy_number_results):
    query = return_tag_query('copy_number_results')
    result = query.filter_by(name=tag_name_with_copy_number_results).one_or_none()

    assert result
    assert isinstance(result.copy_number_results, list)
    assert len(result.copy_number_results) > 0
    # Don't need to iterate through every result.
    for copy_number_result in result.copy_number_results[0:2]:
        assert copy_number_result.tag_id == result.id


def test_Tag_with_data_sets(app, related):
    query = return_tag_query('data_sets')
    result = query.filter_by(name=related).one_or_none()

    assert result
    assert isinstance(result.data_sets, list)
    assert len(result.data_sets) > 0
    # Don't need to iterate through every result.
    for data_set in result.data_sets[0:2]:
        assert type(data_set.name) is str


def test_Tag_with_dataset_tag_assoc(app, related):
    query = return_tag_query('dataset_tag_assoc')
    result = query.filter_by(name=related).one_or_none()

    assert result
    assert isinstance(result.dataset_tag_assoc, list)
    assert len(result.dataset_tag_assoc) > 0
    # Don't need to iterate through every result.
    for dataset_tag_rel in result.dataset_tag_assoc[0:2]:
        assert dataset_tag_rel.tag_id == result.id


def test_Tag_with_driver_results(app, tag_name):
    query = return_tag_query('driver_results')
    result = query.filter_by(name=tag_name).one_or_none()

    assert result
    assert isinstance(result.driver_results, list)
    assert len(result.driver_results) > 0
    # Don't need to iterate through every result.
    for driver_result in result.driver_results[0:2]:
        assert driver_result.tag_id == result.id

def test_Tag_with_publications(app, tag_with_publication):
    query = return_tag_query('publications')
    result = query.filter_by(name=tag_with_publication).one_or_none()

    assert result
    assert len(result.publications) > 0
    # Don't need to iterate through every result.
    for publication in result.publications[0:2]:
        assert type(publication.title) is str


def test_Tag_with_related_tags(app, tag_name):
    query = return_tag_query('related_tags')
    result = query.filter_by(name=tag_name).one_or_none()

    assert result
    assert isinstance(result.related_tags, list)
    assert len(result.related_tags) > 0
    # Don't need to iterate through every result.
    for related_tag in result.related_tags[0:2]:
        assert type(related_tag.name) is str
    assert result.name == tag_name


def test_Tag_with_samples(app, tag_name):
    query = return_tag_query('samples')
    result = query.filter_by(name=tag_name).one_or_none()

    assert result
    assert isinstance(result.samples, list)
    assert len(result.samples) > 0
    # Don't need to iterate through every result.
    for sample in result.samples[0:2]:
        assert type(sample.name) is str
    assert result.name == tag_name
    assert type(result.description) is str
    assert type(result.color) is str or NoneType
    assert type(result.long_display) is str or NoneType
    assert type(result.short_display) is str or NoneType
    assert repr(result) == '<Tag %r>' % tag_name


def test_Tag_with_tags(app, related):
    query = return_tag_query('tags')
    result = query.filter_by(name=related).one_or_none()

    assert result
    assert isinstance(result.tags, list)
    assert len(result.tags) > 0
    # Don't need to iterate through every result.
    for tag in result.tags[0:2]:
        assert type(tag.name) is str


def test_Tag_with_tag_publication_assoc(app, tag_with_publication):
    query = return_tag_query('tag_publication_assoc')
    result = query.filter_by(name=tag_with_publication).one_or_none()

    assert result
    assert isinstance(result.tag_publication_assoc, list)
    assert len(result.tag_publication_assoc) > 0
    # Don't need to iterate through every result.
    for tag_publication_rel in result.tag_publication_assoc[0:2]:
        assert tag_publication_rel.tag_id == result.id
