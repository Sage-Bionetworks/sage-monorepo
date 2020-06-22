import json
import pytest
from tests import client, NoneType


def test_slide_query(client):
    query = """query Slide($id: Int!) {
        slide(id: $id) {
            id
            name
            description
        }
    }"""
    id = 1
    response = client.post(
        '/api', json={'query': query, 'variables': {'id': id}})
    json_data = json.loads(response.data)
    slide = json_data["data"]["slide"]

    assert not isinstance(slide, list)
    assert slide["id"] == id
    assert type(slide["name"]) is str or NoneType
    assert type(slide["description"]) is str or NoneType

