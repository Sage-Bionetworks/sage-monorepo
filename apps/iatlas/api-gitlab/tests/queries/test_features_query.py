import json
import pytest
from tests import NoneType
from api.enums import unit_enum
from api.database import return_feature_query


def test_features_query_with_feature(client, chosen_feature):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!], $featureClass: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            class
            display
            methodTag
            name
            order
            sample
            unit
            value
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    for feature in features:
        assert type(feature['class']) is str
        assert type(feature['display']) is str or NoneType
        assert type(feature['methodTag']) is str or NoneType
        assert feature['name'] == chosen_feature
        assert type(feature['order']) is int or NoneType
        assert type(feature["sample"]) is str or NoneType
        assert feature['unit'] in unit_enum.enums or type(
            feature['unit']) is NoneType
        assert type(feature['value']) is str or float or NoneType


def test_features_query_with_feature_no_sample_or_value(client, data_set, related, chosen_feature):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!], $featureClass: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) == 1


def test_features_query_no_feature(client, data_set, related):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!], $featureClass: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            class
            display
            methodTag
            name
            order
            unit
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    # Don't need to iterate through every result.
    for feature in features[0:2]:
        assert type(feature['class']) is str
        assert type(feature['display']) is str or NoneType
        assert type(feature['methodTag']) is str or NoneType
        assert type(feature['name']) is str
        assert type(feature['order']) is int or NoneType
        assert feature['unit'] in unit_enum.enums or type(
            feature['unit']) is NoneType


def test_features_query_no_relations(client, data_set, related, chosen_feature):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!], $featureClass: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            display
            name
            order
            unit
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) == 1
    for feature in features:
        assert 'class' not in feature
        assert type(feature['display']) is str or NoneType
        assert 'methodTag' not in feature
        assert feature['name'] == chosen_feature
        assert type(feature['order']) is int or NoneType
        assert type(
            feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_features_query_no_dataSet(client, related, chosen_feature):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!], $featureClass: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            display
            name
            order
            unit
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) == 1
    for feature in features:
        assert 'class' not in feature
        assert type(feature['display']) is str or NoneType
        assert 'methodTag' not in feature
        assert feature['name'] == chosen_feature
        assert type(feature['order']) is int or NoneType
        assert type(
            feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_features_query_no_related(client, data_set, chosen_feature):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!], $featureClass: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            display
            name
            order
            unit
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) == 1
    for feature in features:
        assert 'class' not in feature
        assert type(feature['display']) is str or NoneType
        assert 'methodTag' not in feature
        assert feature['name'] == chosen_feature
        assert type(feature['order']) is int or NoneType
        assert type(
            feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_features_query_no_args(client):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!], $featureClass: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            display
            name
            order
            unit
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    # Get the total number of features in the database.
    feature_count = return_feature_query('id').count()

    assert isinstance(features, list)
    assert len(features) == feature_count


def test_features_query_with_feature_class(client, data_set, related, chosen_feature, feature_class):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!], $featureClass: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            class
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature],
                                    'featureClass': [feature_class]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    for feature in features:
        assert feature['class'] == feature_class
        assert feature['name'] == chosen_feature
