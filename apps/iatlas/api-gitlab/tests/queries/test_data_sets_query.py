import json
import pytest
from os import getenv
from tests import NoneType


@pytest.fixture(scope='module')
def data_set_type():
    return 'ici'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query DataSets($dataSet: [String!], $sample: [String!], $dataSetType: [String!]) {
        dataSets(dataSet: $dataSet, sample: $sample, dataSetType: $dataSetType)""" + query_fields + "}"
    return f


def test_data_sets_query_no_args(client, common_query_builder):
    query = common_query_builder("""{
            display
            name
        }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['dataSets']

    assert isinstance(data_sets, list)
    assert len(data_sets) > 0
    for data_set in data_sets:
        assert type(data_set['name']) is str
        assert type(data_set['display']) is str or NoneType


def test_data_sets_query_with_dataSet(client, common_query_builder, data_set):
    query = common_query_builder("""{
            display
            name
            type
            samples { name }
        }""")
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
        assert type(current_data_set['type']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:5]:
            assert type(current_sample['name']) is str


def test_data_sets_query_with_sample(client, common_query_builder, sample):
    query = common_query_builder("""{
            display
            name
            samples { name }
        }""")
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


def test_data_sets_query_with_dataSet_and_sample(client, common_query_builder, data_set, sample):
    query = common_query_builder("""{
            display
            name
            samples { name }
        }""")
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


def test_data_sets_query_with_dataSetType(client, common_query_builder, data_set_type):
    query = common_query_builder("""{
            display
            name
            type
        }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSetType': [data_set_type]}})
    json_data = json.loads(response.data)
    data_sets = json_data['data']['dataSets']

    assert isinstance(data_sets, list)
    assert len(data_sets) > 0
    for data_set in data_sets:
        assert type(data_set['name']) is str
        assert type(data_set['display']) is str or NoneType
        assert data_set['type'] == data_set_type
