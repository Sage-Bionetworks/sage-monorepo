import json
import pytest
from tests import NoneType
from api.enums import direction_enum
from api.resolvers.resolver_helpers.cursor_utils import from_cursor_hash


@pytest.fixture(scope='module')
def feature_name():
    return 'frac_altered'


@pytest.fixture(scope='module')
def tag_name():
    return 'BLCA'


@pytest.fixture(scope='module')
def direction():
    return 'Amp'


@pytest.fixture(scope='module')
def min_p_value():
    return 0.0000028


@pytest.fixture(scope='module')
def max_p_value():
    return 0.000021


@pytest.fixture(scope='module')
def min_log10_p_value():
    return 0.000037


@pytest.fixture(scope='module')
def max_log10_p_value():
    return 13.162428


@pytest.fixture(scope='module')
def min_mean_normal():
    return 9.313083


@pytest.fixture(scope='module')
def min_mean_cnv():
    return 14.833332


@pytest.fixture(scope='module')
def min_t_stat():
    return -5.118745


# Test that forward cursor pagination gives us the expected paginInfo
def test_copyNumberResults_cursor_pagination_first(client):
    query = """
        query CopyNumberResults(
            $first: Int
            $after: String
        ) {
            copyNumberResults(
                first: $first
                after: $after
            ) {
                pageInfo {
                    startCursor
                    endCursor
                    hasPreviousPage
                    hasNextPage
                }
                totalCount
                edges {
                    cursor
                    node {
                        id
                    }
                }
            }
        }
    """
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'first': num
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    edges = page['edges']
    start = from_cursor_hash(page['pageInfo']['startCursor'])
    end = from_cursor_hash(page['pageInfo']['endCursor'])

    assert len(edges) == num
    assert page['pageInfo']['hasNextPage'] == True
    assert page['pageInfo']['hasPreviousPage'] == False
    assert start == edges[0]['node']['id']
    assert end == edges[num-1]['node']['id']
    assert int(end) - int(start) == num - 1

def test_copyNumberResults_cursor_pagination_last(client):
    query = """
        query CopyNumberResults(
            $last: Int
            $before: String
        ) {
            copyNumberResults(
                last: $last
                before: $before
            ) {
                pageInfo {
                    startCursor
                    endCursor
                    hasPreviousPage
                    hasNextPage
                }
                totalCount
                edges {
                    cursor
                    node {
                        id
                    }
                }
            }
        }
    """
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'last': num,
            'before': 'MTE='
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    edges = page['edges']
    start = from_cursor_hash(page['pageInfo']['startCursor'])
    end = from_cursor_hash(page['pageInfo']['endCursor'])

    assert len(edges) == num
    assert page['pageInfo']['hasNextPage'] == False
    assert page['pageInfo']['hasPreviousPage'] == False
    assert start == edges[0]['node']['id']
    assert end == edges[num-1]['node']['id']
    assert int(end) - int(start) == num - 1

def test_copyNumberResults_query_with_passed_data_set(client, data_set, entrez, feature_name):
    query = """
        query CopyNumberResults(
            $first: Int
            $last: Int
            $before: String
            $after: String
            $distinct:Boolean
            $dataSet: [String!]
            $feature: [String!]
            $entrez: [Int!]
            $tag: [String!]
            $direction: DirectionEnum
            $minPValue: Float
            $maxPValue: Float
            $minLog10PValue: Float
            $maxLog10PValue: Float
            $minMeanNormal: Float
            $minMeanCnv: Float
            $minTStat: Float
        ) {
            copyNumberResults(
                first: $first
                last: $last
                before: $before
                after: $after
                distinct: $distinct
                dataSet: $dataSet
                feature: $feature
                entrez: $entrez
                tag: $tag
                direction: $direction
                minPValue: $minPValue
                maxPValue: $maxPValue
                minLog10PValue: $minLog10PValue
                maxLog10PValue: $maxLog10PValue
                minMeanNormal: $minMeanNormal
                minMeanCnv: $minMeanCnv
                minTStat: $minTStat
            ) {
                pageInfo {
                    startCursor
                    endCursor
                    hasPreviousPage
                    hasNextPage
                }
                totalCount
                edges {
                    cursor
                    node {
                        dataSet {
                            name
                        }
                    }
                }
            }
        }
    """
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'first': 100,
            'dataSet': [data_set],
            'entrez': [entrez],
            'feature_name': [feature_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['edges']

    assert type(page['totalCount']) is int
    assert page['pageInfo']['hasNextPage'] == True
    assert page['pageInfo']['hasPreviousPage'] == False
    assert type(page['pageInfo']['startCursor']) is str
    assert type(page['pageInfo']['endCursor']) is str

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        current_data_set = result['node']['dataSet']
        assert current_data_set['name'] == data_set

