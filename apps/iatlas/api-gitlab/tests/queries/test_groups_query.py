import json
import pytest
from tests import NoneType


def test_groups_query_no_args(client):
    query = """query Groups($dataSet: [String!], $related: [String!]) {
        groups(dataSet: $dataSet, related: $related) {
            dataSet
            groups { name }
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    groups = json_data['data']['groups']

    assert isinstance(groups, list)
    assert len(groups) > 0
    for group in groups:
        data_set_groups = groups['groups']
        assert type(groups['dataSet']) is str
        assert isinstance(data_set_groups, list)
        assert len(data_set_groups) > 0
        for group in groups:
            features = group['features']
            assert type(group['name']) is str


def test_groups_query_with_passed_data_set(client, dataset):
    query = """query Groups($dataSet: [String!], $related: [String!]) {
        groups(dataSet: $dataSet, related: $related) {
            dataSet
            groups {
                characteristics
                color
                display
                features { name }
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [dataset]}})
    json_data = json.loads(response.data)
    groups = json_data['data']['groups']

    assert isinstance(groups, list)
    assert len(groups) == 1
    for group in groups:
        data_set_groups = groups['groups']
        assert groups['dataSet'] == dataset
        assert isinstance(data_set_groups, list)
        assert len(data_set_groups) > 0
        for group in groups:
            features = group['features']
            assert type(group['name']) is str
            assert type(group['characteristics']) is str or NoneType
            assert type(group['color']) is str or NoneType
            assert type(group['display']) is str or NoneType
            assert isinstance(features, list)
            if features:
                for feature in features:
                    assert type(feature['name']) is str


def test_groups_query_with_passed_group(client, related):
    query = """query Groups($dataSet: [String!], $related: [String!]) {
        groups(dataSet: $dataSet, related: $related) {
            dataSet
            groups { name }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'related': [related]}})
    json_data = json.loads(response.data)
    groups = json_data['data']['groups']

    assert isinstance(groups, list)
    assert len(groups) > 0
    for group in groups:
        data_set_groups = groups['groups']
        assert type(groups['dataSet']) is str
        assert isinstance(data_set_groups, list)
        assert len(data_set_groups) == 1
        for group in groups:
            assert group['name'] == related


def test_groups_query_with_passed_data_set_and_related(client, dataset, related):
    query = """query Groups($dataSet: [String!], $related: [String!]) {
        groups(dataSet: $dataSet, related: $related) {
            dataSet
            groups { name }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [dataset], 'related': [related]}})
    json_data = json.loads(response.data)
    groups = json_data['data']['groups']

    assert isinstance(groups, list)
    assert len(groups) == 1
    for group in groups:
        data_set_groups = groups['groups']
        assert groups['dataSet'] == dataset
        assert isinstance(data_set_groups, list)
        assert len(data_set_groups) == 1
        for group in groups:
            assert group['name'] == related
