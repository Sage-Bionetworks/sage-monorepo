import json
import pytest
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging

@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Cells(
        $paging: PagingInput
        $distinct:Boolean
        $entrez: [Int!]
        $feature: [String!]
        $cohort: [String!]
        $cell: [String!]
    ) {
        cells(
            paging: $paging
            distinct: $distinct
            entrez: $entrez
            feature: $feature
            cohort: $cohort
            cell: $cell
        )""" + query_fields + "}"
    return f



@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder(
        """
    {
        items {
            name
            type
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

@pytest.fixture(scope='module')
def genes_query(common_query_builder):
    return common_query_builder(
        """
    {
        items {
            name
            type
            genes {
                entrez
                hgnc
                singleCellSeq
            }
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

@pytest.fixture(scope='module')
def features_query(common_query_builder):
    return common_query_builder(
        """
    {
        items {
            name
            type
            features {
                name
                display
                value
            }

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

def test_cells_cursor_pagination_first(client, common_query_builder):
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
    page = json_data['data']['cells']

    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == True
    assert paging['hasPreviousPage'] == False
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_cells_cursor_pagination_last(client, common_query_builder):
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
    page = json_data['data']['cells']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_cells_cursor_distinct_pagination(client, common_query):
    page_num = 2
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
    page = json_data['data']['cells']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_cell_query_with_no_arguments(client, common_query):
    response = client.post('/api', json={'query': common_query})
    json_data = json.loads(response.data)
    page = json_data['data']['cells']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 10
    for result in results[0:10]:
        assert isinstance(result['name'], str)
        assert isinstance(result['type'], str)

def test_cell_query_with_cell(client, common_query):
    response = client.post(
        '/api',
        json={'query': common_query, 'variables': {'cell': ['RU1311A_T_1_165945547864806']}}
    )
    json_data = json.loads(response.data)
    page = json_data['data']['cells']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == 1
    result = results[0]
    assert result['name'] == 'RU1311A_T_1_165945547864806'
    assert isinstance(result['type'], str)

def test_cell_query_with_cohort(client, common_query):
    response = client.post(
        '/api',
        json={'query': common_query, 'variables': {'cohort': ['MSK_Biopsy_Site']}}
    )
    json_data = json.loads(response.data)
    page = json_data['data']['cells']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) > 10
    for result in results[0:10]:
        assert isinstance(result['name'], str)
        assert isinstance(result['type'], str)


def test_cell_query_with_entrez_and_cell(client, genes_query):
    response = client.post(
        '/api',
        json={
            'query': genes_query,
            'variables': {
                'entrez': [54890],
                'cell': ['TGAATACCCAGAGCGTAGG-5']
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['cells']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == 1
    result = results[0]
    assert result['name'] == 'TGAATACCCAGAGCGTAGG-5'
    assert isinstance(result['type'], str)
    genes = result['genes']
    assert isinstance(genes, list)
    assert len(genes) == 1
    gene =  genes[0]
    assert gene['entrez'] == 54890
    assert isinstance(gene['hgnc'], str)
    assert isinstance(gene['singleCellSeq'], float)

def test_cell_query_with_entrez_and_feature(client, features_query):
    response = client.post(
        '/api',
        json={
            'query': features_query,
            'variables': {
                'feature': ['umap_1'],
                'cell': ['RU1311A_T_1_165945547864806']
            }
        }
    )
    json_data = json.loads(response.data)
    page = json_data['data']['cells']
    results = page['items']
    assert isinstance(results, list)
    assert len(results) == 1
    result = results[0]
    assert result['name'] == 'RU1311A_T_1_165945547864806'
    assert isinstance(result['type'], str)
    features = result['features']
    assert isinstance(features, list)
    assert len(features) == 1
    feature = features[0]
    assert feature['name'] == 'umap_1'
    assert isinstance(feature['display'], str)
    assert isinstance(feature['value'], float)