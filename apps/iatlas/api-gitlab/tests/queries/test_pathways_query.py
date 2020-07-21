import json
import pytest
from api.database import return_pathway_query
from tests import NoneType


@pytest.fixture(scope='module')
def pathway():
    return 'Angiogenesis'


def test_pathways_query_with_passed_pathway_name(client, pathway):
    query = """query pathways($name: [String!]) {
        pathways(name: $name) {
            genes { entrez }
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': pathway}})
    json_data = json.loads(response.data)
    results = json_data['data']['pathways']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        genes = result['genes']
        assert result['name'] == pathway
        assert isinstance(genes, list)
        assert len(genes) > 0
        for gene in genes[0:2]:
            assert type(gene['entrez']) is int


def test_pathways_query_with_passed_pathway_no_genes(client, pathway):
    query = """query pathways($name: [String!]) {
        pathways(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': pathway}})
    json_data = json.loads(response.data)
    results = json_data['data']['pathways']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        assert result['name'] == pathway


def test_pathwayss_query_no_args(client):
    query = """query pathways($name: [String!]) {
        pathways(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['pathways']

    pathway_count = return_pathway_query('id').count()

    assert isinstance(results, list)
    assert len(results) == pathway_count
    for result in results[0:1]:
        assert type(result['name']) is str
