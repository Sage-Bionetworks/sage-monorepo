import json
import pytest
from api.database import return_slide_query
from tests import NoneType


def test_slides_query_with_passed_slide(client, slide):
    query = """query Slides($name: [String!]) {
        slides(name: $name) {
            name
            description
            patient {
                barcode
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': slide}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert result['name'] == slide
        assert type(result['description']) is str or NoneType
        assert type(result['patient']['barcode']) is str


def test_slides_query_no_args(client):
    query = """query Slides($name: [String!]) {
        slides(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    slide_count = return_slide_query('id').count()

    assert isinstance(results, list)
    assert len(results) == slide_count
    for result in results[0:1]:
        assert type(result['name']) is str
