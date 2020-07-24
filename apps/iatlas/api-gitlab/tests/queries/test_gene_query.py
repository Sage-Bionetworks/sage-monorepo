import json
import pytest
from tests import NoneType


def test_gene_query_with_relations(client, entrez, hgnc):
    query = """query Gene($entrez: Int!) {
        gene(entrez: $entrez) {
            entrez
            hgnc
            ioLandscapeName
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
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': entrez}})
    json_data = json.loads(response.data)
    gene = json_data['data']['gene']
    gene_types = gene['geneTypes']
    publications = gene['publications']

    assert not isinstance(gene, list)
    assert gene['entrez'] == entrez
    assert gene['hgnc'] == hgnc
    assert type(gene['ioLandscapeName']) is str or NoneType
    assert type(gene['geneFamily']) is str or NoneType
    assert isinstance(gene_types, list)
    if gene_types:
        for gene_type in gene_types[0:2]:
            assert type(gene_type['name']) is str
            assert type(gene_type['display']) is str or NoneType
    assert isinstance(publications, list)
    if publications:
        for publication in publications[0:2]:
            assert type(publication['firstAuthorLastName']) is str or NoneType
            assert type(publication['journal']) is str or NoneType
            assert type(publication['pubmedId']) is int
            assert type(publication['title']) is str or NoneType
            assert type(publication['year']) is str or NoneType


def test_gene_query_get_rnSeqExpr(client, entrez):
    query = """query Gene($entrez: Int!) {
        gene(entrez: $entrez) {
            entrez
            samples {
                name
                rnaSeqExpr
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': entrez}})
    json_data = json.loads(response.data)
    gene = json_data['data']['gene']
    samples = gene['samples']

    assert not isinstance(gene, list)
    assert gene['entrez'] == entrez
    assert isinstance(samples, list)
    assert len(samples) > 0
    for current_sample in samples[0:2]:
        assert type(current_sample['name']) is str
        assert type(current_sample['rnaSeqExpr']) is float or NoneType


def test_gene_query_get_rnSeqExpr_with_passed_sample(client, entrez, sample):
    query = """query Gene($entrez: Int!, $sample: [String!]) {
        gene(entrez: $entrez, sample: $sample) {
            entrez
            samples { name }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': entrez, 'sample': [sample]}})
    json_data = json.loads(response.data)
    gene = json_data['data']['gene']
    samples = gene['samples']

    assert not isinstance(gene, list)
    assert gene['entrez'] == entrez
    assert isinstance(samples, list)
    assert len(samples) == 1
    for current_sample in samples:
        assert current_sample['name'] == sample


def test_gene_query_no_relations(client, entrez, hgnc):
    query = """query Gene($entrez: Int!) {
        gene(entrez: $entrez) {
            entrez
            hgnc
            ioLandscapeName
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': entrez}})
    json_data = json.loads(response.data)
    gene = json_data['data']['gene']

    assert not isinstance(gene, list)
    assert gene['entrez'] == entrez
    assert gene['hgnc'] == hgnc
    assert type(gene['ioLandscapeName']) is str or NoneType


def test_gene_query_bad_entrez(client, entrez, hgnc):
    query = """query Gene($entrez: Int!) {
        gene(entrez: $entrez) {
            entrez
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': 9999999999}})
    json_data = json.loads(response.data)
    gene = json_data['data']

    assert gene == None
