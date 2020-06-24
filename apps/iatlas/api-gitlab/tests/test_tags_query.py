import json
import pytest
from tests import client, NoneType

dataset = 'TCGA'
related = 'Immune_Subtype'
chosen_feature = 'Neutrophils_Aggregate2'


def test_tags_query_with_feature(client):
    query = """query Tags($dataSet: [String!]!, $related: [String!]!, $feature: [String!]) {
        tags(dataSet: $dataSet, related: $related, feature: $feature) {
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


def test_tags_query_no_feature(client):
    query = """query Tags($dataSet: [String!]!, $related: [String!]!, $feature: [String!]) {
        tags(dataSet: $dataSet, related: $related, feature: $feature) {
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
