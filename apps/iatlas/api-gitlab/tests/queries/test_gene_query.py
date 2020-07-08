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
        for gene_type in gene_types:
            assert type(gene_type['name']) is str
            assert type(gene_type['display']) is str or NoneType
    if publications:
        for publication in publications:
            assert type(publication['firstAuthorLastName']) is str or NoneType
            assert type(publication['journal']) is str or NoneType
            assert type(publication['pubmedId']) is int
            assert type(publication['title']) is str or NoneType
            assert type(publication['year']) is str or NoneType


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
