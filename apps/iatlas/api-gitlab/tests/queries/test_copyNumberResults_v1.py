import json
import pytest
from tests import NoneType
from api.enums import direction_enum
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging


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

query = """
    query CopyNumberResults(
            $paging: PagingInput
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
            paging: $paging
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
            paging {
                type
                pages
                total
                startCursor
                endCursor
                hasPreviousPage
                hasNextPage
                page
                limit
            }
            error
            items {
                id
                direction
                meanNormal
                meanCnv
                pValue
                log10PValue
                tStat
                dataSet {
                    name
                }
                tag {
                    name
                }
                gene {
                    entrez
                    hgnc
                }
            }
        }
    }
"""

# Test that forward cursor pagination gives us the expected paginInfo
def test_copyNumberResults_cursor_pagination_first(client):
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {'first': num }
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == True
    assert paging['hasPreviousPage'] == False
    assert start == items[0]['id']
    assert end == items[num-1]['id']
    assert int(end) - int(start) > 0

def test_copyNumberResults_cursor_pagination_last(client):
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {
                'last': num,
                'before': to_cursor_hash(100)
            }
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num-1]['id']
    # assert int(end) - int(start) == num - 1

def test_copyNumberResults_cursor_distinct_pagination(client):
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
            'tag': ['C1']
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']

def test_copyNumberResults_query_with_passed_data_set(client, data_set, entrez, feature_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {
                'first': 10,
            },
            'dataSet': [data_set],
            'entrez': [entrez],
            'feature_name': [feature_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    paging = page['paging']
    items = page['items']

    assert type(paging['total']) is int
    assert paging['hasNextPage'] == True
    assert paging['hasPreviousPage'] == False
    assert type(paging['startCursor']) is str
    assert type(paging['endCursor']) is str

    assert isinstance(items, list)
    assert len(items) > 0
    for item in items[0:2]:
        current_data_set = item['dataSet']
        assert current_data_set['name'] == data_set

def test_copyNumberResults_missing_pagination(client):
    """Verify that query does not error when paging is not sent by the client

    The purpose of this test is the ensure that valid and sensible default values
    are used and the query does not error, when no paging arguments are sent.
    Cursor pagination and a limit of 100,000 will be used by default.
    """
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': ['TCGA'],
            'tag': ['C1']
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    items = page['items']

    assert len(items) == Paging.MAX_LIMIT
