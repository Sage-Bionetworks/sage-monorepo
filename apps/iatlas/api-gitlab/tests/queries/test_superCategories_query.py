import json
import pytest
from api.database import return_super_category_query
from tests import NoneType


@pytest.fixture(scope='module')
def super_category():
    return 'Cell adhesion'


def test_super_categories_query_with_passed_super_category_name(client, super_category):
    query = """query superCategories($name: [String!]) {
        superCategories(name: $name) {
            genes { entrez }
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': super_category}})
    json_data = json.loads(response.data)
    results = json_data['data']['superCategories']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        genes = result['genes']
        assert result['name'] == super_category
        assert isinstance(genes, list)
        assert len(genes) > 0
        for gene in genes[0:2]:
            assert type(gene['entrez']) is int


def test_super_categories_query_with_passed_super_category_no_genes(client, super_category):
    query = """query superCategories($name: [String!]) {
        superCategories(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': super_category}})
    json_data = json.loads(response.data)
    results = json_data['data']['superCategories']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        assert result['name'] == super_category


def test_super_categories_query_no_args(client):
    query = """query superCategories($name: [String!]) {
        superCategories(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['superCategories']

    super_category_count = return_super_category_query('id').count()

    assert isinstance(results, list)
    assert len(results) == super_category_count
    for result in results[0:1]:
        assert type(result['name']) is str
