import json
import pytest
from tests import NoneType
from api.enums import direction_enum


@pytest.fixture(scope='module')
def network_1():
    return 'extracellular_network'


@pytest.fixture(scope='module')
def network_2():
    return 'cellimage_network'


def test_nodes_query_with_passed_data_set(client, data_set):
    query = """Nodes($dataSet: [String!], $related: [String!], $network: [String!]) {
    nodes(dataSet: $dataSet, related: $related, network: $network) {
            label
            name
            score
            x
            y
            dataSet { name }
            gene { entrez }
            feature { name }
            tags { name }
        }
    }"""
    response = client.post('/api', json={'query': query,
                                         'variables': {'dataSet': [data_set]}})
    json_data = json.loads(response.data)
    results = json_data['data']['nodes']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        gene = result['gene']
        feature = result['feature']
        tags = result['tags']
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['score']) is float or NoneType
        assert type(result['x']) is float or NoneType
        assert type(result['y']) is float or NoneType
        assert current_data_set['name'] == data_set
        assert type(gene['entrez']) is int
        assert type(feature['name']) is str
        assert isinstance(tags, list)
        assert len(results) > 0
        for tag in tags[0:2]:
            assert type(tag['name']) is str


def test_nodes_query_with_passed_related(client, data_set, related):
    query = """Nodes($dataSet: [String!], $related: [String!], $network: [String!]) {
    nodes(dataSet: $dataSet, related: $related, network: $network) {
            label
            name
            score
            x
            y
            dataSet { name }
            gene { entrez }
            feature { name }
            tags { name }
        }
    }"""
    response = client.post('/api', json={'query': query,
                                         'variables': {'related': [related]}})
    json_data = json.loads(response.data)
    results = json_data['data']['nodes']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        gene = result['gene']
        feature = result['feature']
        tags = result['tags']
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['score']) is float or NoneType
        assert type(result['x']) is float or NoneType
        assert type(result['y']) is float or NoneType
        assert current_data_set['name'] == data_set
        assert type(gene['entrez']) is int
        assert type(feature['name']) is str
        assert isinstance(tags, list)
        assert len(results) > 0
        for tag in tags[0:2]:
            assert type(tag['name']) is str


def test_nodes_query_with_passed_network(client, data_set, related, network_1):
    query = """Nodes($dataSet: [String!], $related: [String!], $network: [String!]) {
    nodes(dataSet: $dataSet, related: $related, network: $network) {
            label
            name
            score
            x
            y
            dataSet { name }
            gene { entrez }
            feature { name }
            tags { name }
        }
    }"""
    response = client.post('/api', json={'query': query,
                                         'variables': {'network': [network_1]}})
    json_data = json.loads(response.data)
    results = json_data['data']['nodes']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        gene = result['gene']
        feature = result['feature']
        tags = result['tags']
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['score']) is float or NoneType
        assert type(result['x']) is float or NoneType
        assert type(result['y']) is float or NoneType
        assert current_data_set['name'] == data_set
        assert type(gene['entrez']) is int
        assert type(feature['name']) is str
        assert isinstance(tags, list)
        assert len(results) > 0
        for tag in tags[0:2]:
            assert type(tag['name']) is str
            assert tag['name'] != network_


# This pulls too many results and crashes the API.
# TODO: Stop the app from crashing on large results.
def test_nodes_query_with_no_arguments(client):
    query = """Nodes($dataSet: [String!], $related: [String!], $network: [String!]) {
    nodes(dataSet: $dataSet, related: $related, network: $network) {
            label
            name
            score
            x
            y
            dataSet { name }
            gene { entrez }
            feature { name }
            tags { name }
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['nodes']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        gene = result['gene']
        feature = result['feature']
        tags = result['tags']
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['score']) is float or NoneType
        assert type(result['x']) is float or NoneType
        assert type(result['y']) is float or NoneType
        assert type(current_data_set['name']) is str
        assert type(gene['entrez']) is int
        assert type(feature['name']) is str
        assert isinstance(tags, list)
        assert len(results) > 0
        for tag in tags[0:2]:
            assert type(tag['name']) is str
