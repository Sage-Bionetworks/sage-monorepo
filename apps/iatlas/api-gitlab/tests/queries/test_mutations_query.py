import json
import pytest
from tests import NoneType

def test_mutations_query_with_relations(client):
    query = """query Mutations($id: [Int!]) {
        mutations(id: $id) {
            id
            gene
            mutationCode
            mutationType
            samples{
                name
            }
        }
    }"""
    id = [1,2]
    response = client.post(
        '/api', json={'query': query, 'variables': {'id': id}})
    json_data = json.loads(response.data)
    mutations = json_data["data"]["mutations"]

    assert isinstance(mutations, list)
    for mutation in mutations[0:1]:
        assert type(mutation["gene"]) is str or NoneType
        assert type(mutation["mutationCode"]) is str or NoneType
        assert type(mutation["mutationType"]) is str or NoneType
        assert isinstance(mutation["samples"], list) or NoneType


def test_mutations_query_no_relations(client):
    query = """query Mutations($id: [Int!]) {
        mutations(id: $id) {
            id
            mutationCode
            mutationType
            samples{
                name
            }
        }
    }"""
    id = [1,2]
    response = client.post(
        '/api', json={'query': query, 'variables': {'id': id}})
    json_data = json.loads(response.data)
    mutations = json_data["data"]["mutations"]

    assert isinstance(mutations, list)
    for mutation in mutations[0:1]:
        assert type(mutation["mutationCode"]) is str or NoneType
        assert type(mutation["mutationType"]) is str or NoneType
