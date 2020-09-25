import json
import pytest
from api.database import return_node_query
from tests import NoneType


@pytest.fixture(scope='module')
def network():
    return 'extracellular_network'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Nodes(
            $dataSet: [String!]
            $related: [String!]
            $network: [String!]
            $page: Int
        ) {
            nodes(
                dataSet: $dataSet
                related: $related
                network: $network
                page: $page
            )""" + query_fields + "}"
    return f


def test_nodes_query_with_passed_data_set(client, common_query_builder, data_set):
    query = common_query_builder("""{
                                    items {
                                        name
                                        dataSet { name }
                                    }
                                    page
                                    pages
                                    total
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'dataSet': [data_set], 'page': 2}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert page['page'] == 2
    assert type(page['pages']) is int
    assert type(page['total']) is int
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str


def test_nodes_query_with_passed_related(client, common_query_builder, related):
    query = common_query_builder("""{
                                    items {
                                        name
                                        gene { entrez }
                                    }
                                    page
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'related': [related]}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert page['page'] == 1
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        gene = result['gene']
        assert type(result['name']) is str
        if gene:
            assert type(gene['entrez']) is int


def test_nodes_query_with_passed_network(client, common_query_builder, network):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                        x
                                        y
                                        feature { name }
                                        tags { name }
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'network': [network]}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

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
        assert len(tags) > o
        for tag in tags[0:2]:
            assert type(tag['name']) is str
            assert tag['name'] != network


def test_nodes_query_with_no_arguments(client, common_query_builder):
    query = common_query_builder("""{
                                    items {
                                        name
                                        dataSet { name }
                                    }
                                    total
                                }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    # Get the total number of samples_to_mutations in the database.
    node_count = return_node_query('id').count()

    assert page['total'] == node_count
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['dataSet']
        assert type(result['name']) is str
        assert type(current_data_set['name']) is str
