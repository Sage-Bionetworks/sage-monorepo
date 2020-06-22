import json
import pytest
from tests import client, NoneType

entrez = 3627
hgnc = 'CXCL10'


def test_gene_query_with_relations(client):
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
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': entrez}})
    json_data = json.loads(response.data)
    gene = json_data['data']['gene']
    gene_types = gene['geneTypes']

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


def test_gene_query_no_relations(client):
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
