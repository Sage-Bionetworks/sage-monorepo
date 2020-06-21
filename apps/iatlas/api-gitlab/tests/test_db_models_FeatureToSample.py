import pytest
from tests import app, NoneType
from flaskr.database import return_feature_to_sample_query

feature_id = 1


def test_FeatureToSample_with_relations(app):
    app()
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['features', 'samples']

    query = return_feature_to_sample_query()
    results = query.filter_by(feature_id=feature_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<FeatureToSample %r>' % feature_id
        string_representation_list.append(string_representation)
        if type(result.features) is not NoneType:
            assert isinstance(result.features, list)
            # Don't need to iterate through every result.
            for feature in result.features[0:2]:
                assert type(feature.name) is str
        if type(result.samples) is not NoneType:
            assert isinstance(result.samples, list)
            # Don't need to iterate through every result.
            for sample in result.samples[0:2]:
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

    query = return_feature_to_sample_query()
    results = query.filter_by(feature_id=feature_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.features == []
        assert result.samples == []
        assert result.feature_id == feature_id
        assert type(result.sample_id) is int
        assert type(result.value) is float or NoneType
        assert type(result.inf_value) is float or NoneType
