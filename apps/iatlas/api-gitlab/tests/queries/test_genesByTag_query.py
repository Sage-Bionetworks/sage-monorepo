import json
import pytest
from tests import NoneType
from api.database import return_feature_class_query


@pytest.fixture(scope='module')
def gene_type():
    return 'extra_cellular_network'


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
def common_query_builder():
    def f(query_fields):
        return """query GenesByTag(
            $dataSet: [String!]
            $entrez: [Int!]
            $feature: [String!]
            $featureClass: [String!]
            $geneType: [String!]
            $maxRnaSeqExpr: Float
            $minRnaSeqExpr: Float
            $related: [String!]
            $sample: [String!]
            $tag: [String!]
        ) {
            genesByTag(
                dataSet: $dataSet
                entrez: $entrez
                feature: $feature
                featureClass: $featureClass
                geneType: $geneType
                maxRnaSeqExpr: $maxRnaSeqExpr
                minRnaSeqExpr: $minRnaSeqExpr
                related: $related
                sample: $sample
                tag: $tag
            )""" + query_fields + "}"
    return f


def test_genesByTag_query_with_entrez(client, common_query_builder, data_set, related, entrez, hgnc):
    query = common_query_builder("""{
            tag
            characteristics
            shortDisplay
            genes {
                entrez
                hgnc
            }
        }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'entrez': [entrez]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        genes = result['genes']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert isinstance(genes, list)
        assert len(genes) == 1
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            assert gene['entrez'] == entrez
            assert gene['hgnc'] == hgnc


def test_genesByTag_query_no_entrez(client, common_query_builder, data_set, related, tag):
    query = common_query_builder("""{
            tag
            genes { entrez }
        }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'tag': [tag]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        genes = result['genes']
        assert result['tag'] == tag
        assert isinstance(genes, list)
        assert len(genes) > 0
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            assert type(gene['entrez']) is int


def test_genesByTag_query_no_relations(client, common_query_builder, data_set, related, entrez, hgnc):
    query = common_query_builder("""{
            tag
            characteristics
            longDisplay
            genes {
                entrez
                hgnc
            }
        }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'entrez': [entrez]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        genes = result['genes']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['longDisplay']) is str or NoneType
        assert isinstance(genes, list)
        assert len(genes) == 1
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            assert gene['entrez'] == entrez
            assert gene['hgnc'] == hgnc


def test_genesByTag_query_with_gene_type(client, common_query_builder, data_set, related, entrez, hgnc, gene_type):
    query = common_query_builder("""{
            tag
            characteristics
            shortDisplay
            genes {
                entrez
                hgnc
            }
        }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'entrez': [entrez],
                                    'geneType': [gene_type]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        genes = result['genes']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert isinstance(genes, list)
        assert len(genes) == 1
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            assert gene['entrez'] == entrez
            assert gene['hgnc'] == hgnc


def test_genesByTag_query_with_sample(client, common_query_builder, data_set, related, entrez, hgnc, gene_type, sample):
    query = common_query_builder("""{
            tag
            characteristics
            shortDisplay
            genes {
                entrez
                hgnc
            }
        }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'entrez': [entrez],
                                    'geneType': [gene_type],
                                    'sample': [sample]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        genes = result['genes']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert isinstance(genes, list)
        assert len(genes) == 1
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            assert gene['entrez'] == entrez
            assert gene['hgnc'] == hgnc


def test_genesByTag_query_with_maxRnaSeqExpr(client, common_query_builder, data_set, max_rna_seq_expr_1, related, tag):
    query = common_query_builder("""{
            tag
            genes {
                entrez
                samples {
                    name
                    rnaSeqExpr
                }
            }
        }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'maxRnaSeqExpr': max_rna_seq_expr_1,
                                    'related': [related],
                                    'tag': [tag]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        genes = result['genes']
        assert result['tag'] == tag
        assert isinstance(genes, list)
        assert len(genes) > 0
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            samples = gene['samples']
            assert type(gene['entrez']) is int
            assert isinstance(samples, list)
            assert len(samples) > 0
            for current_sample in samples[0:3]:
                assert type(current_sample['name']) is str
                assert current_sample['rnaSeqExpr'] <= max_rna_seq_expr_1


def test_genesByTag_query_with_minRnaSeqExpr(client, common_query_builder, data_set, min_rna_seq_expr_1, related):
    query = common_query_builder("""{
            tag
            genes {
                entrez
                samples {
                    name
                    rnaSeqExpr
                }
            }
        }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'minRnaSeqExpr': min_rna_seq_expr_1,
                                    'related': [related]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:3]:
        genes = result['genes']
        assert type(result['tag']) is str
        assert isinstance(genes, list)
        assert len(genes) > 0
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            samples = gene['samples']
            assert type(gene['entrez']) is int
            assert isinstance(samples, list)
            assert len(samples) > 0
            for current_sample in samples[0:3]:
                assert type(current_sample['name']) is str
                assert current_sample['rnaSeqExpr'] >= min_rna_seq_expr_1


def test_genesByTag_query_with_minRnaSeqExpr_and_maxRnaSeqExpr(client, common_query_builder, data_set, max_rna_seq_expr_2, min_rna_seq_expr_2, related, tag):
    query = common_query_builder("""{
            tag
            genes {
                entrez
                samples {
                    name
                    rnaSeqExpr
                }
            }
        }""")
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'maxRnaSeqExpr': max_rna_seq_expr_2,
                                    'minRnaSeqExpr': min_rna_seq_expr_2,
                                    'related': [related],
                                    'tag': [tag]}})
    json_data = json.loads(response.data)
    results = json_data['data']['genesByTag']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        genes = result['genes']
        assert result['tag'] == tag
        assert isinstance(genes, list)
        assert len(genes) > 0
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            samples = gene['samples']
            assert type(gene['entrez']) is int
            assert isinstance(samples, list)
            assert len(samples) > 0
            for current_sample in samples[0:3]:
                assert type(current_sample['name']) is str
                assert current_sample['rnaSeqExpr'] <= max_rna_seq_expr_2
                assert current_sample['rnaSeqExpr'] >= min_rna_seq_expr_2
