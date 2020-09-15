import json
import pytest


def test_samples_query_with_passed_sample(client, sample):
    query = """query Samples(
        $ageAtDiagnosis: [Int!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
    ) {
        samples(
            ageAtDiagnosis: $ageAtDiagnosis
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            weight: $weight
        ) {
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
    query = """query Samples(
        $ageAtDiagnosis: [Int!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
    ) {
        samples(
            ageAtDiagnosis: $ageAtDiagnosis
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            weight: $weight
        ) {
            name
            patient { barcode }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'patient': [patient]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['barcode'] == patient


def test_samples_query_with_passed_age_at_diagnosis(client, age_at_diagnosis):
    query = """query Samples(
        $ageAtDiagnosis: [Int!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
    ) {
        samples(
            ageAtDiagnosis: $ageAtDiagnosis
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            weight: $weight
        ) {
            name
            patient { ageAtDiagnosis }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'ageAtDiagnosis': [age_at_diagnosis]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['ageAtDiagnosis'] == age_at_diagnosis


def test_samples_query_with_passed_ethnicity(client, ethnicity):
    query = """query Samples(
        $ageAtDiagnosis: [Int!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
    ) {
        samples(
            ageAtDiagnosis: $ageAtDiagnosis
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            weight: $weight
        ) {
            name
            patient { ethnicity }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'ethnicity': [ethnicity]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['ethnicity'] == ethnicity


def test_samples_query_with_passed_gender(client, gender):
    query = """query Samples(
        $ageAtDiagnosis: [Int!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
    ) {
        samples(
            ageAtDiagnosis: $ageAtDiagnosis
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            weight: $weight
        ) {
            name
            patient { gender }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'gender': [gender]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['gender'] == gender


def test_samples_query_with_passed_height(client, height):
    query = """query Samples(
        $ageAtDiagnosis: [Int!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
    ) {
        samples(
            ageAtDiagnosis: $ageAtDiagnosis
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            weight: $weight
        ) {
            name
            patient { height }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'height': [height]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['height'] == height


def test_samples_query_with_passed_race(client, race):
    query = """query Samples(
        $ageAtDiagnosis: [Int!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
    ) {
        samples(
            ageAtDiagnosis: $ageAtDiagnosis
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            weight: $weight
        ) {
            name
            patient { race }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'race': [race]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['race'] == race


def test_samples_query_with_passed_weight(client, weight):
    query = """query Samples(
        $ageAtDiagnosis: [Int!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
    ) {
        samples(
            ageAtDiagnosis: $ageAtDiagnosis
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            weight: $weight
        ) {
            name
            patient { weight }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'weight': [weight]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['weight'] == weight


def test_samples_query_with_no_args(client):
    query = """query Samples(
        $ageAtDiagnosis: [Int!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
    ) {
        samples(
            ageAtDiagnosis: $ageAtDiagnosis
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            weight: $weight
        ) {
            name
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str


def test_samples_query_with_patient_and_sample(client, patient, sample):
    query = """query Samples(
        $ageAtDiagnosis: [Int!]
        $ethnicity: [EthnicityEnum!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $weight: [Int!]
    ) {
        samples(
            ageAtDiagnosis: $ageAtDiagnosis
            ethnicity: $ethnicity
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            weight: $weight
        ) {
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
    assert len(results) == 1
    for result in results[0:2]:
        assert result['name'] == sample
        assert result['patient']['barcode'] == patient
