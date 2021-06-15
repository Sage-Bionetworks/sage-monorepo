import pytest
from tests import NoneType
from decimal import Decimal
from api.database import return_feature_to_sample_query


@pytest.fixture(scope='module')
def fs_feature(test_db):
    return 'AS'


@pytest.fixture(scope='module')
def fs_feature_id(test_db, fs_feature):
    from api.db_models import Feature
    (id, ) = test_db.session.query(Feature.id).filter_by(
        name=fs_feature).one_or_none()
    return id


def test_FeatureToSample_with_relations(app, fs_feature, fs_feature_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['features', 'samples']

    query = return_feature_to_sample_query(*relationships_to_join)
    results = query.filter_by(feature_id=fs_feature_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<FeatureToSample %r>' % fs_feature_id
        string_representation_list.append(string_representation)
        assert isinstance(result.features, list)
        assert len(result.features) > 0
        # Don't need to iterate through every result.
        for feature in result.features[0:2]:
            assert feature.id == fs_feature_id
            assert feature.name == fs_feature
        if result.samples:
            assert isinstance(result.samples, list)
            # Don't need to iterate through every result.
            for sample in result.samples[0:2]:
                assert type(sample.name) is str
        assert type(result.id) is int
        assert result.feature_id == fs_feature_id
        assert type(result.sample_id) is int
        assert isinstance(result.value, Decimal)
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_FeatureToSample_no_relations(app, fs_feature_id):
    query = return_feature_to_sample_query()
    results = query.filter_by(feature_id=fs_feature_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.features == []
        assert result.samples == []
        assert result.feature_id == fs_feature_id
        assert type(result.id) is int
        assert type(result.sample_id) is int
        assert isinstance(result.value, Decimal)
