import json
import pytest
from tests import NoneType


def test_related_query_no_args(client):
    query = """query Related($dataSet: [String!], $related: [String!]) {
        related(dataSet: $dataSet, related: $related) {
            dataSet
            display
            related {
                name
                display
            }
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['related']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        related_list = result['related']
        assert type(result['dataSet']) is str
        assert type(result['display']) is str
        assert isinstance(related_list, list)
        assert len(related_list) > 0
        for current_related in related_list:
            assert type(current_related['name']) is str
            assert type(current_related['display']) is str or NoneType


def test_related_query_passed_data_set(client, data_set):
    query = """query Related($dataSet: [String!], $related: [String!]) {
        related(dataSet: $dataSet, related: $related) {
            dataSet
            display
            related { color }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [data_set]}})
    json_data = json.loads(response.data)
    results = json_data['data']['related']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        related_list = result['related']
        assert result['dataSet'] == data_set
        assert type(result['display']) is str
        assert isinstance(related_list, list)
        assert len(related_list) > 0
        for current_related in related_list:
            assert type(current_related['color']) is str or NoneType


def test_data_sets_query_passed_related(client, related):
    query = """query Related($dataSet: [String!], $related: [String!]) {
        related(dataSet: $dataSet, related: $related) {
            dataSet
            display
            related {
                name
                characteristics
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'related': [related]}})
    json_data = json.loads(response.data)
    results = json_data['data']['related']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        related_list = result['related']
        assert type(result['dataSet']) is str
        assert type(result['display']) is str
        assert isinstance(related_list, list)
        assert len(related_list) == 1
        for current_related in related_list:
            assert current_related['name'] == related
            assert type(current_related['characteristics']) is str or NoneType


def test_data_sets_query_passed_data_set_passed_sample(client, data_set, related):
    query = """query Related($dataSet: [String!], $related: [String!]) {
        related(dataSet: $dataSet, related: $related) {
            dataSet
            display
            related { name }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [data_set], 'related': [related]}})
    json_data = json.loads(response.data)
    results = json_data['data']['related']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        related_list = result['related']
        assert result['dataSet'] == data_set
        assert type(result['display']) is str
        assert isinstance(related_list, list)
        assert len(related_list) == 1
        for current_related in related_list:
            assert current_related['name'] == related
