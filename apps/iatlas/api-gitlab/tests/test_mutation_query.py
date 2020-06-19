import json
import pytest
from tests import client, NoneType


def test_mutation_query_with_relations(client):
    query = """query Mutation($id: Int!) {
        mutation(id: $id) {
            id
            gene
            mutationCode
            mutationType
            samples{
                name
            }
        }
    }"""
    id = 1
    response = client.post(
        '/api', json={'query': query, 'variables': {'id': id}})
    json_data = json.loads(response.data)
    mutation = json_data["data"]["mutation"]

    assert not isinstance(mutation, list)
    assert mutation["id"] == id
    assert type(mutation["gene"]) is str or NoneType
    assert type(mutation["mutationCode"]) is str or NoneType
    assert type(mutation["mutationType"]) is str or NoneType
    assert isinstance(mutation["samples"], list) or NoneType


def test_mutation_query_no_relations(client):
    query = """query Mutation($id: Int!) {
        mutation(id: $id) {
            id
            mutationCode
            mutationType
            samples{
                name
            }
        }
    }"""
    id = 1
    response = client.post(
        '/api', json={'query': query, 'variables': {'id': id}})
    json_data = json.loads(response.data)
    mutation = json_data["data"]["mutation"]

    assert not isinstance(mutation, list)
    assert mutation["id"] == id
    assert type(mutation["mutationCode"]) is str or NoneType
    assert type(mutation["mutationType"]) is str or NoneType
