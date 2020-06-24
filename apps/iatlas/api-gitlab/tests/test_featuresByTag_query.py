import json
import pytest
from tests import client, NoneType
from flaskr.enums import unit_enum
from flaskr.database import return_feature_class_query

dataset = 'TCGA'
related = 'Immune_Subtype'
chosen_feature = 'Neutrophils_Aggregate2'


def test_featuresByTag_query_with_feature(client):
    query = """query FeaturesByTag($dataSet: [String!]!, $related: [String!]!, $feature: [String!], $featureClass: [String!]) {
        featuresByTag(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            tag
            characteristics
            display
            features {
                class
                display
                methodTag
                name
                order
                sample
                unit
                value
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related],
                                    'feature': [related]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['featuresByTag']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        assert type(data_set['tag']) is str
        assert type(data_set['characteristics']) is str or NoneType
        assert type(data_set['display']) is str or NoneType
        assert isinstance(data_set['features'], list)
        # Don't need to iterate through every result.
        for feature in data_set['features'][0:2]:
            assert type(feature['class']) is str or NoneType
            assert type(feature['display']) is str or NoneType
            assert type(feature['methodTag']) is str or NoneType
            assert feature['name'] == related
            assert type(feature['order']) is int or NoneType
            assert type(feature["sample"]) is str or NoneType
            assert type(
                feature['unit']) is NoneType or feature['unit'] in unit_enum.enums
            assert type(feature['value']) is float or NoneType


def test_featuresByTag_query_no_feature(client):
    query = """query FeaturesByTag($dataSet: [String!]!, $related: [String!]!, $feature: [String!], $featureClass: [String!]) {
        featuresByTag(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            tag
            characteristics
            display
            features {
                class
                display
                methodTag
                name
                order
                unit
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['featuresByTag']

    assert isinstance(data_sets, list)
    # Don't need to iterate through every result.
    for data_set in data_sets[0:2]:
        assert type(data_set['tag']) is str
        assert type(data_set['characteristics']) is str or NoneType
        assert type(data_set['display']) is str or NoneType
        assert isinstance(data_set['features'], list)
        # Don't need to iterate through every result.
        for feature in data_set['features'][0:2]:
            assert type(feature['class']) is str or NoneType
            assert type(feature['display']) is str or NoneType
            assert type(feature['methodTag']) is str or NoneType
            assert type(feature['name']) is str
            assert type(feature['order']) is int or NoneType
            assert type(
                feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_featuresByTag_query_no_relations(client):
    query = """query FeaturesByTag($dataSet: [String!]!, $related: [String!]!, $feature: [String!], $featureClass: [String!]) {
        featuresByTag(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            tag
            characteristics
            display
            features {
                display
                name
                order
                unit
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related],
                                    'feature': [related]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['featuresByTag']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        assert type(data_set['tag']) is str
        assert type(data_set['characteristics']) is str or NoneType
        assert type(data_set['display']) is str or NoneType
        assert isinstance(data_set['features'], list)
        # Don't need to iterate through every result.
        for feature in data_set['features'][0:2]:
            assert 'class' not in feature
            assert type(feature['display']) is str or NoneType
            assert 'methodTag' not in feature
            assert feature['name'] == related
            assert type(feature['order']) is int or NoneType
            assert type(
                feature['unit']) is NoneType or feature['unit'] in unit_enum.enums
