import json
import pytest
from tests import client, NoneType


def test_sample_query_with_relations(client):
    query = """query Sample($id: Int!) {
        sample(id: $id) {
            name
            patient{
                barcode
                age
                race
            }
            features{
                name
                value
            }
            tags{
                characteristics
                color
                display
                name
            }
        }
    }"""
    id = 1
    response = client.post(
        '/api', json={'query': query, 'variables': {'id': id}})
    json_data = json.loads(response.data)
    sample = json_data["data"]["sample"]

    assert not isinstance(sample, list)
    assert type(sample["name"]) is str or NoneType
    assert type(sample["patient"]) is object or NoneType
    assert isinstance(sample["features"], list) or NoneType
    assert isinstance(sample["tags"], list) or NoneType


def test_sample_query_no_relations(client):
    query = """query Sample($id: Int!) {
        sample(id: $id) {
            name
        }
    }"""
    id = 1
    response = client.post(
        '/api', json={'query': query, 'variables': {'id': id}})
    json_data = json.loads(response.data)
    sample = json_data["data"]["sample"]

    assert not isinstance(sample, list)
    assert type(sample["name"]) is str or NoneType
