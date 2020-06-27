import json
import pytest
from tests import NoneType

def test_patient_query(client):
    query = """query Patient($barcode: String!) {
        patient(barcode: $barcode) {
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
    barcode = "DO1328"
    response = client.post(
        '/api', json={'query': query, 'variables': {'barcode': barcode}})
    json_data = json.loads(response.data)
    patient = json_data["data"]["patient"]

    assert not isinstance(patient, list)
    assert type(patient["age"]) is int or NoneType
    assert type(patient["barcode"]) is str or NoneType
    assert type(patient["ethnicity"]) is str or NoneType
    assert type(patient["gender"]) is str or NoneType
    assert type(patient["height"]) is int or NoneType
    assert type(patient["race"]) is str or NoneType
    assert type(patient["weight"]) is int or NoneType
