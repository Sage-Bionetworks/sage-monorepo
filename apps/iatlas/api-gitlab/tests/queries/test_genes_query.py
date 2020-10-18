import json
import pytest
from api.database import return_gene_query
from tests import NoneType


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
            $maxRnaSeqExp: Float
            $minRnaSeqExp: Float
            $related: [String!]
            $sample: [String!]
            $tag: [String!]
        ) {
        genes(
            dataSet: $dataSet
            entrez: $entrez
            geneType: $geneType
            maxRnaSeqExp: $maxRnaSeqExp
            minRnaSeqExp: $minRnaSeqExp
            related: $related
            sample: $sample
            tag: $tag
        )""" + query_fields + "}"
    return f


def test_genes_query_with_entrez(client, common_query_builder, entrez, hgnc):
    query = common_query_builder("""{
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
        }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [entrez]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genes']

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


def test_genes_query_with_gene_type(client, common_query_builder, entrez, gene_type):
    query = common_query_builder("""{
            entrez
            geneTypes { name }
        }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [entrez], 'geneType': [gene_type]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genes']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        gene_types = result['geneTypes']

        assert result['entrez'] == entrez
        assert isinstance(gene_types, list)
        for current_gene_type in gene_types:
            assert current_gene_type['name'] == gene_type


def test_genes_query_with_sample(client, common_query_builder, entrez, gene_type, sample_name):
    query = common_query_builder("""{
            entrez
            publications { pubmedId }
            samples {
                name
                rnaSeqExpr
            }
        }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [entrez], 'geneType': [gene_type], 'sample': [sample_name]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genes']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        samples = result['samples']

        assert result['entrez'] == entrez
        assert isinstance(samples, list)
        assert len(samples) == 1
        for current_sample in samples:
            assert current_sample['name'] == sample_name
            assert type(current_sample['rnaSeqExpr']) is float


def test_genes_query_with_dataSet_tag_and_maxRnaSeqExpr(client, common_query_builder, data_set, max_rna_seq_expr_1, tag):
    query = common_query_builder("""{
            entrez
            samples {
                name
                rnaSeqExpr
            }
        }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'maxRnaSeqExpr': max_rna_seq_expr_1,
            'tag': tag
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['genes']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:3]:
        samples = result['samples']
        assert result['entrez'] == entrez
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:3]:
            assert type(current_sample['name']) is str
            assert current_sample['rnaSeqExpr'] <= float(max_rna_seq_expr_1)


def test_genes_query_with_dataSet_tag_and_minRnaSeqExpr(client, common_query_builder, min_rna_seq_expr_1, tag):
    query = common_query_builder("""{
            entrez
            samples {
                name
                rnaSeqExpr
            }
        }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'minRnaSeqExpr': min_rna_seq_expr_1,
            'tag': tag
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['genes']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:3]:
        samples = result['samples']
        assert result['entrez'] == entrez
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:3]:
            assert type(current_sample['name']) is str
            assert current_sample['rnaSeqExpr'] >= min_rna_seq_expr_1


def test_genes_query_with_dataSet_tag_maxRnaSeqExpr_and_minRnaSeqExpr(client, common_query_builder, max_rna_seq_expr_2, min_rna_seq_expr_2, tag):
    query = common_query_builder("""{
            entrez
            samples {
                name
                rnaSeqExpr
            }
        }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'maxRnaSeqExpr': max_rna_seq_expr_2,
            'minRnaSeqExpr': min_rna_seq_expr_2,
            'tag': tag
        }})
    json_data = json.loads(response.data)
    results = json_data['data']['genes']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:3]:
        samples = result['samples']
        assert result['entrez'] == entrez
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:3]:
            assert type(current_sample['name']) is str
            assert current_sample['rnaSeqExpr'] <= max_rna_seq_expr_2
            assert current_sample['rnaSeqExpr'] >= min_rna_seq_expr_2


def test_genes_query_no_entrez(client, common_query_builder):
    query = common_query_builder("""{
            entrez
            hgnc
        }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['genes']

    # Get the total number of features in the database.
    gene_count = return_gene_query('id').count()

    assert isinstance(results, list)
    assert len(results) == gene_count
    for gene in results[0:1]:
        assert type(gene['entrez']) is int
        assert type(gene['hgnc']) is str
