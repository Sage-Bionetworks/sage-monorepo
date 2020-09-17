import json
import pytest
from tests import NoneType


@pytest.fixture(scope='module')
def network():
    return 'extracellular_network'


def test_edges_query_with_passed_data_set(client, data_set):
    query = """query Edges($dataSet: [String!], $related: [String!], $network: [String!], $page: Int) {
        edges(dataSet: $dataSet, related: $related, network: $network, page: $page) {
            items { name }
            page
            pages
            total
        }
    }"""
    response = client.post('/api', json={'query': query,
                                         'variables': {'dataSet': [data_set], 'page': 2}})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

    assert page['page'] == 2
    assert type(page['pages']) is int
    assert type(page['total']) is int
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str


def test_edges_query_with_passed_related(client, related):
    query = """query Edges($dataSet: [String!], $related: [String!], $network: [String!], $page: Int) {
        edges(dataSet: $dataSet, related: $related, network: $network, page: $page) {
            items {
                name
                node1 { name }
            }
            page
        }
    }"""
    response = client.post('/api', json={'query': query,
                                         'variables': {'related': [related]}})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

    assert page['page'] == 1
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert type(result['node1']['name']) is str


def test_edges_query_with_passed_network(client, network):
    query = """query Edges($dataSet: [String!], $related: [String!], $network: [String!], $page: Int) {
        edges(dataSet: $dataSet, related: $related, network: $network, page: $page) {
            items {
                label
                name
                score
                node1 { name }
                node2 { name }
            }
        }
    }"""
    response = client.post('/api', json={'query': query,
                                         'variables': {'network': [network]}})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['score']) is float or NoneType
        assert type(result['node1']['name']) is str
        assert type(result['node2']['name']) is str


def test_edges_query_with_no_arguments(client):
    query = """query Edges($dataSet: [String!], $related: [String!], $network: [String!], $page: Int) {
        edges(dataSet: $dataSet, related: $related, network: $network, page: $page) {
            items {
                name
                node1 { name }
            }
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert type(result['node1']['name']) is str
