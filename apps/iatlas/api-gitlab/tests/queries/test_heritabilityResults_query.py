import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging

"""
query HeritabilityResults(
  $paging: PagingInput
  $distinct:Boolean
  $dataSet: [String!]
  $feature: [String!]
  $module: [String!]
  $cluster: [String!]
  $minPValue: Float
  $maxPValue: Float
) {
  heritabilityResults(
    paging: $paging
    distinct: $distinct
    dataSet: $dataSet
    feature: $feature
    module: $module
    cluster: $cluster
    minPValue: $minPValue
    maxPValue: $maxPValue
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
        pValue
        dataSet { name }
        feature { name }
        cluster
        module
        category
        fdr
        variance
        se
        yMin
        yMax
    }
  }
}
"""


@pytest.fixture(scope='module')
def dr_feature():
    return 'Module11_Prolif_score'


@pytest.fixture(scope='module')
def gene_entrez():
    return 284058


@pytest.fixture(scope='module')
def mutation_code():
    return '(OM)'


@pytest.fixture(scope='module')
def dr_tag_name():
    return 'BLCA'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query HeritabilityResults(
            $paging: PagingInput
            $distinct:Boolean
            $dataSet: [String!]
            $feature: [String!]
            $module: [String!]
            $cluster: [String!]
            $minPValue: Float
            $maxPValue: Float
    ) {
        heritabilityResults(
            paging: $paging
            distinct: $distinct
            dataSet: $dataSet
            feature: $feature
            module: $module
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
                feature { name }
                cluster
                module
                category
                fdr
                variance
                se
                yMin
                yMax
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
    return 0.495103


@pytest.fixture(scope='module')
def max_log10_p_value():
    return 0.197782


@pytest.fixture(scope='module')
def min_fold_change():
    return 1.44142


@pytest.fixture(scope='module')
def min_log10_fold_change():
    return -0.0544383


@pytest.fixture(scope='module')
def min_p_value():
    return 0.634187


@pytest.fixture(scope='module')
def min_log10_p_value():
    return 0.30530497


@pytest.fixture(scope='module')
def min_n_mut():
    return 23


@pytest.fixture(scope='module')
def min_n_wt():
    return 383

# Test that forward cursor pagination gives us the expected paginInfo


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


def test_heritabilityResults_query_with_passed_data_set_and_feature(client, common_query, data_set, dr_feature):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
        'feature': [dr_feature]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['heritabilityResults']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['dataSet']['name'] == data_set
        assert result['feature']['name'] == dr_feature


def test_heritabilityResults_query_with_passed_min_p_value(client, common_query, data_set, dr_feature, min_p_value):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'feature': [dr_feature],
            'minPValue': min_p_value
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['heritabilityResults']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert result['pValue'] >= min_p_value


def test_heritabilityResults_query_with_passed_max_p_value(client, common_query, data_set, dr_feature, max_p_value):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'feature': [dr_feature],
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
            items {
                pValue
            }
        }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['heritabilityResults']
    heritability_results = page['items']
    assert isinstance(heritability_results, list)
    assert len(heritability_results) > 0
    for heritability_result in heritability_results[0:2]:
        assert type(heritability_result['pValue']) is float or NoneType
