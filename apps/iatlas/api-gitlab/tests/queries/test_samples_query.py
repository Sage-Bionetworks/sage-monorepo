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


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder(
        """{
            items {
                name
                patient {
                    ageAtDiagnosis
                    barcode
                    ethnicity
                    gender
                    height
                    race
                    weight
                }
            }
            paging {
                type
                pages
                total
                startCursor
                endCursor
                hasPreviousPage
                hasNextPage
                page
                limit
            }
            error
        }"""
    )


def test_samples_query_with_passed_sample(client, common_query, sample):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'name': [sample]}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        current_patient = result['patient']
        assert result['name'] == sample
        if current_patient:
            assert type(current_patient['barcode']) is str


def test_samples_query_with_passed_patient(client, common_query, patient):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'patient': [patient]}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['barcode'] == patient


def test_samples_query_with_passed_maxAgeAtDiagnosis(client, common_query, max_age_at_diagnosis):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'maxAgeAtDiagnosis': max_age_at_diagnosis}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['ageAtDiagnosis'] <= max_age_at_diagnosis


def test_samples_query_with_passed_minAgeAtDiagnosis(client, common_query, min_age_at_diagnosis):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'minAgeAtDiagnosis': min_age_at_diagnosis}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['ageAtDiagnosis'] >= min_age_at_diagnosis


def test_samples_query_with_passed_ethnicity(client, common_query, ethnicity):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'ethnicity': [ethnicity]}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['ethnicity'] == ethnicity


def test_samples_query_with_passed_gender(client, common_query, gender):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'gender': [gender]}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['gender'] == gender


def test_samples_query_with_passed_maxHeight(client, common_query, max_height):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'maxHeight': max_height}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['height'] <= max_height


def test_samples_query_with_passed_minHeight(client, common_query, min_height):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'minHeight': min_height}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['height'] >= min_height


def test_samples_query_with_passed_race(client, common_query, race):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'race': [race]}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['race'] == race


def test_samples_query_with_passed_maxWeight(client, common_query, max_weight):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'maxWeight': max_weight}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['weight'] <= max_weight


def test_samples_query_with_passed_minWeight(client, common_query, min_weight):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'minWeight': min_weight}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str
        assert result['patient']['weight'] >= min_weight


def test_samples_query_with_no_args(client, common_query):
    response = client.post('/api', json={'query': common_query})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        assert type(result['name']) is str


def test_samples_query_with_patient_and_sample(client, common_query, patient, sample):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'patient': [patient],
            'sample': [sample]}})
    json_data = json.loads(response.data)
    page = json_data['data']['samples']
    results = page['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        assert result['name'] == sample
        assert result['patient']['barcode'] == patient
