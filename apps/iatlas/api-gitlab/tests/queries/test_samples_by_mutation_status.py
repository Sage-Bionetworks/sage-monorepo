import json
import pytest
from api.enums import status_enum
from tests import NoneType


@pytest.fixture(scope='module')
def mutation_id():
    return 777


@pytest.fixture(scope='module')
def mutation_status():
    return 'Mut'


# Sample id 1904
@pytest.fixture(scope='module')
def sample_name():
    return 'TCGA-38-7271'


@pytest.fixture(scope='module')
def common_query():
    return """query SamplesByMutationStatus(
        $mutationId: [Int!]
        $mutationStatus: StatusEnum
        $sample: [String!]
    ) {
        samplesByMutationStatus(
            mutationId: $mutationId
            mutationStatus: $mutationStatus
            sample: $sample
        ) {
            status
            samples { name }
         }
    }"""


def test_samples_by_mutation_status_query_with_passed_sample(client, common_query, sample_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'sample': [sample_name]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert current_sample['name'] == sample_name


def test_samples_by_mutation_status_query_with_passed_mutation_id(client, common_query, mutation_id):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'mutationId': [mutation_id]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert type(current_sample['name']) is str


def test_samples_by_mutation_status_query_with_no_args(client, common_query):
    response = client.post('/api', json={'query': common_query})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert type(current_sample['name']) is str


def test_samples_by_mutation_status_query_with_passed_mutation_status(client, common_query, mutation_status):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'mutationStatus': mutation_status}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] == mutation_status
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert type(current_sample['name']) is str


def test_samples_by_mutation_status_query_with_all_args(client, common_query, mutation_id, mutation_status, sample_name):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'mutationId': [mutation_id],
        'mutationStatus': mutation_status,
        'sample': [sample_name]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] == mutation_status
        assert isinstance(samples, list)
        assert len(samples) == 1
        for current_sample in samples:
            assert current_sample['name'] == sample_name
