import json
import pytest
from tests import NoneType


def test_tags_query_with_feature(client, dataset, related, chosen_feature):
    query = """query Tags($dataSet: [String!]!, $related: [String!]!, $feature: [String!], $featureClass: [String!]) {
        tags(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            characteristics
            color
            display
            name
            sampleCount
            sampleIds
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['tags']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        assert type(data_set['characteristics']) is str or NoneType
        assert type(data_set['color']) is str or NoneType
        assert type(data_set['display']) is str or NoneType
        assert type(data_set['name']) is str
        assert type(data_set['sampleCount']) is int
        assert isinstance(data_set['sampleIds'], list)


def test_tags_query_no_feature(client, dataset, related):
    query = """query Tags($dataSet: [String!]!, $related: [String!]!, $feature: [String!], $featureClass: [String!]) {
        tags(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            characteristics
            color
            display
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['tags']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        assert type(data_set['characteristics']) is str or NoneType
        assert type(data_set['color']) is str or NoneType
        assert type(data_set['display']) is str or NoneType
        assert type(data_set['name']) is str
        assert not 'sampleCount' in data_set
        assert not 'sampleIds' in data_set


def test_tags_query_with_feature_class(client, dataset, related, feature_class):
    query = """query Tags($dataSet: [String!]!, $related: [String!]!, $feature: [String!], $featureClass: [String!]) {
        tags(dataSet: $dataSet, related: $related, feature: $feature, featureClass: $featureClass) {
            characteristics
            color
            display
            name
            sampleCount
            sampleIds
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related],
                                    'featureClass': [feature_class]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['tags']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        assert type(data_set['characteristics']) is str or NoneType
        assert type(data_set['color']) is str or NoneType
        assert type(data_set['display']) is str or NoneType
        assert type(data_set['name']) is str
        assert type(data_set['sampleCount']) is int
        assert isinstance(data_set['sampleIds'], list)
