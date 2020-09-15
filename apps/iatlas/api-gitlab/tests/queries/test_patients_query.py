import json
import pytest
from tests import NoneType
from api.enums import ethnicity_enum, gender_enum, race_enum


@pytest.fixture(scope='module')
def barcode():
    return 'TCGA-WN-AB4C'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query Patients(
            $ageAtDiagnosis: [Int!]
            $barcode: [String!]
            $ethnicity: [EthnicityEnum!]
            $gender: [GenderEnum!]
            $height: [Int!]
            $race: [RaceEnum!]
            $sample: [String!]
            $slide: [String!]
            $weight: [Int!]
        ) {
            patients(
                ageAtDiagnosis: $ageAtDiagnosis
                barcode: $barcode
                ethnicity: $ethnicity
                gender: $gender
                height: $height
                race: $race
                sample: $sample
                slide: $slide
                weight: $weight
            )""" + query_fields + "}"
    return f


def test_patients_query(client, common_query_builder, barcode):
    query = common_query_builder("""{
                                    ageAtDiagnosis
                                    barcode
                                    ethnicity
                                    gender
                                    height
                                    race
                                    weight
                                    slides { name }
                                    samples
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'barcode': [barcode]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        slides = result['slides']
        samples = result['samples']

        assert type(result['ageAtDiagnosis']) is int or NoneType
        assert result['barcode'] == barcode
        assert type(result['ethnicity']) in ethnicity_enum.enums or NoneType
        assert type(result['gender']) in gender_enum.enums or NoneType
        assert type(result['height']) is int or NoneType
        assert type(result['race']) in race_enum.enums or NoneType
        assert type(result['weight']) is int or NoneType
        assert isinstance(slides, list)
        for slide in slides:
            assert type(slide['name']) is str
        assert isinstance(samples, list)
        for sample in samples:
            assert type(sample) is str


def test_patients_query_with_passed_ageAtDiagnosis(client, common_query_builder, age_at_diagnosis):
    query = common_query_builder("""{ ageAtDiagnosis }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'ageAtDiagnosis': [age_at_diagnosis]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['ageAtDiagnosis'] == age_at_diagnosis


def test_patients_query_with_passed_ethnicity(client, common_query_builder, ethnicity):
    query = common_query_builder("""{ ethnicity }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'ethnicity': [ethnicity]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['ethnicity'] == ethnicity


def test_patients_query_with_passed_gender(client, common_query_builder, gender):
    query = common_query_builder("""{ gender }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'gender': [gender]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['gender'] == gender


def test_patients_query_with_passed_height(client, common_query_builder, height):
    query = common_query_builder("""{ height }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'height': [height]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['height'] == height


def test_patients_query_with_passed_race(client, common_query_builder, race):
    query = common_query_builder("""{ race }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'race': [race]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['race'] == race


def test_patients_query_with_passed_weight(client, common_query_builder, weight):
    query = common_query_builder("""{ weight }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'weight': [weight]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['weight'] == weight


def test_patients_query_with_passed_sample(client, common_query_builder, sample):
    query = common_query_builder("""{ samples }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'sample': [sample]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        samples = result['samples']
        assert isinstance(samples, list)
        assert len(samples) == 1
        for current_sample in samples:
            assert current_sample == sample


def test_patients_query_with_passed_slide(client, common_query_builder, slide):
    query = common_query_builder("""{
                                    slides {
                                        name
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'slide': [slide]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        slides = result['slides']

        assert isinstance(slides, list)
        for current_slide in slides:
            assert current_slide['name'] == slide
