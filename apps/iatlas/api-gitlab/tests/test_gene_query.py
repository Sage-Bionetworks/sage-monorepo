import json
import pytest
from tests import client, NoneType


def test_gene_query(client):
    query = """query Gene($entrez: Int!) {
        gene(entrez: $entrez) {
            entrez
            hgnc
            geneFamily
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': 3627}})
    json_data = json.loads(response.data)
    gene = json_data["data"]["gene"]

    assert not isinstance(gene, list)
    assert gene["entrez"] == 3627
    assert gene["hgnc"] == "CXCL10"
    assert type(gene["geneFamily"]) is str or NoneType
