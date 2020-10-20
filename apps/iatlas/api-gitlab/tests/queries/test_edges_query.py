import json
import pytest
from tests import NoneType


@pytest.fixture(scope='module')
def max_score():
    return 10.6


@pytest.fixture(scope='module')
def min_score():
    return 5.5


@pytest.fixture(scope='module')
def node_1():
    return 'tcga_ecn_13857'


@pytest.fixture(scope='module')
def node_2():
    return 'tcga_ecn_38967'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Edges(
            $maxScore: Float
            $minScore: Float
            $node1: [String!]
            $node2: [String!]
            $page: Int
        ) {
            edges(
                maxScore: $maxScore
                minScore: $minScore
                node1: $node1
                node2: $node2
                page: $page
            )""" + query_fields + "}"
    return f


def test_edges_query_with_passed_node1_and_node2(client, common_query_builder, node_1, node_2):
    query = common_query_builder("""{
                                    items { name }
                                    page
                                    pages
                                    total
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'node1': [node_1], 'node2': [node_2]}})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

    assert page['page'] == 1
    assert type(page['pages']) is int
    assert type(page['total']) is int
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str


def test_edges_query_with_passed_node1(client, common_query_builder, node_1):
    query = common_query_builder("""{
                                    items {
                                        name
                                        node1 { name }
                                    }
                                    page
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'node1': [node_1]}})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

    assert page['page'] == 1
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['node1']['name'] == node_1


def test_edges_query_with_passed_node2(client, common_query_builder, node_2):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                        node1 { name }
                                        node2 { name }
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'node2': [node_2]}})
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
        assert result['node2']['name'] == node_2


def test_edges_query_with_passed_maxScore_and_node2(client, common_query_builder, max_score, node_2):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'maxScore': max_score, 'node2': [node_2]}})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert result['score'] <= max_score


def test_edges_query_with_passed_minScore_and_node2(client, common_query_builder, min_score, node_2):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'minScore': min_score, 'node2': [node_2]}})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert result['score'] >= min_score


def test_edges_query_with_passed_maxScore_minScore_and_node2(client, common_query_builder, max_score, min_score, node_2):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'maxScore': max_score,
                                                       'minScore': min_score,
                                                       'node2': [node_2]}})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert result['score'] <= max_score
        assert result['score'] >= min_score


def test_edges_query_with_no_arguments(client, common_query_builder):
    query = common_query_builder("""{
                                    items {
                                        name
                                        node1 { name }
                                    }
                                }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert type(result['node1']['name']) is str
