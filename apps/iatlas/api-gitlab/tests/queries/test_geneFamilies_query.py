import json
import pytest
from api.database import return_gene_family_query
from tests import NoneType


@pytest.fixture(scope='module')
def gene_family():
    return 'B7/CD28'


def test_gene_families_query_with_passed_gene_family_name(client, gene_family):
    query = """query geneFamilies($name: [String!]) {
        geneFamilies(name: $name) {
            genes { entrez }
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': gene_family}})
    json_data = json.loads(response.data)
    results = json_data['data']['geneFamilies']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        genes = result['genes']
        assert result['name'] == gene_family
        assert isinstance(genes, list)
        assert len(genes) > 0
        for gene in genes[0:2]:
            assert type(gene['entrez']) is int


def test_gene_families_query_with_passed_gene_family_no_genes(client, gene_family):
    query = """query geneFamilies($name: [String!]) {
        geneFamilies(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': gene_family}})
    json_data = json.loads(response.data)
    results = json_data['data']['geneFamilies']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        assert result['name'] == gene_family


def test_gene_familiess_query_no_args(client):
    query = """query geneFamilies($name: [String!]) {
        geneFamilies(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['geneFamilies']

    gene_family_count = return_gene_family_query('id').count()

    assert isinstance(results, list)
    assert len(results) == gene_family_count
    for result in results[0:1]:
        assert type(result['name']) is str
