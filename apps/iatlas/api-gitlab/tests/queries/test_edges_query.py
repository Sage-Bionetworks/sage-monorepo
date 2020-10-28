import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging

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
            $paging: PagingInput
            $distinct: Boolean
            $maxScore: Float
            $minScore: Float
            $node1: [String!]
            $node2: [String!]
        ) {
            edges(
                paging: $paging
                distinct: $distinct
                maxScore: $maxScore
                minScore: $minScore
                node1: $node1
                node2: $node2
            )""" + query_fields + "}"
    return f

def test_edges_cursor_pagination_first(client, common_query_builder):
    query = common_query_builder("""{
            paging {
                startCursor
                endCursor
                hasNextPage
                hasPreviousPage
            }
            items { id }
        }""")
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {'first': num}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == True
    assert paging['hasPreviousPage'] == False
    assert start == items[0]['id']
    assert end == items[num - 1]['id']
    assert int(end) - int(start) > 0

def test_edges_cursor_pagination_last(client, common_query_builder):
    query = common_query_builder("""{
            paging {
                startCursor
                endCursor
                hasNextPage
                hasPreviousPage
            }
            items { id }
        }""")
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {
                'last': num,
                'before': to_cursor_hash(100)
            }
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_edges_cursor_distinct_pagination(client, common_query_builder):
    query = common_query_builder("""{
                                    paging {
                                        page
                                    }
                                    items {
                                        name
                                        node1 { name }
                                    }
                                }""")
    page_num = 2
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {
                'page': page_num,
                'first': num,
            },
            'distinct': True,
            'dataSet': ['TCGA'],
            "related": ["Immune_Subtype"]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']

def test_edges_missing_pagination(client, common_query_builder):
    """Verify that query does not error when paging is not sent by the client

    The purpose of this test is the ensure that valid and sensible default values
    are used and the query does not error, when no paging arguments are sent.
    Cursor pagination and a limit of 100,000 will be used by default.
    """
    query = common_query_builder("""{
            paging {
                startCursor
                endCursor
                hasNextPage
                hasPreviousPage
            }
            items { id }
        }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': ['TCGA'],
            "related": ["Immune_Subtype"]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    items = page['items']

    assert len(items) == Paging.MAX_LIMIT

def test_edges_query_with_passed_node1_and_node2(client, common_query_builder, node_1, node_2):
    query = common_query_builder("""{
                                    items { name }
                                    paging {
                                        page
                                        pages
                                        total
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'paging': {'type': Paging.OFFSET}, 'node1': [node_1], 'node2': [node_2]}})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']
    paging = page['paging']

    assert paging['page'] == 1
    assert type(paging['pages']) is int
    assert type(paging['total']) is int
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
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'node1': [node_1]}})
    json_data = json.loads(response.data)
    page = json_data['data']['edges']
    results = page['items']

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
