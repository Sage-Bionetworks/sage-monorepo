import json
import pytest
from api.resolvers.resolver_helpers.paging_utils import from_cursor_hash, to_cursor_hash
from tests import NoneType


@pytest.fixture(scope='module')
def gene_type():
    return 'immunomodulator'


@pytest.fixture(scope='module')
def gene_type2():
    return 'io_target'


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
def nanostring_sample():
    return "Prins_GBM_2019-SK08-ar-A07"

@pytest.fixture(scope='module')
def nanostring_entrez():
    return 259


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Genes(
            $entrez: [Int!]
            $geneType: [String!]
            $maxRnaSeqExpr: Float
            $minRnaSeqExpr: Float
            $cohort: [String!]
            $sample: [String!]
            $paging: PagingInput
            $distinct: Boolean
        ) {
            genes(
                paging: $paging
                distinct: $distinct
                cohort: $cohort
                entrez: $entrez
                geneType: $geneType
                maxRnaSeqExpr: $maxRnaSeqExpr
                minRnaSeqExpr: $minRnaSeqExpr
                sample: $sample
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


@pytest.fixture(scope='module')
def rnaseq_query(common_query_builder):
    return common_query_builder(
        """
        {
            items{
                entrez
                samples {
                    rnaSeqExpr
                    name
                }
            }
        }
        """
    )

@pytest.fixture(scope='module')
def nanostring_query(common_query_builder):
    return common_query_builder(
        """
        {
            items{
                entrez
                samples {
                    nanostringExpr
                    name
                }
            }
        }
        """
    )


def test_cursor_pagination_first_without_samples(client, common_query_builder):
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
    requested_n = 15
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {'first': requested_n}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == requested_n
    assert paging['hasNextPage'] == True
    assert paging['hasPreviousPage'] == False
    assert start == items[0]['id']
    assert end == items[requested_n - 1]['id']
    assert int(end) - int(start) > 0


def test_cursor_pagination_first_with_samples(client, common_query_builder):
    query = common_query_builder("""{
            items {
                id
                samples { name }
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
    requested_n = 15
    max_n = 10
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'paging': {'first': requested_n}
        }})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    items = page['items']
    paging = page['paging']
    start = from_cursor_hash(paging['startCursor'])
    end = from_cursor_hash(paging['endCursor'])

    assert len(items) == max_n
    assert paging['hasNextPage'] == True
    assert paging['hasPreviousPage'] == False
    assert start == items[0]['id']
    assert end == items[max_n - 1]['id']
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
            'distinct': True
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


def test_genes_query_with_gene_type2(client, common_query, entrez, gene_type2):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'entrez': [55], 'geneType': [gene_type2]}})
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert result['pathway'] == 'Innate Immune System'
        assert result['therapyType'] == 'Targeted by Other Immuno-Oncology Therapy Type'

        gene_types = result['geneTypes']

        assert isinstance(gene_types, list)
        for current_gene_type in gene_types:
            assert current_gene_type['name'] == gene_type2


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
    assert len(results) > 10
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


def test_genes_rnaseq_query_with_gene_and_cohort(client, entrez, rnaseq_query, tcga_tag_cohort_name, tcga_tag_cohort_samples):
    response = client.post(
        '/api', json={
            'query': rnaseq_query,
            'variables': {
                'entrez': [entrez],
                'cohort': [tcga_tag_cohort_name]
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    genes = page['items']
    assert isinstance(genes, list)
    assert len(genes) == 1
    gene = genes[0]
    assert gene['entrez'] == entrez
    samples = gene['samples']
    assert isinstance(samples, list)
    assert len(samples) > 1
    for sample in gene['samples'][0:10]:
        assert type(sample['name']) is str
        assert type(sample['rnaSeqExpr']) is float
        assert sample['name'] in tcga_tag_cohort_samples


def test_genes_rnaseq_query_with_gene_and_sample(client, entrez, rnaseq_query, sample):
    response = client.post(
        '/api', json={
            'query': rnaseq_query,
            'variables': {
                'entrez': [entrez],
                'sample': [sample]
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    genes = page['items']
    assert isinstance(genes, list)
    assert len(genes) == 1
    gene = genes[0]
    assert gene['entrez'] == entrez
    samples = gene['samples']
    assert isinstance(samples, list)
    assert len(samples) == 1
    s = samples[0]
    assert type(s['name']) is str
    assert type(s['rnaSeqExpr']) is float
    assert s['name'] == sample


def test_genes_query_with_entrez_and_maxRnaSeqExpr(client, rnaseq_query, entrez):
    max_rna_seq_expr = 1
    response = client.post(
        '/api', json={
            'query': rnaseq_query,
            'variables': {
                'maxRnaSeqExpr': max_rna_seq_expr,
                'entrez': entrez
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    genes = page['items']
    assert isinstance(genes, list)
    assert len(genes) == 1
    gene = genes[0]
    assert gene['entrez'] == entrez
    samples = gene['samples']
    assert isinstance(samples, list)
    assert len(samples) > 1
    for sample in gene['samples'][0:10]:
        assert type(sample['name']) is str
        assert type(sample['rnaSeqExpr']) is float
        assert sample['rnaSeqExpr'] <= max_rna_seq_expr


def test_genes_query_with_entrez_and_minRnaSeqExpr(client, rnaseq_query, entrez):
    min_rna_seq_expr = 1
    response = client.post(
        '/api', json={
            'query': rnaseq_query,
            'variables': {
                'minRnaSeqExpr': min_rna_seq_expr,
                'entrez': entrez
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    genes = page['items']
    assert isinstance(genes, list)
    assert len(genes) == 1
    gene = genes[0]
    assert gene['entrez'] == entrez
    samples = gene['samples']
    assert isinstance(samples, list)
    assert len(samples) > 1
    for sample in gene['samples'][0:10]:
        assert type(sample['name']) is str
        assert type(sample['rnaSeqExpr']) is float
        assert sample['rnaSeqExpr'] >= min_rna_seq_expr

def test_genes_nanostring_query_with_gene_and_sample(client, nanostring_query, nanostring_entrez, nanostring_sample):
    response = client.post(
        '/api', json={
            'query': nanostring_query,
            'variables': {
                'entrez': [nanostring_entrez],
                'sample': [nanostring_sample]
            }
        })
    json_data = json.loads(response.data)
    page = json_data['data']['genes']
    genes = page['items']
    assert isinstance(genes, list)
    assert len(genes) == 1
    gene = genes[0]
    assert gene['entrez'] == nanostring_entrez
    samples = gene['samples']
    assert isinstance(samples, list)
    assert len(samples) == 1
    s = samples[0]
    assert type(s['name']) is str
    assert type(s['nanostringExpr']) is float
    assert s['name'] == nanostring_sample