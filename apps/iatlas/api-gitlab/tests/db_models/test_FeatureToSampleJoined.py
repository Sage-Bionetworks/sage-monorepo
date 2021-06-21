import pytest
from tests import NoneType
from decimal import Decimal
from api.database import return_feature_to_sample_joined_query


def test_FeatureToSampleJoined_no_relations(app, chosen_feature, chosen_feature_id, feature_class):
    query = return_feature_to_sample_joined_query()
    results = query.filter_by(feature_name=chosen_feature).limit(3).all()
    string_representation_list = []
    separator = ', '
    assert isinstance(results, list)
    for result in results:
        assert type(result.id) is int
        assert isinstance(result.value, Decimal)

        assert result.feature_name == chosen_feature
        assert result.class_name == feature_class
        assert result.feature_id == chosen_feature_id
        assert type(result.feature_order) is int or NoneType

        assert type(result.sample_id) is int
        assert type(result.sample_name) is str

        string_representation = '<FeatureToSampleJoined %r>' % result.id
        assert repr(result) == string_representation
        string_representation_list.append(string_representation)
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_FeatureToSampleJoined_with_relations(app, chosen_feature, chosen_feature_id, feature_class):
    relationships_to_join = ['features', 'samples']
    query = return_feature_to_sample_joined_query(*relationships_to_join)
    results = query.filter_by(feature_name=chosen_feature).limit(3).all()
    string_representation_list = []
    separator = ', '
    assert isinstance(results, list)
    for result in results:
        import logging
        logger = logging.getLogger("test")
        logger.info(result)
        assert type(result.id) is int
        assert isinstance(result.value, Decimal)

        assert result.feature_name == chosen_feature
        assert result.class_name == feature_class
        assert result.feature_id == chosen_feature_id
        assert type(result.feature_order) is int or NoneType

        assert type(result.sample_id) is int
        assert type(result.sample_name) is str

        string_representation = '<FeatureToSampleJoined %r>' % result.id
        assert repr(result) == string_representation
        string_representation_list.append(string_representation)
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
