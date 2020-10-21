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
                feature {
                    name
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

def test_copyNumberResults_query_with_passed_entrez(client, data_set, entrez, feature_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'feature': [feature_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        gene = result['gene']
        assert gene['entrez'] == entrez

def test_copyNumberResults_query_with_passed_features(client, data_set, entrez, feature_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'feature': [feature_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        feature = result['feature']
        assert feature['name'] == feature_name

def test_copyNumberResults_query_with_passed_tag(client, data_set, feature_name, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'feature': [feature_name],
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        tag = result['tag']
        assert tag['name'] == tag_name

def test_copyNumberResults_query_with_passed_direction(client, data_set, direction, entrez, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'direction': direction,
            'entrez': [entrez],
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['direction'] == direction

def test_copyNumberResults_query_with_passed_min_p_value(client, data_set, entrez, min_p_value, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minPValue': min_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] >= min_p_value

def test_copyNumberResults_query_with_passed_min_p_value_and_min_log10_p_value(client, data_set, entrez, min_log10_p_value, min_p_value, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minLog10PValue': min_log10_p_value,
            'minPValue': min_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] >= min_p_value

def test_copyNumberResults_query_with_passed_max_p_value(client, data_set, entrez, max_p_value, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'maxPValue': max_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] <= max_p_value

def test_copyNumberResults_query_with_passed_max_p_value_and_max_log10_p_value(client, data_set, entrez, max_log10_p_value, max_p_value, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'maxLog10PValue': max_log10_p_value,
            'maxPValue': max_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] <= max_p_value

def test_copyNumberResults_query_with_passed_min_log10_p_value(client, data_set, entrez, min_log10_p_value, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minLog10PValue': min_log10_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['log10PValue'] >= min_log10_p_value

def test_copyNumberResults_query_with_passed_max_log10_p_value(client, data_set, entrez, max_log10_p_value, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'maxLog10PValue': max_log10_p_value,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['log10PValue'] <= max_log10_p_value

def test_copyNumberResults_query_with_passed_min_mean_normal(client, data_set, entrez, min_mean_normal, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minMeanNormal': min_mean_normal,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['meanNormal'] >= min_mean_normal


def test_copyNumberResults_query_with_passed_min_mean_cnv(client, data_set, entrez, min_mean_cnv, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minMeanCnv': min_mean_cnv,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['meanCnv'] >= min_mean_cnv


def test_copyNumberResults_query_with_passed_min_t_stat(client, data_set, entrez, min_t_stat, tag_name):
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'entrez': [entrez],
            'minTStat': min_t_stat,
            'tag': [tag_name]
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['tStat'] >= min_t_stat


def test_copyNumberResults_query_with_no_arguments(client):
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['copyNumberResults']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['direction'] in direction_enum.enums
        assert type(result['meanNormal']) is float or NoneType
        assert type(result['meanCnv']) is float or NoneType
        assert type(result['pValue']) is float or NoneType
        assert type(result['log10PValue']) is float or NoneType
        assert type(result['tStat']) is int or NoneType

