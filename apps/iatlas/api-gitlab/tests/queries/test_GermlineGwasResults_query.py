import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging
from api.database import return_germline_gwas_result_query
import logging


@pytest.fixture(scope='module')
def ggr_feature():
    return 'Cell_Proportion_B_Cells_Memory_Binary_MedianLowHigh'


@pytest.fixture(scope='module')
def ggr_snp():
    return '7:104003135:C:G'


@pytest.fixture(scope='module')
def ggr_max_p_value():
    # return 0.000000000000712
    return 7.12e-26


@pytest.fixture(scope='module')
def ggr_min_p_value():
    return 9.98e-07


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query GermlineGwasResults(
            $paging: PagingInput
            $distinct: Boolean
            $dataSet: [String!]
            $feature: [String!]
            $snp: [String!]
            $minPValue: Float
            $maxPValue: Float
    ) {
        germlineGwasResults(
            paging: $paging
            distinct: $distinct
            dataSet: $dataSet
            feature: $feature
            snp: $snp
            minPValue: $minPValue
            maxPValue: $maxPValue
        )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder("""{
            items {
                dataSet { name }
                feature { name }
                snp { name }
                category
                pValue
                maf
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


# Test that forward cursor pagination gives us the expected paginInfo


def test_germlineGwasResults_cursor_pagination_first(client, common_query_builder):
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
    page = json_data['data']['germlineGwasResults']
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


def test_germlineGwasResults_cursor_pagination_last(client, common_query_builder):
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
    page = json_data['data']['germlineGwasResults']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_germlineGwasResults_cursor_distinct_pagination(client, common_query):
    page_num = 1
    num = 10
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'paging': {
                'page': page_num,
                'first': num,
            },
            'distinct': True,
            'dataSet': ['TCGA']
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['germlineGwasResults']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_germlineGwasResults_query_with_passed_dataset_snp_and_feature(client, common_query, data_set, ggr_snp, ggr_feature):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
        'feature': [ggr_feature],
        'snp': [ggr_snp]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['germlineGwasResults']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['dataSet']['name'] == data_set
        assert result['feature']['name'] == ggr_feature
        assert result['snp']['name'] == ggr_snp


def test_germlineGwasResults_query_with_passed_min_p_value(client, common_query_builder, ggr_min_p_value):
    query = common_query_builder(
        """{
            items { pValue }
        }"""
    )
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'minPValue': ggr_min_p_value
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['germlineGwasResults']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] >= ggr_min_p_value


def test_germlineGwasResults_query_with_passed_max_p_value(client, common_query_builder, ggr_max_p_value):
    logger = logging.getLogger("ggresulttest ")
    query = common_query_builder(
        """{
            items { pValue }
        }"""
    )
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'maxPValue': ggr_max_p_value
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['germlineGwasResults']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] <= ggr_max_p_value


def test_germlineGwasResults_query_with_no_arguments(client, common_query_builder):
    query = common_query_builder("""{
            items {
                pValue
                feature {
                    name
                }
            }
        }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['germlineGwasResults']
    germline_gwas_results = page['items']
    # Get the total number of hr results in the database.
    ggr_count = return_germline_gwas_result_query('id').count()

    assert isinstance(germline_gwas_results, list)
    assert len(germline_gwas_results) == ggr_count
    for germline_gwas_result in germline_gwas_results[0:2]:
        assert type(germline_gwas_result['pValue']) is float or NoneType
