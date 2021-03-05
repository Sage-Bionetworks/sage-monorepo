import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging


@pytest.fixture(scope='module')
def rvpa_feature():
    return 'BCR_Richness'


@pytest.fixture(scope='module')
def rvpa_pathway():
    return 'MMR'


@pytest.fixture(scope='module')
def rvpa_max_p_value():
    return 0.495103


@pytest.fixture(scope='module')
def rvpa_min_p_value():
    return 0.634187


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query RareVariantPathwayAssociation(
        $paging: PagingInput
        $distinct: Boolean
        $dataSet: [String!]
        $feature: [String!]
        $pathway: [String!]
        $minPValue: Float
        $maxPValue: Float
    ) {
        rareVariantPathwayAssociations(
            paging: $paging
            distinct: $distinct
            dataSet: $dataSet
            feature: $feature
            pathway: $pathway
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
                pathway
                pValue
                min
                max
                mean
                q1
                q2
                q3
                nMutants
                nTotal
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


def test_rareVariantPathwayAssociation_cursor_pagination_first(client, common_query_builder):
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
    page = json_data['data']['rareVariantPathwayAssociations']
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


def test_rareVariantPathwayAssociation_cursor_pagination_last(client, common_query_builder):
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
    page = json_data['data']['rareVariantPathwayAssociations']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_rareVariantPathwayAssociation_cursor_distinct_pagination(client, common_query):
    page_num = 2
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
    page = json_data['data']['rareVariantPathwayAssociations']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_rareVariantPathwayAssociation_query_with_passed_data_set_feature_and_pathway(client, common_query, data_set, rvpa_feature, rvpa_pathway):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
        'feature': [rvpa_feature],
        'pathway': [rvpa_pathway]
    }})
    json_data = json.loads(response.data)
    page = json_data['data']['rareVariantPathwayAssociations']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert result['dataSet']['name'] == data_set
        assert result['feature']['name'] == rvpa_feature
        assert result['pathway'] == rvpa_pathway
        #assert type(result['pValue']) is float
        assert type(result['min']) is float
        assert type(result['max']) is float
        assert type(result['mean']) is float
        assert type(result['q1']) is float
        assert type(result['q2']) is float
        assert type(result['q3']) is float
        #assert type(result['nMutants']) is int
        #assert type(result['nTotal']) is int


def test_rareVariantPathwayAssociation_query_with_passed_min_p_value(client, common_query, data_set, rvpa_min_p_value):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'minPValue': rvpa_min_p_value
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['rareVariantPathwayAssociations']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['pValue']) is float
        assert result['pValue'] >= rvpa_min_p_value


def test_rareVariantPathwayAssociation_query_with_passed_max_p_value(client, common_query, data_set, rvpa_max_p_value):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'maxPValue': rvpa_max_p_value
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['rareVariantPathwayAssociations']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['pValue']) is float
        assert result['pValue'] <= rvpa_max_p_value


def test_rareVariantPathwayAssociation_query_with_no_arguments_no_relations(client, common_query_builder):
    query = common_query_builder("""{
            items {
                pathway
                pValue
                min
                max
                mean
                q1
                q2
                q3
                nMutants
                nTotal
            }
        }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['rareVariantPathwayAssociations']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['pathway']) is str
        assert type(result['pValue']) is float or NoneType
        assert type(result['min']) is float or NoneType
        assert type(result['max']) is float or NoneType
        assert type(result['mean']) is float or NoneType
        assert type(result['q1']) is float or NoneType
        assert type(result['q2']) is float or NoneType
        assert type(result['q3']) is float or NoneType
        assert type(result['nMutants']) is int or NoneType
        assert type(result['nTotal']) is int or NoneType
