import json
import pytest
from tests import NoneType
from api.database import return_feature_class_query


@pytest.fixture(scope='module')
def gene_type():
    return 'extra_cellular_network'


def test_genesByTag_query_with_entrez(client, dataset, related, entrez, hgnc):
    query = """query GenesByTag($dataSet: [String!]!, $related: [String!]!, $entrez: [Int!], $geneType: [String!]) {
        genesByTag(dataSet: $dataSet, related: $related, entrez: $entrez, geneType: $geneType) {
            tag
            characteristics
            display
            genes {
                entrez
                hgnc
                geneFamily
                geneTypes {
                    name
                    display
                }
                publications {
                    firstAuthorLastName
                    journal
                    pubmedId
                    title
                    year
                }
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related],
                                    'entrez': [entrez]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['genesByTag']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        genes = data_set['genes']
        assert type(data_set['tag']) is str
        assert type(data_set['characteristics']) is str or NoneType
        assert type(data_set['display']) is str or NoneType
        assert isinstance(genes, list)
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            gene_types = gene['geneTypes']
            pubs = gene['publications']
            assert gene['entrez'] == entrez
            assert gene['hgnc'] == hgnc
            assert type(gene['geneFamily']) is str or NoneType
            if gene_types:
                assert isinstance(gene_types, list)
                for current_type in gene_types:
                    assert type(current_type['name']) is str
                    assert type(current_type['display']) is str or NoneType
            if pubs:
                assert isinstance(pubs, list)
                for pub in pubs:
                    assert type(pub['firstAuthorLastName']) is str or NoneType
                    assert type(pub['journal']) is str or NoneType
                    assert type(pub['pubmedId']) is int
                    assert type(pub['title']) is str or NoneType
                    assert type(pub['year']) is int or NoneType


def test_genesByTag_query_no_entrez(client, dataset, related):
    query = """query GenesByTag($dataSet: [String!]!, $related: [String!]!, $entrez: [Int!], $geneType: [String!]) {
        genesByTag(dataSet: $dataSet, related: $related, entrez: $entrez, geneType: $geneType) {
            tag
            characteristics
            display
            genes {
                entrez
                hgnc
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['genesByTag']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        genes = data_set['genes']
        assert type(data_set['tag']) is str
        assert type(data_set['characteristics']) is str or NoneType
        assert type(data_set['display']) is str or NoneType
        assert isinstance(genes, list)
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            assert type(gene['entrez']) is int
            assert type(gene['hgnc']) is str


def test_genesByTag_query_no_relations(client, dataset, related, entrez, hgnc):
    query = """query GenesByTag($dataSet: [String!]!, $related: [String!]!, $entrez: [Int!], $geneType: [String!]) {
        genesByTag(dataSet: $dataSet, related: $related, entrez: $entrez, geneType: $geneType) {
            tag
            characteristics
            display
            genes {
                entrez
                hgnc
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related],
                                    'entrez': [entrez]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['genesByTag']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        genes = data_set['genes']
        assert type(data_set['tag']) is str
        assert type(data_set['characteristics']) is str or NoneType
        assert type(data_set['display']) is str or NoneType
        assert isinstance(genes, list)
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            assert gene['entrez'] == entrez
            assert gene['hgnc'] == hgnc


def test_genesByTag_query_with_gene_type(client, dataset, related, entrez, hgnc, gene_type):
    query = """query GenesByTag($dataSet: [String!]!, $related: [String!]!, $entrez: [Int!], $geneType: [String!]) {
        genesByTag(dataSet: $dataSet, related: $related, entrez: $entrez, geneType: $geneType) {
            tag
            characteristics
            display
            genes {
                entrez
                hgnc
                geneTypes {
                    name
                }
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [dataset],
                                    'related': [related],
                                    'entrez': [entrez],
                                    'geneType': [gene_type]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['genesByTag']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        genes = data_set['genes']
        assert type(data_set['tag']) is str
        assert type(data_set['characteristics']) is str or NoneType
        assert type(data_set['display']) is str or NoneType
        assert isinstance(genes, list)
        # Don't need to iterate through every result.
        for gene in genes[0:2]:
            gene_types = gene['geneTypes']
            assert gene['entrez'] == entrez
            assert gene['hgnc'] == hgnc
            assert isinstance(geneTypes, list)
            for current_type in gene_types:
                assert current_type['name'] == gene_type
