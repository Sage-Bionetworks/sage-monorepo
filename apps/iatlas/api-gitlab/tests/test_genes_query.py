import json
import pytest
from tests import client, NoneType

gene_id = 3627
hgnc = "CXCL10"


def test_genes_query_with_entrez(client):
    query = """query Genes($entrez: [Int!]) {
        genes(entrez: $entrez) {
            entrez
            hgnc
            geneFamily
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [gene_id]}})
    json_data = json.loads(response.data)
    genes = json_data["data"]["genes"]

    assert isinstance(genes, list)
    for gene in genes:
        assert gene["entrez"] == gene_id
        assert gene["hgnc"] == hgnc
        assert type(gene["geneFamily"]) is str or NoneType


def test_genes_query_no_entrez(client):
    query = """query Genes($entrez: [Int!]) {
        genes(entrez: $entrez) {
            entrez
            hgnc
            geneFamily
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    genes = json_data["data"]["genes"]

    assert isinstance(genes, list)
    for gene in genes[0:1]:
        assert type(gene["entrez"]) is int
        assert type(gene["hgnc"]) is str
        assert type(gene["geneFamily"]) is str or NoneType
