import pytest
from tests import app, NoneType
from flaskr.database import return_feature_to_sample_query


def test_FeatureToSample_with_relations(app):
    app()
    feature_id = 1
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['features', 'samples']

    query = return_feature_to_sample_query()
    results = query.filter_by(feature_id=feature_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<FeatureToSample %r>' % feature_id
        string_representation_list.append(string_representation)
        if type(result.features) is not NoneType:
            assert isinstance(result.features, list)
            for feature in result.features:
                assert type(feature.name) is str
        if type(result.samples) is not NoneType:
            assert isinstance(result.samples, list)
            for sample in result.samples:
                assert type(sample.name) is str
        assert result.feature_id == feature_id
        assert type(result.sample_id) is int
        assert type(result.value) is float or NoneType
        assert type(result.inf_value) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_FeatureToSample_no_relations(app):
    app()
    feature_id = 1
    string_representation_list = []
    separator = ', '

    query = return_feature_to_sample_query()
    results = query.filter_by(feature_id=feature_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<FeatureToSample %r>' % feature_id
        string_representation_list.append(string_representation)
        assert result.feature_id == feature_id
        assert type(result.sample_id) is int
        assert type(result.value) is float or NoneType
        assert type(result.inf_value) is float or NoneType
        assert repr(result) == string_representation
