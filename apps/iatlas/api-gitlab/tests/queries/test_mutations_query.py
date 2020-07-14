import json
import pytest
from tests import NoneType


@pytest.fixture(scope='module')
def gene_entrez():
    return 92


@pytest.fixture(scope='module')
def mutation_code():
    return 'G12'


def test_mutations_query_with_passed_entrez(client, gene_entrez):
    query = """query Mutations($entrez: [Int!], $mutationCode: [String!], $mutationType: [String!]) {
        mutations(entrez: $entrez, mutationCode: $mutationCode, mutationType: $mutationType) {
            id
            gene {
                entrez
            }
            mutationCode
            mutationType {
                name
            }
            samples {
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'entrez': [gene_entrez]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) > 0
    for mutation in mutations[0:2]:
        samples = mutation['samples']
        assert type(mutation['id']) is int
        assert mutation['gene']['entrez'] == gene_entrez
        assert type(mutation['mutationCode']) is str
        assert type(mutation['mutationType']['name']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str


def test_mutations_query_with_passed_mutation_code(client, mutation_code):
    query = """query Mutations($entrez: [Int!], $mutationCode: [String!], $mutationType: [String!]) {
        mutations(entrez: $entrez, mutationCode: $mutationCode, mutationType: $mutationType) {
            id
            mutationCode
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'mutationCode': [mutation_code]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) > 0
    for mutation in mutations[0:2]:
        assert type(mutation['id']) is int
        assert mutation['mutationCode'] == mutation_code


def test_mutations_query_with_passed_mutation_type(client, mutation_type):
    query = """query Mutations($entrez: [Int!], $mutationCode: [String!], $mutationType: [String!]) {
        mutations(entrez: $entrez, mutationCode: $mutationCode, mutationType: $mutationType) {
            id
            mutationType {
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'mutationType': [mutation_type]}})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) > 0
    for mutation in mutations[0:2]:
        assert type(mutation['id']) is int
        assert mutation['mutationType']['name'] == mutation_type


def test_mutations_query_with_no_variables(client):
    query = """query Mutations($entrez: [Int!], $mutationCode: [String!], $mutationType: [String!]) {
        mutations(entrez: $entrez, mutationCode: $mutationCode, mutationType: $mutationType) {
            id
        }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    mutations = json_data['data']['mutations']

    assert isinstance(mutations, list)
    assert len(mutations) > 0
    for mutation in mutations[0:2]:
        assert type(mutation['id']) is int
