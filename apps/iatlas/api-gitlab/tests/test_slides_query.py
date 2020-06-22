import json
import pytest
from tests import client, NoneType


def test_slide_query(client):
    query = """query Slides($id: [Int!]) {
        slides(id: $id) {
            id
            name
            description
        }
    }"""
    id = [1,2]
    response = client.post(
        '/api', json={'query': query, 'variables': {'id': id}})
    json_data = json.loads(response.data)
    slides = json_data["data"]["slides"]

    assert isinstance(slides, list)
    for slide in slides[0:1]:
        assert type(slide["name"]) is str or NoneType
        assert type(slide["description"]) is str or NoneType

