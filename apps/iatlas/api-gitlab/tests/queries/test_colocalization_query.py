import json
import pytest
from tests import NoneType
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging
from api.database import return_colocalization_query
import logging


@pytest.fixture(scope='module')
def coloc_data_set(test_db):
    return "GTEX"


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


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Colocalizations(
        $paging: PagingInput
        $distinct:Boolean
        $dataSet: [String!]
        $colocDataSet: [String!]
        $feature: [String!]
        $entrez: [Int!]
        $snp: [String!]
        $qtlType: QTLTypeEnum
        $eCaviarPP: ECaviarPPEnum
        $plotType: ColocPlotTypeEnum
    ) {
        colocalizations(
            paging: $paging
            distinct: $distinct
            dataSet: $dataSet
            colocDataSet: $colocDataSet
            feature: $feature
            entrez: $entrez
            snp: $snp
            qtlType: $qtlType
            eCaviarPP: $eCaviarPP
            plotType: $plotType
        )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder(
        """
    {
        items {
            dataSet { name }
            colocDataSet { name }
            feature { name }
            gene { entrez }
            snp { name }
            qtlType
            eCaviarPP
            plotType
            tissue
            spliceLoc
            plotLink
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


def test_colocalizations_cursor_pagination_first(client, common_query_builder):
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
    page = json_data['data']['colocalizations']
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


def test_colocalizations_cursor_pagination_last(client, common_query_builder):
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
    page = json_data['data']['colocalizations']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_colocalizations_cursor_distinct_pagination(client, common_query):
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
    page = json_data['data']['colocalizations']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_colocalizations_unique_query(client, common_query, data_set, coloc_data_set, coloc_feature, coloc_gene_entrez, coloc_snp_name, coloc_qtl_type, coloc_ecaviar_pp, coloc_plot_type, coloc_tissue):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'dataSet': [data_set],
        'colocDataSet': [coloc_data_set],
        'feature': [coloc_feature],
        'entrez': [coloc_gene_entrez],
        'snp': [coloc_snp_name],
        'qtlType': coloc_qtl_type
    }})
    logger = logging.getLogger('logger name here')

    json_data = json.loads(response.data)
    page = json_data['data']['colocalizations']
    results = page['items']

    logger.info(results)

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert result['dataSet']['name'] == data_set
        assert result['colocDataSet']['name'] == coloc_data_set
        assert result['feature']['name'] == coloc_feature
        assert result['gene']['entrez'] == coloc_gene_entrez
        assert result['snp']['name'] == coloc_snp_name
        assert result['qtlType'] == coloc_qtl_type
        assert type(result['eCaviarPP']) is NoneType
        assert result['plotType'] == coloc_plot_type
        assert result['tissue'] == coloc_tissue
        assert type(result['spliceLoc']) is NoneType
        assert type(result['plotLink']) is str


def test_colocalizations_query_with_no_arguments(client, common_query):
    response = client.post('/api', json={'query': common_query})
    json_data = json.loads(response.data)
    page = json_data['data']['colocalizations']
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
