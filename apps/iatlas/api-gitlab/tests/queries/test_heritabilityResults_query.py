import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging
from api.database import return_heritability_result_query


@pytest.fixture(scope='module')
def hr_feature():
    return 'BCR_Richness'


@pytest.fixture(scope='module')
def hr_germline_module():
    return 'Unassigned'


@pytest.fixture(scope='module')
def hr_germline_category():
    return 'Adaptive Receptor'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query HeritabilityResults(
            $paging: PagingInput
            $distinct:Boolean
            $dataSet: [String!]
            $feature: [String!]
            $cluster: [String!]
            $minPValue: Float
            $maxPValue: Float
    ) {
        heritabilityResults(
            paging: $paging
            distinct: $distinct
            dataSet: $dataSet
            feature: $feature
            cluster: $cluster
            minPValue: $minPValue
            maxPValue: $maxPValue
        )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder("""{
            items {
              pValue
              dataSet { name }
              feature {
                name
                display
                unit
                order
                germlineModule
                germlineCategory
              }
              cluster
              fdr
              variance
              se
            }
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
        }""")


@pytest.fixture(scope='module')
def max_p_value():
    return 0.000084099999999999998


@pytest.fixture(scope='module')
def min_p_value():
    return 0.493599999999999983213


def test_heritabilityResults_cursor_pagination_first(client, common_query_builder):
    query = common_query_builder("""{
            items {
                id
            }
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
        }""")
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {'first': num}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['heritabilityResults']
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


def test_heritabilityResults_cursor_pagination_last(client, common_query_builder):
    query = common_query_builder("""{
            items {
                id
            }
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
        }""")
    num = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {
                'last': num,
                'before': to_cursor_hash(1000)
            }
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['heritabilityResults']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_heritabilityResults_cursor_distinct_pagination(client, common_query):
    page_num = 2
    num = 10
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'paging': {
                'page': page_num,
                'first': num,
            },
            'distinct': True,
            'dataSet': ['TCGA'],
            'tag': ['C1']
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['heritabilityResults']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_heritabilityResults_query_with_passed_data_set_and_feature(client, common_query, data_set, hr_feature, hr_germline_module, hr_germline_category):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
        'feature': [hr_feature]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['heritabilityResults']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['dataSet']['name'] == data_set
        assert result['feature']['name'] == hr_feature
        assert type(result['feature']['display']) is str
        assert type(result['feature']['unit']) is str
        assert type(result['feature']['order']) is int
        assert result['feature']['germlineModule'] == hr_germline_module
        assert result['feature']['germlineCategory'] == hr_germline_category


def test_heritabilityResults_query_with_passed_min_p_value(client, common_query, min_p_value):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'minPValue': min_p_value
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['heritabilityResults']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] >= min_p_value


def test_heritabilityResults_query_with_passed_max_p_value(client, common_query, max_p_value):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'maxPValue': max_p_value
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['heritabilityResults']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] <= max_p_value


def test_heritabilityResults_query_with_no_arguments(client, common_query_builder):
    query = common_query_builder("""{
            items { id }
        }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['heritabilityResults']
    heritability_results = page['items']
    # Get the total number of hr results in the database.
    hr_count = return_heritability_result_query('id').count()

    assert isinstance(heritability_results, list)
    assert len(heritability_results) == hr_count
    for heritability_result in heritability_results[0:2]:
        assert type(heritability_result['id']) is str
