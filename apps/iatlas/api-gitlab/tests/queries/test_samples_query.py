import json
import pytest


@pytest.fixture(scope='module')
def sample_name():
    return 'DO1328'


@pytest.fixture(scope='module')
def patient_barcode():
    return 'DO1328'


def test_samples_query_with_passed_sample_name(client, sample_name):
    query = """query Samples($name: [String!], $patient: [String!]) {
        samples(name: $name, patient: $patient) {
            name
            patient { barcode }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': [sample_name]}})
    json_data = json.loads(response.data)
    samples = json_data['data']['samples']

    assert isinstance(samples, list)
    assert len(samples) == 1
    for sample in samples[0:2]:
        patient = sample['patient']
        assert sample['name'] == sample_name
        if patient:
            assert type(patient['barcode']) is str


def test_samples_query_with_passed_patient_barcode(client, patient_barcode):
    query = """query Samples($name: [String!], $patient: [String!]) {
        samples(name: $name, patient: $patient) {
            name
            patient { barcode }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'patient': [patient_barcode]}})
    json_data = json.loads(response.data)
    samples = json_data['data']['samples']

    assert isinstance(samples, list)
    for sample in samples[0:2]:
        assert type(sample['name']) is str
        assert sample['patient']['barcode'] == patient_barcode


def test_samples_query_with_no_args(client):
    query = """query Samples($name: [String!], $patient: [String!]) {
        samples(name: $name, patient: $patient) {
            name
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    samples = json_data['data']['samples']

    assert isinstance(samples, list)
    for sample in samples[0:2]:
        assert type(sample['name']) is str


def test_samples_query_with_all_args(client, patient_barcode, sample_name):
    query = """query Samples($name: [String!], $patient: [String!]) {
        samples(name: $name, patient: $patient) {
            name
            patient { barcode }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'patient': [patient_barcode],
            'sample': [sample_name]}})
    json_data = json.loads(response.data)
    samples = json_data['data']['samples']

    assert isinstance(samples, list)
    for sample in samples[0:2]:
        assert sample['name'] == sample_name
        assert sample['patient']['barcode'] == patient_barcode
