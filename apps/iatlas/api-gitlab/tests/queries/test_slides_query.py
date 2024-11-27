import json
import pytest
from api.database import return_slide_query
from tests import NoneType


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Slides(
            $barcode: [String!]
            $ethnicity: [EthnicityEnum!]
            $gender: [GenderEnum!]
            $maxAgeAtDiagnosis: Int
            $maxHeight: Float
            $maxWeight: Float
            $minAgeAtDiagnosis: Int
            $minHeight: Float
            $minWeight: Float
            $name: [String!]
            $race: [RaceEnum!]
            $sample: [String!]
        ) {
            slides(
                barcode: $barcode
                ethnicity: $ethnicity
                gender: $gender
                maxAgeAtDiagnosis: $maxAgeAtDiagnosis
                maxHeight: $maxHeight
                maxWeight: $maxWeight
                minAgeAtDiagnosis: $minAgeAtDiagnosis
                minHeight: $minHeight
                minWeight: $minWeight
                name: $name
                race: $race
                sample: $sample
            )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder(
        """{
            items {
                id
                name
                description
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


def test_slides_query_with_passed_slide(client, common_query, slide):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'name': slide}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert result['name'] == slide
        assert type(result['description']) is str or NoneType
        assert type(result['patient']['barcode']) is str


def test_slides_query_with_passed_maxAgeAtDiagnosis(client, common_query, max_age_at_diagnosis):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'maxAgeAtDiagnosis': max_age_at_diagnosis}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['ageAtDiagnosis'] <= max_age_at_diagnosis


def test_slides_query_with_passed_minAgeAtDiagnosis(client, common_query, min_age_at_diagnosis):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'minAgeAtDiagnosis': min_age_at_diagnosis}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['ageAtDiagnosis'] >= min_age_at_diagnosis


def test_slides_query_with_passed_barcode(client, common_query, patient):

    response = client.post(
        '/api', json={'query': common_query, 'variables': {'barcode': [patient]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['barcode'] == patient


def test_slides_query_with_passed_ethnicity(client, common_query, ethnicity):

    response = client.post(
        '/api', json={'query': common_query, 'variables': {'ethnicity': [ethnicity]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['ethnicity'] == ethnicity


def test_slides_query_with_passed_gender(client, common_query, gender):

    response = client.post(
        '/api', json={'query': common_query, 'variables': {'gender': [gender]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['gender'] == gender


def test_slides_query_with_passed_maxHeight(client, common_query, max_height):

    response = client.post(
        '/api', json={'query': common_query, 'variables': {'maxHeight': max_height}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['height'] <= max_height


def test_slides_query_with_passed_minHeight(client, common_query, min_height):

    response = client.post(
        '/api', json={'query': common_query, 'variables': {'minHeight': min_height}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['height'] >= min_height


def test_slides_query_with_passed_race(client, common_query, race):

    response = client.post(
        '/api', json={'query': common_query, 'variables': {'race': [race]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['race'] == race


def test_slides_query_with_passed_maxWeight(client, common_query, max_weight):

    response = client.post(
        '/api', json={'query': common_query, 'variables': {'maxWeight': max_weight}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['weight'] <= max_weight


def test_slides_query_with_passed_minWeight(client, common_query, min_weight):

    response = client.post(
        '/api', json={'query': common_query, 'variables': {'minWeight': min_weight}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result['name']) is str
        assert result['patient']['weight'] >= min_weight


def test_slides_query_with_passed_sample(client, common_query, sample):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'sample': [sample]}})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        assert type(result['name']) is str


def test_slides_query_no_args(client, common_query):
    response = client.post(
        '/api', json={'query': common_query})
    json_data = json.loads(response.data)
    results = json_data['data']['slides']['items']

    slide_count = return_slide_query('id').count()

    assert isinstance(results, list)
    assert len(results) == slide_count
    for result in results[0:1]:
        assert type(result['name']) is str
