import pytest
from tests import app, NoneType
from flaskr.db_models import FeatureToSample


def test_FeatureToSample(app):
    app()
    feature_id = 1
    string_representation_list = []
    separator = ', '

    results = FeatureToSample.query.filter_by(feature_id=feature_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<FeatureToSample %r>' % feature_id
        string_representation_list.append(string_representation)
        assert result.feature_id == feature_id
        assert type(result.sample_id) is int
        assert type(result.value) is float or NoneType
        assert type(result.inf_value) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
