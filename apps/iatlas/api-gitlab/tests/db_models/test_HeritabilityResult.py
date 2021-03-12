import pytest
from tests import NoneType
from api.database import return_heritability_result_query


@pytest.fixture(scope='module')
def hr_feature(test_db):
    return 'Attractors_G_CD3E'


@pytest.fixture(scope='module')
def hr_feature_id(test_db, hr_feature):
    from api.db_models import Feature
    (id, ) = test_db.session.query(Feature.id).filter_by(
        name=hr_feature).one_or_none()
    return id


def test_HeritabilityResult_with_relations(app, data_set, data_set_id, hr_feature, hr_feature_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['data_set', 'feature']

    query = return_heritability_result_query(*relationships_to_join)
    results = query.filter_by(dataset_id=data_set_id).filter_by(
        feature_id=hr_feature_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        heritability_result_id = result.id
        string_representation = '<HeritabilityResult %r>' % heritability_result_id
        string_representation_list.append(string_representation)
        assert result.data_set.id == data_set_id
        assert result.data_set.name == data_set
        assert result.feature.id == hr_feature_id
        assert result.feature.name == hr_feature
        assert type(result.p_value) is float or NoneType
        assert type(result.variance) is float or NoneType
        assert type(result.se) is float or NoneType
        assert type(result.cluster) is str
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_HeritabilityResult_no_relations(app, data_set_id, hr_feature_id):
    query = return_heritability_result_query()
    results = query.filter_by(dataset_id=data_set_id).filter_by(
        feature_id=hr_feature_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        heritability_result_id = result.id
        string_representation = '<HeritabilityResult %r>' % heritability_result_id
        assert type(result.data_set) is NoneType
        assert type(result.feature) is NoneType
        assert result.dataset_id == data_set_id
        assert result.feature_id == hr_feature_id
        assert type(result.p_value) is float or NoneType
        assert type(result.variance) is float or NoneType
        assert type(result.se) is float or NoneType
        assert type(result.cluster) is str
        assert repr(result) == string_representation
