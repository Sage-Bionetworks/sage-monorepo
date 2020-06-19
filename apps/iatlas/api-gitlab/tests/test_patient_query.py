import json
import pytest
from tests import client, NoneType


def test_patient_query(client):
    query = """query Patient($id: Int!) {
        patient(id: $id) {
            age
            barcode
            ethnicity
            gender
            height
            race
            weight
        }
    }"""
    id = 1
    response = client.post(
        '/api', json={'query': query, 'variables': {'id': id}})
    json_data = json.loads(response.data)
    patient = json_data["data"]["patient"]

    assert not isinstance(patient, list)
    assert patient["id"] == id
    assert type(patient["age"]) is int or NoneType
    assert type(patient["barcode"]) is str or NoneType
    assert type(patient["ethnicity"]) is str or NoneType
    assert type(patient["gender"]) is str or NoneType
    assert type(patient["height"]) is int or NoneType
    assert type(patient["race"]) is str or NoneType
    assert type(patient["weight"]) is int or NoneType
