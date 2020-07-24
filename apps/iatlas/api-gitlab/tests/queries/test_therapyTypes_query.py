import json
import pytest
from api.database import return_therapy_type_query
from tests import NoneType


@pytest.fixture(scope='module')
def therapy_type():
    return 'T-cell targeted immunomodulator'


def test_therapy_types_query_with_passed_therapy_type_name(client, therapy_type):
    query = """query therapyTypes($name: [String!]) {
        therapyTypes(name: $name) {
            genes { entrez }
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': therapy_type}})
    json_data = json.loads(response.data)
    results = json_data['data']['therapyTypes']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        genes = result['genes']
        assert result['name'] == therapy_type
        assert isinstance(genes, list)
        assert len(genes) > 0
        for gene in genes[0:2]:
            assert type(gene['entrez']) is int


def test_therapy_types_query_with_passed_therapy_type_no_genes(client, therapy_type):
    query = """query therapyTypes($name: [String!]) {
        therapyTypes(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': therapy_type}})
    json_data = json.loads(response.data)
    results = json_data['data']['therapyTypes']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        assert result['name'] == therapy_type


def test_therapy_typess_query_no_args(client):
    query = """query therapyTypes($name: [String!]) {
        therapyTypes(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['therapyTypes']

    therapy_type_count = return_therapy_type_query('id').count()

    assert isinstance(results, list)
    assert len(results) == therapy_type_count
    for result in results[0:1]:
        assert type(result['name']) is str
