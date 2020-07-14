import json
import pytest
from tests import NoneType


def test_tags_query_with_data_set_related_and_feature(client, data_set, related, chosen_feature):
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
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    results = json_data['data']['tags']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['display']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['sampleCount']) is int
        assert isinstance(result['samples'], list)


def test_tags_query_no_data_set_and_related(client, data_set, related):
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
                      'variables': {'dataSet': [data_set],
                                    'related': [related]}})
    json_data = json.loads(response.data)
    results = json_data['data']['tags']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['display']) is str or NoneType
        assert type(result['name']) is str
        assert not 'sampleCount' in result
        assert not 'samples' in result


def test_tags_query_with_data_set_related_and_feature_class(client, data_set, related, feature_class):
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
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'featureClass': [feature_class]}})
    json_data = json.loads(response.data)
    results = json_data['data']['tags']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['characteristics']) is str or NoneType
        assert type(result['color']) is str or NoneType
        assert type(result['display']) is str or NoneType
        assert type(result['name']) is str


def test_tags_query_with_data_set_related_and_tag(client, data_set, related, tag):
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
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'tag': [tag]}})
    json_data = json.loads(response.data)
    results = json_data['data']['tags']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['name'] == tag
        assert type(result['sampleCount']) is int
