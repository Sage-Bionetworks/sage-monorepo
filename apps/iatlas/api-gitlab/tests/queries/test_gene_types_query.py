import json
import pytest
from api.database import return_gene_type_query
from tests import NoneType


@pytest.fixture(scope='module')
def gene_type():
    return 'CD8_CD68_ratio'


def test_gene_types_query_with_passed_gene_type(client, gene_type):
    query = """query GeneTypes($name: [String!]) {
        geneTypes(name: $name) {
            display
            genes { entrez }
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': [gene_type]}})
    json_data = json.loads(response.data)
    results = json_data['data']['geneTypes']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        genes = result['genes']
        assert result['name'] == gene_type
        assert type(result['display']) is str or NoneType
        assert isinstance(genes, list)
        assert len(genes) > 0
        for gene in genes[0:2]:
            assert type(gene['entrez']) is int


def test_gene_types_query_with_passed_gene_type_no_genes(client, gene_type):
    query = """query GeneTypes($name: [String!]) {
        geneTypes(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': [gene_type]}})
    json_data = json.loads(response.data)
    results = json_data['data']['geneTypes']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        assert result['name'] == gene_type


def test_gene_types_query_no_args(client):
    query = """query GeneTypes($name: [String!]) {
        geneTypes(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['geneTypes']

    gene_type_count = return_gene_type_query('id').count()

    assert isinstance(results, list)
    assert len(results) == gene_type_count
    for result in results[0:1]:
        assert type(result['name']) is str
