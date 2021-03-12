import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging
from api.database import return_snp_query
"""
query Snps(
    $paging: PagingInput
    $distinct: Boolean
    $name: [String!]
    $rsid: [String!]
    $chr: [String!]
    $maxBP: Int
    $minBP: Int
) {
    snps(
        paging: $paging
        distinct: $distinct
        dataSet: $dataSet
        name: $name
        rsid: $rsid
        chr: $chr
        maxBP: $maxBP
        minBP: $minBP
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
        name
        rsid
        chr
        bp
    }
  }
}
"""


@pytest.fixture(scope='module')
def snp_name():
    return '7:104003135:C:G'


@pytest.fixture(scope='module')
def snp_rsid():
    return 'rs2188491'


@pytest.fixture(scope='module')
def snp_chr():
    return '7'


@pytest.fixture(scope='module')
def max_bp():
    return 629418


@pytest.fixture(scope='module')
def min_bp():
    return 245245741


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Snps(
            $paging: PagingInput
            $distinct: Boolean
            $name: [String!]
            $rsid: [String!]
            $chr: [String!]
            $maxBP: Int
            $minBP: Int
    ) {
        snps(
            paging: $paging
            distinct: $distinct
            name: $name
            rsid: $rsid
            chr: $chr
            maxBP: $maxBP
            minBP: $minBP
        )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder("""{
            items {
                name
                rsid
                chr
                bp
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


def test_snp_cursor_pagination_first(client, common_query_builder):
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
    page = json_data['data']['snps']
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


def test_snp_cursor_pagination_last(client, common_query_builder):
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
    page = json_data['data']['snps']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_snp_cursor_distinct_pagination(client, common_query):
    page_num = 1
    num = 10
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'paging': {
                'page': page_num,
                'first': num,
            },
            'distinct': True
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['snps']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_snps_query_with_passed_min_bp(client, common_query, min_bp):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'minBP': min_bp
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['snps']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['bp'] >= min_bp


def test_snps_query_with_passed_max_bp(client, common_query, max_bp):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'maxBP': max_bp
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['snps']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['bp'] <= max_bp


def test_snps_query_with_no_arguments(client, common_query_builder):
    query = common_query_builder("""{
            items {
                name
            }
        }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['snps']
    snp_results = page['items']
    # Get the total number of hr results in the database.
    snp_count = return_snp_query('id').count()

    assert isinstance(snp_results, list)
    assert len(snp_results) == snp_count
    for snp_result in snp_results[0:2]:
        assert type(snp_result['name']) is str
