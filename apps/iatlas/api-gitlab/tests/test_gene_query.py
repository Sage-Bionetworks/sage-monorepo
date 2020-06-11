import json
import pytest
from tests import client, NoneType


def test_gene_query_with_relations(client):
    query = """query Gene($entrez: Int!) {
        gene(entrez: $entrez) {
            entrez
            hgnc
            ioLandscapeName
            references
            geneFamily
        }
    }"""
    entrez = 3627
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': entrez}})
    json_data = json.loads(response.data)
    gene = json_data["data"]["gene"]

    assert not isinstance(gene, list)
    assert gene["entrez"] == entrez
    assert gene["hgnc"] == "CXCL10"
    assert type(gene["ioLandscapeName"]) is str or NoneType
    assert isinstance(gene["references"], list) or NoneType
    assert type(gene["geneFamily"]) is str or NoneType


def test_gene_query_no_relations(client):
    query = """query Gene($entrez: Int!) {
        gene(entrez: $entrez) {
            entrez
            hgnc
            ioLandscapeName
            references
        }
    }"""
    entrez = 3627
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': entrez}})
    json_data = json.loads(response.data)
    gene = json_data["data"]["gene"]

    assert not isinstance(gene, list)
    assert gene["entrez"] == entrez
    assert gene["hgnc"] == "CXCL10"
    assert type(gene["ioLandscapeName"]) is str or NoneType
    assert isinstance(gene["references"], list) or NoneType
