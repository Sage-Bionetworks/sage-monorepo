import json
import pytest


def test_samples_query_with_passed_sample(client, sample):
    query = """query Samples($name: [String!], $patient: [String!]) {
        samples(name: $name, patient: $patient) {
            name
            patient { barcode }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': [sample]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        current_patient = result['patient']
        assert result['name'] == sample
        if current_patient:
            assert type(current_patient['barcode']) is str


def test_samples_query_with_passed_patient(client, patient):
    query = """query Samples($name: [String!], $patient: [String!]) {
        samples(name: $name, patient: $patient) {
            name
            patient { barcode }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'patient': [patient]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['barcode'] == patient


def test_samples_query_with_no_args(client):
    query = """query Samples($name: [String!], $patient: [String!]) {
        samples(name: $name, patient: $patient) {
            name
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    for result in results[0:2]:
        assert type(result['name']) is str


def test_samples_query_with_all_args(client, patient, sample):
    query = """query Samples($name: [String!], $patient: [String!]) {
        samples(name: $name, patient: $patient) {
            name
            patient { barcode }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'patient': [patient],
            'sample': [sample]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    for result in results[0:2]:
        assert result['name'] == sample
        assert result['patient']['barcode'] == patient
