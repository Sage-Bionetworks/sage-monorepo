import json
import pytest
from api.database import return_node_query
from tests import NoneType


@pytest.fixture(scope='module')
def max_score():
    return 0.0234375


@pytest.fixture(scope='module')
def min_score():
    return 0.9980582524271844891


@pytest.fixture(scope='module')
def network():
    return 'extracellular_network'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Nodes(
            $dataSet: [String!]
            $maxScore: Float
            $minScore: Float
            $network: [String!]
            $related: [String!]
            $tag: [String!]
            $page: Int
        ) {
            nodes(
                dataSet: $dataSet
                maxScore: $maxScore
                minScore: $minScore
                network: $network
                related: $related
                tag: $tag
                page: $page
            )""" + query_fields + "}"
    return f


def test_nodes_query_with_passed_data_set(client, common_query_builder, data_set):
    query = common_query_builder("""{
                                    items { name }
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
        assert len(tags) > 0
        for tag in tags[0:2]:
            assert type(tag['name']) is str
            assert tag['name'] != network


def test_nodes_query_with_passed_network_and_tag(client, common_query_builder, network, tag):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                        x
                                        y
                                        gene { entrez }
                                        tags { name }
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'network': [network], 'tag': [tag]}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        gene = result['gene']
        tags = result['tags']
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert type(result['score']) is float or NoneType
        assert type(result['x']) is float or NoneType
        assert type(result['y']) is float or NoneType
        if gene:
            assert type(gene['entrez']) is int
        assert isinstance(tags, list)
        assert len(tags) > 0
        for current_tag in tags[0:2]:
            assert type(current_tag['name']) is str
            assert current_tag['name'] != network
            assert current_tag['name'] == tag


def test_nodes_query_with_passed_tag(client, common_query_builder, tag):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        tags {
                                            name
                                            characteristics
                                            color
                                            longDisplay
                                            shortDisplay
                                        }
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'tag': [tag]}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        tags = result['tags']
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert isinstance(tags, list)
        assert len(tags) > 0
        for current_tag in tags[0:2]:
            assert type(current_tag['name']) is str
            assert current_tag['name'] == tag


def test_nodes_query_with_passed_maxScore(client, common_query_builder, max_score):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'maxScore': max_score}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert result['score'] <= max_score


def test_nodes_query_with_passed_minScore(client, common_query_builder, min_score):
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'minScore': min_score}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert result['score'] >= min_score


def test_nodes_query_with_passed_maxScore_and_minScore(client, common_query_builder, max_score):
    min_score = 0.015625
    query = common_query_builder("""{
                                    items {
                                        label
                                        name
                                        score
                                    }
                                }""")
    response = client.post('/api', json={'query': query,
                                         'variables': {'maxScore': max_score, 'minScore': min_score}})
    json_data = json.loads(response.data)
    page = json_data['data']['nodes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['label']) is str or NoneType
        assert type(result['name']) is str
        assert result['score'] <= max_score
        assert result['score'] >= min_score


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
