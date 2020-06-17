import json
import pytest
from tests import client, NoneType


def test_genes_query(client):
    query = """query Genes($entrez: [Int!]) {
        genes(entrez: $entrez) {
            entrez
            hgnc
            geneFamily
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [3627]}})
    json_data = json.loads(response.data)
    genes = json_data["data"]["genes"]

    assert isinstance(genes, list)
    for gene in genes:
        assert gene["entrez"] == 3627
        assert gene["hgnc"] == "CXCL10"
        assert type(gene["geneFamily"]) is str or NoneType

    query = """query Genes($entrez: [Int!]) {
        genes(entrez: $entrez) {
            entrez
            hgnc
            geneFamily
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [135, 558, 3627]}})
    json_data = json.loads(response.data)
    genes = json_data["data"]["genes"]

    assert isinstance(genes, list)
    for gene in genes[0:1]:
        assert type(gene["entrez"]) is int
        assert type(gene["hgnc"]) is str
        assert type(gene["geneFamily"]) is str or NoneType
