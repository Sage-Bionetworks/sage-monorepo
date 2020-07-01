import json
import pytest
from tests import NoneType


@pytest.fixture(scope='module')
def sample_name():
    return 'DO1328'


def test_data_sets_query_no_passed_data_set_no_passed_sample(client):
    query = """query DataSets($dataSet: [String!], $sample: [String!]) {
        dataSets(dataSet: $dataSet, sample: $sample) {
            display
            name
            samples {
                name
            }
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['dataSets']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        samples = data_set['samples']

        assert type(data_set['name']) is str
        assert type(data_set['display']) is str or NoneType
        assert isinstance(samples, list)
        if samples:
            for sample in samples:
                assert type(sample['name']) is str


def test_data_sets_query_passed_data_set(client, dataset):
    query = """query DataSets($dataSet: [String!], $sample: [String!]) {
        dataSets(dataSet: $dataSet, sample: $sample) {
            display
            name
            samples {
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [dataset]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['dataSets']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        samples = data_set['samples']

        assert data_set['name'] == dataset
        assert type(data_set['display']) is str or NoneType
        assert isinstance(samples, list)
        if samples:
            for sample in samples:
                assert type(sample['name']) is str


def test_data_sets_query_passed_sample(client, sample_name):
    query = """query DataSets($dataSet: [String!], $sample: [String!]) {
        dataSets(dataSet: $dataSet, sample: $sample) {
            display
            name
            samples {
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'sample': [sample_name]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['dataSets']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        samples = data_set['samples']

        assert type(data_set['name']) is str
        assert type(data_set['display']) is str or NoneType
        assert isinstance(samples, list)
        if samples:
            for sample in samples:
                assert sample['name'] == sample_name


def test_data_sets_query_passed_data_set_passed_sample(client, dataset, sample_name):
    query = """query DataSets($dataSet: [String!], $sample: [String!]) {
        dataSets(dataSet: $dataSet, sample: $sample) {
            display
            name
            samples {
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [dataset], 'sample': [sample_name]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['dataSets']

    assert isinstance(data_sets, list)
    for data_set in data_sets:
        samples = data_set['samples']

        assert data_set['name'] == dataset
        assert type(data_set['display']) is str or NoneType
        assert isinstance(samples, list)
        if samples:
            for sample in samples:
                assert sample['name'] == sample_name
