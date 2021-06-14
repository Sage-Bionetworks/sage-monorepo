import pytest
from tests import NoneType
from api.database import return_cohort_to_sample_query


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
def clinical_cohort_name():
    return 'tcga_race'


@pytest.fixture(scope='module')
def clinical_cohort_id(test_db, clinical_cohort_name):
    from api.db_models import Cohort
    (id, ) = test_db.session.query(Cohort.id).filter_by(
        name=clinical_cohort_name).one_or_none()
    return id


def test_CohortToSample_no_relations():
    query = return_cohort_to_sample_query()
    results = query.limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.sample_id) is int
        assert type(result.cohort_id) is int
        assert type(result.tag_id) is float or NoneType
        assert type(result.clinical_value) is str or NoneType


def test_CohortToSample_with_tag_cohort(tag_cohort_name, tag_cohort_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['cohort', 'sample']

    query = return_cohort_to_sample_query(*relationships_to_join)
    results = query.filter_by(cohort_id=tag_cohort_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        id = result.id
        string_representation = '<CohortToSample %r>' % id
        string_representation_list.append(string_representation)
        assert type(result.sample_id) is int
        assert result.cohort_id == tag_cohort_id
        assert type(result.tag_id) is int
        assert type(result.clinical_value) is NoneType
        assert result.cohort.name == tag_cohort_name
        assert type(result.sample.name) is str
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_CohortToSample_with_clinical_cohort(clinical_cohort_name, clinical_cohort_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['cohort', 'sample']

    query = return_cohort_to_sample_query(*relationships_to_join)
    results = query.filter_by(cohort_id=clinical_cohort_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        id = result.id
        string_representation = '<CohortToSample %r>' % id
        string_representation_list.append(string_representation)
        assert type(result.sample_id) is int
        assert result.cohort_id == clinical_cohort_id
        assert type(result.tag_id) is NoneType
        assert type(result.clinical_value) is str
        assert result.cohort.name == clinical_cohort_name
        assert type(result.sample.name) is str
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
