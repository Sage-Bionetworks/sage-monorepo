import json
import pytest
from tests import NoneType

def test_patient_query(client):
    query = """query Patient($barcode: [String!]) {
        patients(barcode: $barcode) {
            id
            age
            barcode
            ethnicity
            gender
            height
            race
            weight
        }
    }"""
    barcode = ["DO1328", "DO219585"]
    response = client.post(
        '/api', json={'query': query, 'variables': {'barcode': barcode}})
    json_data = json.loads(response.data)
    patients = json_data["data"]["patients"]

    assert isinstance(patients, list)
    for patient in patients[0:1]:
        assert type(patient["age"]) is int or NoneType
        assert type(patient["barcode"]) is str or NoneType
        assert type(patient["ethnicity"]) is str or NoneType
        assert type(patient["gender"]) is str or NoneType
        assert type(patient["height"]) is int or NoneType
        assert type(patient["race"]) is str or NoneType
        assert type(patient["weight"]) is int or NoneType
