import json
import pytest
from tests import NoneType


def test_data_sets_query_no_args(client):
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
    assert len(data_sets) > 0
    for data_set in data_sets:
        samples = data_set['samples']

        assert type(data_set['name']) is str
        assert type(data_set['display']) is str or NoneType
        if samples:
            assert isinstance(samples, list)
            assert len(samples) > 0
            for current_sample in samples:
                assert type(current_sample['name']) is str


def test_data_sets_query_passed_data_set(client, data_set):
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
        '/api', json={'query': query, 'variables': {'dataSet': [data_set]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['dataSets']

    assert isinstance(data_sets, list)
    assert len(data_sets) == 1
    for current_data_set in data_sets:
        samples = current_data_set['samples']

        assert current_data_set['name'] == data_set
        assert type(current_data_set['display']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) > 0
        if samples:
            for current_sample in samples:
                assert type(current_sample['name']) is str


def test_data_sets_query_passed_sample(client, sample):
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
        '/api', json={'query': query, 'variables': {'sample': [sample]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['dataSets']

    assert isinstance(data_sets, list)
    assert len(data_sets) > 0
    for data_set in data_sets:
        samples = data_set['samples']

        assert type(data_set['name']) is str
        assert type(data_set['display']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) == 1
        if samples:
            for current_sample in samples:
                assert current_sample['name'] == sample


def test_data_sets_query_passed_data_set_passed_sample(client, data_set, sample):
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
        '/api', json={'query': query, 'variables': {'dataSet': [data_set], 'sample': [sample]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['dataSets']

    assert isinstance(data_sets, list)
    assert len(data_sets) == 1
    for current_data_set in data_sets:
        samples = current_data_set['samples']

        assert current_data_set['name'] == data_set
        assert type(current_data_set['display']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) == 1
        for current_sample in samples:
            assert current_sample['name'] == sample
