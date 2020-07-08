import json
import pytest
from tests import NoneType


def test_tags_query_with_data_set_related_and_feature(client, dataset, related, chosen_feature):
    query = """query Tags($dataSet: [String!]!, $related: [String!]!, $tag: [String!], $feature: [String!], $featureClass: [String!]) {
        tags(dataSet: $dataSet, related: $related, tag: $tag, feature: $feature, featureClass: $featureClass) {
            characteristics
            color
            display
            name
            sampleCount
            samples
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
        assert isinstance(data_set['samples'], list)


def test_tags_query_no_data_set_and_related(client, dataset, related):
    query = """query Tags(
        $dataSet: [String!]!
        $related: [String!]!
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
    ) {
        tags(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
        ) {
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
        assert not 'samples' in data_set


def test_tags_query_with_data_set_related_and_feature_class(client, dataset, related, feature_class):
    query = """query Tags(
        $dataSet: [String!]!
        $related: [String!]!
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
    ) {
        tags(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
        ) {
            characteristics
            color
            display
            name
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


def test_tags_query_with_data_set_related_and_tag(client, dataset, related, tag):
    query = """query Tags(
        $dataSet: [String!]!
        $related: [String!]!
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
    ) {
        tags(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
        ) {
            name
            sampleCount
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related],
                                    'tag': [tag]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['tags']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        assert data_set['name'] == tag
        assert type(data_set['sampleCount']) is int
