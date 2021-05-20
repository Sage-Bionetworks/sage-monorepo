import json
import pytest
from api.database import return_gene_query
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash, Paging
from tests import NoneType
import logging


@pytest.fixture(scope='module')
def gene_type():
    return 'immunomodulator'


@pytest.fixture(scope='module')
def max_rna_seq_expr_1():
    return -0.727993495057642991952


@pytest.fixture(scope='module')
def min_rna_seq_expr_1():
    return 3424420


@pytest.fixture(scope='module')
def max_rna_seq_expr_2():
    return -0.377686024337191006417


@pytest.fixture(scope='module')
def min_rna_seq_expr_2():
    return -0.379707089801648023375


@pytest.fixture(scope='module')
def sample_name():
    return 'TCGA-27-1837'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Genes(
            $dataSet: [String!]
            $entrez: [Int!]
            $geneType: [String!]
            $maxRnaSeqExpr: Float
            $minRnaSeqExpr: Float
            $related: [String!]
            $sample: [String!]
            $tag: [String!]
            $paging: PagingInput
            $distinct: Boolean
        ) {
            genes(
                paging: $paging
                distinct: $distinct
                dataSet: $dataSet
                entrez: $entrez
                geneType: $geneType
                maxRnaSeqExpr: $maxRnaSeqExpr
                minRnaSeqExpr: $minRnaSeqExpr
                related: $related
                sample: $sample
                tag: $tag
            )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder("""
        {
            items {
                entrez
                hgnc
                geneFamily
                geneFunction
                geneTypes {
                    name
                    display
                }
                immuneCheckpoint
                pathway
                publications {
                    firstAuthorLastName
                    journal
                    pubmedId
                    title
                    year
                }
                superCategory
                therapyType
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
        }"""
                                )


def test_cursor_pagination_first(client, common_query_builder):
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
    page = json_data['data']['genes']
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


def test_cursor_pagination_last(client, common_query_builder):
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
    page = json_data['data']['genes']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == num
    assert paging['hasNextPage'] == False
    assert paging['hasPreviousPage'] == True
    assert start == items[0]['id']
    assert end == items[num - 1]['id']


def test_cursor_distinct_pagination(client, common_query):
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
    page = json_data['data']['genes']
    items = page['items']

    assert len(items) == num
    assert page_num == page['paging']['page']


def test_genes_query_with_entrez(client, common_query, entrez, hgnc):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'entrez': [entrez]}})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        gene_types = result['geneTypes']
        publications = result['publications']

        assert result['entrez'] == entrez
        assert result['hgnc'] == hgnc
        assert type(result['geneFamily']) is str or NoneType
        assert type(result['geneFunction']) is str or NoneType
        assert isinstance(gene_types, list)
        if gene_types:
            for gene_type in gene_types:
                assert type(gene_type['name']) is str
                assert type(gene_type['display']) is str or NoneType
        assert type(result['immuneCheckpoint']) is str or NoneType
        assert type(result['pathway']) is str or NoneType
        assert isinstance(publications, list)
        if publications:
            for publication in publications:
                assert type(
                    publication['firstAuthorLastName']) is str or NoneType
                assert type(publication['journal']) is str or NoneType
                assert type(publication['pubmedId']) is int
                assert type(publication['title']) is str or NoneType
                assert type(publication['year']) is str or NoneType
        assert type(result['superCategory']) is str or NoneType
        assert type(result['therapyType']) is str or NoneType


def test_genes_query_with_gene_type(client, common_query, entrez, gene_type):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'entrez': [entrez], 'geneType': [gene_type]}})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        gene_types = result['geneTypes']

        assert result['entrez'] == entrez
        assert isinstance(gene_types, list)
        for current_gene_type in gene_types:
            assert current_gene_type['name'] == gene_type


def test_genes_query_with_sample(client, common_query_builder, entrez, gene_type, sample_name):
    query = common_query_builder(
        """
        {
            items {
                entrez
                samples
            }
        }
        """
    )

    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [entrez], 'geneType': [gene_type], 'sample': [sample_name]}})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        samples = result['samples']

        assert result['entrez'] == entrez
        assert isinstance(samples, list)
        assert len(samples) == 1
        for current_sample in samples:
            assert current_sample == sample_name


def test_genes_query_with_dataSet_tag_and_maxRnaSeqExpr(client, common_query_builder, data_set, max_rna_seq_expr_1, tag):
    query = common_query_builder(
        """
        {
            items{
                entrez
                rnaSeqExprs
            }
        }
        """
    )
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'maxRnaSeqExpr': max_rna_seq_expr_1,
            'tag': tag
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:3]:
        rna_seq_exprs = result['rnaSeqExprs']
        assert type(result['entrez']) is int
        assert isinstance(rna_seq_exprs, list)
        assert len(rna_seq_exprs) > 0
        for rna_seq_expr in rna_seq_exprs[0:3]:
            assert rna_seq_expr <= max_rna_seq_expr_1


def test_genes_query_with_dataSet_and_minRnaSeqExpr(client, common_query_builder, data_set, min_rna_seq_expr_1):
    query = common_query_builder(
        """
        {
            items{
                entrez
                rnaSeqExprs
            }
        }
        """
    )
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'minRnaSeqExpr': min_rna_seq_expr_1
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:3]:
        rna_seq_exprs = result['rnaSeqExprs']
        assert type(result['entrez']) is int
        assert isinstance(rna_seq_exprs, list)
        assert len(rna_seq_exprs) > 0
        for rna_seq_expr in rna_seq_exprs[0:3]:
            assert rna_seq_expr >= min_rna_seq_expr_1


def test_genes_query_with_dataSet_tag_maxRnaSeqExpr_and_minRnaSeqExpr(client, common_query_builder, data_set, max_rna_seq_expr_2, min_rna_seq_expr_2, tag):
    query = common_query_builder(
        """
        {
            items{
                entrez
                rnaSeqExprs
            }
        }
        """
    )
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'maxRnaSeqExpr': max_rna_seq_expr_2,
            'minRnaSeqExpr': min_rna_seq_expr_2,
            'tag': tag
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:3]:
        rna_seq_exprs = result['rnaSeqExprs']
        assert type(result['entrez']) is int
        assert isinstance(rna_seq_exprs, list)
        assert len(rna_seq_exprs) > 0
        for rna_seq_expr in rna_seq_exprs[0:3]:
            assert rna_seq_expr <= max_rna_seq_expr_2
            assert rna_seq_expr >= min_rna_seq_expr_2


def test_genes_query_no_entrez(client, common_query_builder):
    query = common_query_builder(
        """
        {
            items{
                entrez
                hgnc
            }
        }
        """
    )
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 10
    for gene in results[0:1]:
        assert type(gene['entrez']) is int
        assert type(gene['hgnc']) is str


def test_genes_query_returns_publications(client, common_query_builder, entrez, hgnc):
    query = common_query_builder(
        """
        {
            items{
                entrez
                hgnc
                publications { pubmedId }
            }
        }
        """
    )
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [entrez]}})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        publications = result['publications']

        assert result['entrez'] == entrez
        assert result['hgnc'] == hgnc
        assert isinstance(publications, list)
        assert len(publications) > 0
        for publication in publications[0:5]:
            assert type(publication['pubmedId']) is int


def test_genes_query_returns_publications_with_geneType(client, common_query_builder, entrez, gene_type, hgnc):
    query = common_query_builder(
        """
        {
            items{
                entrez
                hgnc
                publications { pubmedId }
            }
        }
        """
    )
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [entrez], 'geneType': [gene_type]}})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        publications = result['publications']

        assert result['entrez'] == entrez
        assert result['hgnc'] == hgnc
        assert isinstance(publications, list)
        assert len(publications) > 0
        for publication in publications[0:5]:
            assert type(publication['pubmedId']) is int


def test_genes_query_returns_samples_and_rnaSeqExprs(client, common_query_builder, data_set, max_rna_seq_expr_2, min_rna_seq_expr_2, tag):
    query = common_query_builder(
        """
        {
            items{
                entrez
                samples
                rnaSeqExprs
            }
        }
        """
    )
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'maxRnaSeqExpr': max_rna_seq_expr_2,
            'minRnaSeqExpr': min_rna_seq_expr_2,
            'tag': tag
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        samples = result['samples']
        samples_length = len(samples)
        rna_seq_exprs = result['rnaSeqExprs']
        rna_seq_exprs_length = len(rna_seq_exprs)

        assert type(result['entrez']) is int
        assert isinstance(samples, list)
        assert samples_length > 0
        for sample_name in samples[0:5]:
            assert type(sample_name) is str
        assert isinstance(rna_seq_exprs, list)
        assert rna_seq_exprs_length > 0
        for rna_seq_expr in rna_seq_exprs[0:5]:
            assert type(rna_seq_expr) is float
        assert samples_length == rna_seq_exprs_length
