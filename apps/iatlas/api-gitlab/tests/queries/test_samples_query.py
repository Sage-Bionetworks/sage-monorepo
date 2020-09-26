import json
import pytest


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Samples(
            $ethnicity: [EthnicityEnum!]
            $gender: [GenderEnum!]
            $maxAgeAtDiagnosis: Int
            $maxHeight: Float
            $maxWeight: Float
            $minAgeAtDiagnosis: Int
            $minHeight: Float
            $minWeight: Float
            $name: [String!]
            $patient: [String!]
            $race: [RaceEnum!]
        ) {
            samples(
                ethnicity: $ethnicity
                gender: $gender
                maxAgeAtDiagnosis: $maxAgeAtDiagnosis
                maxHeight: $maxHeight
                maxWeight: $maxWeight
                minAgeAtDiagnosis: $minAgeAtDiagnosis
                minHeight: $minHeight
                minWeight: $minWeight
                name: $name
                patient: $patient
                race: $race
            )""" + query_fields + "}"
    return f


def test_samples_query_with_passed_sample(client, common_query_builder, sample):
    query = common_query_builder("""{
        name
        patient { barcode }
    }""")
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


def test_samples_query_with_passed_patient(client, common_query_builder, patient):
    query = common_query_builder("""{
        name
        patient { barcode }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'patient': [patient]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['barcode'] == patient


def test_samples_query_with_passed_maxAgeAtDiagnosis(client, common_query_builder, max_age_at_diagnosis):
    query = common_query_builder("""{
        name
        patient { ageAtDiagnosis }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'maxAgeAtDiagnosis': max_age_at_diagnosis}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['ageAtDiagnosis'] <= max_age_at_diagnosis


def test_samples_query_with_passed_minAgeAtDiagnosis(client, common_query_builder, min_age_at_diagnosis):
    query = common_query_builder("""{
        name
        patient { ageAtDiagnosis }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'minAgeAtDiagnosis': min_age_at_diagnosis}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['ageAtDiagnosis'] >= min_age_at_diagnosis


def test_samples_query_with_passed_ethnicity(client, common_query_builder, ethnicity):
    query = common_query_builder("""{
        name
        patient { ethnicity }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'ethnicity': [ethnicity]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['ethnicity'] == ethnicity


def test_samples_query_with_passed_gender(client, common_query_builder, gender):
    query = common_query_builder("""{
        name
        patient { gender }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'gender': [gender]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['gender'] == gender


def test_samples_query_with_passed_maxHeight(client, common_query_builder, max_height):
    query = common_query_builder("""{
        name
        patient { height }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'maxHeight': max_height}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['height'] <= max_height


def test_samples_query_with_passed_minHeight(client, common_query_builder, min_height):
    query = common_query_builder("""{
        name
        patient { height }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'minHeight': min_height}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['height'] >= min_height


def test_samples_query_with_passed_race(client, common_query_builder, race):
    query = common_query_builder("""{
        name
        patient { race }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'race': [race]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['race'] == race


def test_samples_query_with_passed_maxWeight(client, common_query_builder, max_weight):
    query = common_query_builder("""{
        name
        patient { weight }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'maxWeight': max_weight}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['weight'] <= max_weight


def test_samples_query_with_passed_minWeight(client, common_query_builder, min_weight):
    query = common_query_builder("""{
        name
        patient { weight }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'minWeight': min_weight}})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['weight'] >= min_weight


def test_samples_query_with_no_args(client, common_query_builder):
    query = common_query_builder("""{ name }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['samples']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str


def test_samples_query_with_patient_and_sample(client, common_query_builder, patient, sample):
    query = common_query_builder("""{
        name
        patient { barcode }
    }""")
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
