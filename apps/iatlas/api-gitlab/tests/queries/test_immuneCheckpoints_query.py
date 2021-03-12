import json
import pytest
from api.database import return_immune_checkpoint_query
from tests import NoneType


@pytest.fixture(scope='module')
def immuneCheckpoint():
    return 'Inhibitory'


def test_immune_checkpoints_query_with_passed_immune_checkpoint_name(client, immuneCheckpoint):
    query = """query ImmuneCheckpoints($name: [String!]) {
        immuneCheckpoints(name: $name) {
            genes { entrez }
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': immuneCheckpoint}})
    json_data = json.loads(response.data)
    results = json_data['data']['immuneCheckpoints']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        genes = result['genes']
        assert result['name'] == immuneCheckpoint
        assert isinstance(genes, list)
        assert len(genes) > 0
        for gene in genes[0:2]:
            assert type(gene['entrez']) is int


def test_immune_checkpoints_query_with_passed_immune_checkpoint_no_genes(client, immuneCheckpoint):
    query = """query immuneCheckpoints($name: [String!]) {
        immuneCheckpoints(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': immuneCheckpoint}})
    json_data = json.loads(response.data)
    results = json_data['data']['immuneCheckpoints']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        assert result['name'] == immuneCheckpoint


def test_immune_checkpoints_query_no_args(client):
    query = """query ImmuneCheckpoints($name: [String!]) {
        immuneCheckpoints(name: $name) { name }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['immuneCheckpoints']

    immune_checkpoint_count = return_immune_checkpoint_query('id').count()

    assert isinstance(results, list)
    assert len(results) == immune_checkpoint_count
    for result in results[0:1]:
        assert type(result['name']) is str
