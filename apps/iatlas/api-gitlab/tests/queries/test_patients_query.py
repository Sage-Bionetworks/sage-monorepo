import json
import pytest
from tests import NoneType
from api.enums import ethnicity_enum, gender_enum, race_enum


@pytest.fixture(scope='module')
def barcode():
    return 'TCGA-WN-AB4C'


def test_patients_query(client, barcode):
    query = """query Patients($barcode: [String!]) {
        patients(barcode: $barcode) {
            age
            barcode
            ethnicity
            gender
            height
            race
            weight
            slides { name }
            samples { name }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'barcode': [barcode]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        slides = result['slides']
        samples = result['samples']

        assert type(result['age']) is int or NoneType
        assert result['barcode'] == barcode
        assert type(result['ethnicity']) in ethnicity_enum.enums or NoneType
        assert type(result['gender']) in gender_enum.enums or NoneType
        assert type(result['height']) is int or NoneType
        assert type(result['race']) in race_enum.enums or NoneType
        assert type(result['weight']) is int or NoneType
        for slide in slides:
            assert type(slide['name']) is str
        for sample in samples:
            assert type(sample['name']) is str
