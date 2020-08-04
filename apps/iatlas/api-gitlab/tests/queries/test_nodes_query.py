import json
import pytest
from tests import NoneType


@pytest.fixture(scope='module')
def network():
    return 'extracellular_network'


def test_nodes_query_with_passed_data_set(client, data_set):
    query = """query Nodes($dataSet: [String!], $related: [String!], $network: [String!]) {
    nodes(dataSet: $dataSet, related: $related, network: $network) { name }
    }"""
    response = client.post('/api', json={'query': query,
                                         'variables': {'dataSet': [data_set]}})
    json_data = json.loads(response.data)
    results = json_data['data']['nodes']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str


def test_nodes_query_with_passed_related(client, data_set, related):
    query = """query Nodes($dataSet: [String!], $related: [String!], $network: [String!]) {
    nodes(dataSet: $dataSet, related: $related, network: $network) {
            name
            gene { entrez }
        }
    }"""
    response = client.post('/api', json={'query': query,
                                         'variables': {'related': [related]}})
    json_data = json.loads(response.data)
    results = json_data['data']['nodes']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        gene = result['gene']
        assert type(result['name']) is str
        if gene:
            assert type(gene['entrez']) is int


def test_nodes_query_with_passed_network(client, data_set, related, network):
    query = """query Nodes($dataSet: [String!], $related: [String!], $network: [String!]) {
    nodes(dataSet: $dataSet, related: $related, network: $network) {
            label
            name
            score
            x
            y
            feature { name }
            tags { name }
        }
    }"""
    response = client.post('/api', json={'query': query,
                                         'variables': {'network': [network]}})
    json_data = json.loads(response.data)
    results = json_data['data']['nodes']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        feature = result['feature']
        tags = result['tags']
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['score']) is float or NoneType
        assert type(result['x']) is float or NoneType
        assert type(result['y']) is float or NoneType
        if feature:
            assert type(feature['name']) is str
        assert isinstance(tags, list)
        for tag in tags[0:2]:
            assert type(tag['name']) is str
            assert tag['name'] != network


def test_nodes_query_with_no_arguments(client):
    query = """query Nodes($dataSet: [String!], $related: [String!], $network: [String!]) {
    nodes(dataSet: $dataSet, related: $related, network: $network) {
            name
            dataSet { name }
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['nodes']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        assert type(result['name']) is str
        assert type(current_data_set['name']) is str
