import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging
from api.database import return_cohort_query
import logging


@pytest.fixture(scope='module')
def parent_tag():
    return "immune_subtype"


'''
@pytest.fixture(scope='module')
def coloc_feature(test_db):
    return 'Bindea_aDC'


@pytest.fixture(scope='module')
def coloc_gene_entrez(test_db):
    return 4677


@pytest.fixture(scope='module')
def coloc_snp_name(test_db):
    return "18:55726795:A:T"


@pytest.fixture(scope='module')
def coloc_qtl_type(test_db):
    return "eQTL"


@pytest.fixture(scope='module')
def coloc_ecaviar_pp(test_db):
    return "C2"


@pytest.fixture(scope='module')
def coloc_plot_type(test_db):
    return "Expanded Region"


@pytest.fixture(scope='module')
def coloc_tissue(test_db):
    return "Artery Aorta"
'''


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """
        query Cohorts(
            $paging: PagingInput
            $distinct:Boolean
            $name: [String!]
            $dataSet: [String!]
            $tag: [String!]
            $clinical: [String!]
        ) {
        cohorts(
            paging: $paging
            distinct: $distinct
            name: $name
            dataSet: $dataSet
            tag: $tag
            clinical: $clinical
        )
        """ + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder(
        """
    {
        items {
            name
            tag { name }
            clinical
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
    }
    """
    )

# Test that forward cursor pagination gives us the expected paginInfo


def test_cohorts_cursor_pagination_first(client, common_query_builder):
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
    num = 5
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {'first': num}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
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


def test_cohorts_cursor_pagination_last(client, common_query_builder):
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
    num = 5
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {
                'last': num,
                'before': to_cursor_hash(1000)
            }
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_cohorts_cursor_distinct_pagination(client, common_query):
    page_num = 2
    num = 2
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
    page = json_data['data']['cohorts']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_cohorts_unique_tag_query(client, common_query, data_set, parent_tag):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set]
    }})
    logger = logging.getLogger('logger name here')
    logger.info(data_set)
    logger.info(parent_tag)

    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']

    assert isinstance(results, list)
    #assert len(results) == 1
    for result in results:
        logger.info(results)
        #assert result['dataSet']['name'] == data_set
        #assert result['tag']['name'] == parent_tag
        assert type(result['name']) is str
        assert type(result['clinical']) is NoneType
    assert 1 == 2


'''
def test_cohorts_query_with_no_arguments(client, common_query):
    response = client.post('/api', json={'query': common_query})
    json_data = json.loads(response.data)
    page = json_data['data']['cohorts']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 10
    for result in results[1:10]:
        assert type(result['dataSet']['name']) is str
        assert type(result['colocDataSet']['name']) is str
        assert type(result['feature']['name']) is str
        assert type(result['gene']['entrez']) is int
        assert type(result['snp']['name']) is str
        assert type(result['qtlType']) is str
        assert type(result['eCaviarPP']) is str or NoneType
        assert type(result['plotType']) is str or NoneType
        assert type(result['tissue']) is str or NoneType
        assert type(result['spliceLoc']) is str or NoneType
        assert type(result['plotLink']) is str or NoneType
'''
