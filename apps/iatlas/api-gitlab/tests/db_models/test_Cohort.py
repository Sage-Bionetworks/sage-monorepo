import pytest
from tests import NoneType
from decimal import Decimal
from api.database import return_cohort_query
import logging


@pytest.fixture(scope='module')
def tag_cohort_name():
    return 'tcga_immune_subtype'


@pytest.fixture(scope='module')
def tag_cohort_id(test_db, tag_cohort_name):
    from api.db_models import Cohort
    (id, ) = test_db.session.query(Cohort.id).filter_by(
        name=tag_cohort_name).one_or_none()
    return id


@pytest.fixture(scope='module')
def cohort_data_set():
    return "TCGA"


@pytest.fixture(scope='module')
def cohort_data_set_id(test_db, cohort_data_set):
    from api.db_models import Dataset
    (id, ) = test_db.session.query(Dataset.id).filter_by(
        name=cohort_data_set).one_or_none()
    return id


@pytest.fixture(scope='module')
def parent_tag_name():
    return "immune_subtype"


@pytest.fixture(scope='module')
def parent_tag_id(test_db, parent_tag_name):
    from api.db_models import Tag
    (id, ) = test_db.session.query(Tag.id).filter_by(
        name=parent_tag_name).one_or_none()
    return id


def test_cohort_no_relationships(app):
    string_representation_list = []
    separator = ', '

    query = return_cohort_query()
    results = query.all()

    assert isinstance(results, list)
    assert len(results) == 9
    for result in results:
        string_representation = '<Cohort %r>' % result.name
        string_representation_list.append(string_representation)
        assert repr(result) == string_representation
        assert type(result.name) is str
        assert type(result.clinical) is str or NoneType
        assert type(result.tag_id) is int or NoneType
        assert type(result.dataset_id) is int
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_cohort_relationships(app):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['data_set', 'tag',
                             'sample', 'feature', 'gene', 'mutation']

    query = return_cohort_query(*relationships_to_join)
    results = query.all()

    assert isinstance(results, list)
    assert len(results) == 9
    logger = logging.getLogger('test cohort')
    for result in results:
        string_representation = '<Cohort %r>' % result.name
        string_representation_list.append(string_representation)
        assert repr(result) == string_representation
        assert type(result.name) is str
        assert type(result.clinical) is str or NoneType
        assert type(result.tag_id) is int or NoneType
        assert type(result.dataset_id) is int
        assert type(result.data_set.name) is str
        assert type(result.data_set.id) is int
        logger.info(result.feature)
        logger.info(result.gene)
        logger.info(result.sample)
        logger.info(result.mutation)
        assert 1 == 2
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_unique_cohort(app, data_set_id, parent_tag_id, parent_tag_name, cohort_data_set, cohort_data_set_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['data_set', 'tag',
                             'sample', 'feature', 'gene', 'mutation']

    query = return_cohort_query(*relationships_to_join)
    results = query.filter_by(tag_id=parent_tag_id).filter_by(
        dataset_id=data_set_id).all()
    logger = logging.getLogger('test cohort')
    logger.info(results)
    logger.info(parent_tag_name)

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        string_representation = '<Cohort %r>' % result.name
        string_representation_list.append(string_representation)
        assert repr(result) == string_representation
        assert type(result.name) is str
        assert type(result.clinical) is str or NoneType
        assert type(result.tag_id) is int or NoneType
        assert type(result.dataset_id) is int
        assert type(result.data_set.name) is str
        assert type(result.data_set.id) is int
        logger.info(result.feature)
        logger.info(result.gene)
        logger.info(result.sample)
        logger.info(result.mutation)
        assert 1 == 2
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
