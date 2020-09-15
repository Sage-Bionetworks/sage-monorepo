import json
import pytest
from api.database import return_slide_query
from tests import NoneType


def test_slides_query_with_passed_slide(client, slide):
    query = """query Slides(
        $ageAtDiagnosis: [Int!]
        $barcode: [String!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
        $sample: [String!]
    ) {
        slides(
            ageAtDiagnosis: $ageAtDiagnosis
            barcode: $barcode
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            race: $race
            weight: $weight
            sample: $sample
        ) {
            name
            description
            patient { barcode }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': slide}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert result['name'] == slide
        assert type(result['description']) is str or NoneType
        assert type(result['patient']['barcode']) is str


def test_slides_query_with_passed_ageAtDiagnosis(client, age_at_diagnosis):
    query = """query Slides(
        $ageAtDiagnosis: [Int!]
        $barcode: [String!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
        $sample: [String!]
    ) {
        slides(
            ageAtDiagnosis: $ageAtDiagnosis
            barcode: $barcode
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            race: $race
            weight: $weight
            sample: $sample
        ) {
            name
            patient { ageAtDiagnosis }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'ageAtDiagnosis': [age_at_diagnosis]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['ageAtDiagnosis'] == age_at_diagnosis


def test_slides_query_with_passed_barcode(client, patient):
    query = """query Slides(
        $ageAtDiagnosis: [Int!]
        $barcode: [String!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
        $sample: [String!]
    ) {
        slides(
            ageAtDiagnosis: $ageAtDiagnosis
            barcode: $barcode
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            race: $race
            weight: $weight
            sample: $sample
        ) {
            name
            patient { barcode }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'barcode': [patient]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['barcode'] == patient


def test_slides_query_with_passed_ethnicity(client, ethnicity):
    query = """query Slides(
        $ageAtDiagnosis: [Int!]
        $barcode: [String!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
        $sample: [String!]
    ) {
        slides(
            ageAtDiagnosis: $ageAtDiagnosis
            barcode: $barcode
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            race: $race
            weight: $weight
            sample: $sample
        ) {
            name
            patient { ethnicity }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'ethnicity': [ethnicity]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['ethnicity'] == ethnicity


def test_slides_query_with_passed_gender(client, gender):
    query = """query Slides(
        $ageAtDiagnosis: [Int!]
        $barcode: [String!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
        $sample: [String!]
    ) {
        slides(
            ageAtDiagnosis: $ageAtDiagnosis
            barcode: $barcode
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            race: $race
            weight: $weight
            sample: $sample
        ) {
            name
            patient { gender }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'gender': [gender]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['gender'] == gender


def test_slides_query_with_passed_height(client, height):
    query = """query Slides(
        $ageAtDiagnosis: [Int!]
        $barcode: [String!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
        $sample: [String!]
    ) {
        slides(
            ageAtDiagnosis: $ageAtDiagnosis
            barcode: $barcode
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            race: $race
            weight: $weight
            sample: $sample
        ) {
            name
            patient { height }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'height': [height]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['height'] == height


def test_slides_query_with_passed_race(client, race):
    query = """query Slides(
        $ageAtDiagnosis: [Int!]
        $barcode: [String!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
        $sample: [String!]
    ) {
        slides(
            ageAtDiagnosis: $ageAtDiagnosis
            barcode: $barcode
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            race: $race
            weight: $weight
            sample: $sample
        ) {
            name
            patient { race }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'race': [race]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['race'] == race


def test_slides_query_with_passed_weight(client, weight):
    query = """query Slides(
        $ageAtDiagnosis: [Int!]
        $barcode: [String!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
        $sample: [String!]
    ) {
        slides(
            ageAtDiagnosis: $ageAtDiagnosis
            barcode: $barcode
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            race: $race
            weight: $weight
            sample: $sample
        ) {
            name
            patient { weight }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'weight': [weight]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['weight'] == weight


def test_slides_query_with_passed_sample(client, sample):
    query = """query Slides(
        $ageAtDiagnosis: [Int!]
        $barcode: [String!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
        $sample: [String!]
    ) {
        slides(
            ageAtDiagnosis: $ageAtDiagnosis
            barcode: $barcode
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            race: $race
            weight: $weight
            sample: $sample
        ) { name }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'sample': [sample]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert type(result['name']) is str


def test_slides_query_no_args(client):
    query = """query Slides(
        $ageAtDiagnosis: [Int!]
        $barcode: [String!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
        $sample: [String!]
    ) {
        slides(
            ageAtDiagnosis: $ageAtDiagnosis
            barcode: $barcode
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            race: $race
            weight: $weight
            sample: $sample
        ) { name }
    }"""
    response = client.post(
        '/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']

    slide_count = return_slide_query('id').count()

    assert isinstance(results, list)
    assert len(results) == slide_count
    for result in results[0:1]:
        assert type(result['name']) is str
