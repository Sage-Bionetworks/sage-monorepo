import json
import pytest
from api.database import return_method_tag_query
from tests import NoneType


@pytest.fixture(scope='module')
def method_tag():
    return 'CIBERSORT'


def test_method_tags_query_with_passed_method_tag_name(client, method_tag):
    query = """query methodTags($name: [String!]) {
        methodTags(name: $name) {
            features { name }
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': method_tag}})
    json_data = json.loads(response.data)
    results = json_data['data']['methodTags']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        features = result['features']
        assert result['name'] == method_tag
        assert isinstance(features, list)
        assert len(features) > 0
        for feature in features[0:2]:
            assert type(feature['name']) is str


def test_method_tags_query_with_passed_method_tag_no_features(client, method_tag):
    query = """query methodTags($name: [String!]) {
        methodTags(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': method_tag}})
    json_data = json.loads(response.data)
    results = json_data['data']['methodTags']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        assert result['name'] == method_tag


def test_method_tags_query_no_args(client):
    query = """query methodTags($name: [String!]) {
        methodTags(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['methodTags']

    method_tag_count = return_method_tag_query('id').count()

    assert isinstance(results, list)
    assert len(results) == method_tag_count
    for result in results[0:1]:
        assert type(result['name']) is str
