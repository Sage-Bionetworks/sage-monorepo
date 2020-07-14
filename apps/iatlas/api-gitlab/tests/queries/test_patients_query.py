import json
import pytest
from tests import NoneType


def test_patients_query(client, patient):
    query = """query Patients($barcode: [String!]) {
        patients(barcode: $barcode) {
            age
            barcode
            ethnicity
            gender
            height
            race
            weight
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'barcode': patient}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert type(result['age']) is int or NoneType
        assert result['barcode'] == patient
        assert type(result['ethnicity']) is str or NoneType
        assert type(result['gender']) is str or NoneType
        assert type(result['height']) is int or NoneType
        assert type(result['race']) is str or NoneType
        assert type(result['weight']) is int or NoneType
