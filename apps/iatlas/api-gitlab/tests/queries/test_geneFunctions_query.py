import json
import pytest
from api.database import return_gene_function_query
from tests import NoneType


@pytest.fixture(scope='module')
def gene_function():
    return 'Immune suppressor'


def test_gene_functions_query_with_passed_gene_function_name(client, gene_function):
    query = """query geneFunctions($name: [String!]) {
        geneFunctions(name: $name) {
            genes { entrez }
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': gene_function}})
    json_data = json.loads(response.data)
    results = json_data['data']['geneFunctions']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        genes = result['genes']
        assert result['name'] == gene_function
        assert isinstance(genes, list)
        assert len(genes) > 0
        for gene in genes[0:2]:
            assert type(gene['entrez']) is int


def test_gene_functions_query_with_passed_gene_function_no_genes(client, gene_function):
    query = """query geneFunctions($name: [String!]) {
        geneFunctions(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': gene_function}})
    json_data = json.loads(response.data)
    results = json_data['data']['geneFunctions']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        assert result['name'] == gene_function


def test_gene_functionss_query_no_args(client):
    query = """query geneFunctions($name: [String!]) {
        geneFunctions(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['geneFunctions']

    gene_function_count = return_gene_function_query('id').count()

    assert isinstance(results, list)
    assert len(results) == gene_function_count
    for result in results[0:1]:
        assert type(result['name']) is str
