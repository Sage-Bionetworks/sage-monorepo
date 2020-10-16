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
            $barcode: [String!]
            $dataSet: [String!]
            $ethnicity: [EthnicityEnum!]
            $gender: [GenderEnum!]
            $maxAgeAtDiagnosis: Int
            $maxHeight: Float
            $maxWeight: Float
            $minAgeAtDiagnosis: Int
            $minHeight: Float
            $minWeight: Float
            $race: [RaceEnum!]
            $sample: [String!]
            $slide: [String!]
        ) {
            patients(
                barcode: $barcode
                dataSet: $dataSet
                ethnicity: $ethnicity
                gender: $gender
                maxAgeAtDiagnosis: $maxAgeAtDiagnosis
                maxHeight: $maxHeight
                maxWeight: $maxWeight
                minAgeAtDiagnosis: $minAgeAtDiagnosis
                minHeight: $minHeight
                minWeight: $minWeight
                race: $race
                sample: $sample
                slide: $slide
            )""" + query_fields + "}"
    return f


def test_patients_query_with_passed_barcode(client, common_query_builder, barcode):
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


def test_patients_query_with_passed_data_set(client, common_query_builder, data_set):
    query = common_query_builder("""{
                                    barcode
                                    slides { name }
                                    samples
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [data_set]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        slides = result['slides']
        samples = result['samples']
        assert type(result['barcode']) is str
        assert isinstance(slides, list)
        for slide in slides:
            assert type(slide['name']) is str
        assert isinstance(samples, list)
        for sample in samples:
            assert type(sample) is str


def test_patients_query_with_passed_slide_and_maxAgeAtDiagnosis(client, common_query_builder, slide, max_age_at_diagnosis):
    query = common_query_builder("""{
        ageAtDiagnosis
        samples
        slides {
            name
        }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'maxAgeAtDiagnosis': max_age_at_diagnosis, 'slide': [slide]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        slides = result['slides']
        assert isinstance(slides, list)
        assert len(slides) > 0
        for current_slide in slides:
            assert current_slide['name'] == slide
        samples = result['samples']
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert type(current_sample) is str
        assert result['ageAtDiagnosis'] <= max_age_at_diagnosis


def test_patients_query_with_passed_slide_and_minAgeAtDiagnosis(client, common_query_builder, slide, min_age_at_diagnosis):
    query = common_query_builder("""{
        ageAtDiagnosis
        samples
        slides {
            name
        }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'minAgeAtDiagnosis': min_age_at_diagnosis, 'slide': [slide]}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        slides = result['slides']
        assert isinstance(slides, list)
        assert len(slides) > 0
        for current_slide in slides:
            assert current_slide['name'] == slide
        samples = result['samples']
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert type(current_sample) is str
        assert result['ageAtDiagnosis'] >= min_age_at_diagnosis


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


def test_patients_query_with_passed_maxHeight(client, common_query_builder, max_height):
    query = common_query_builder("""{
        height
        samples
        slides {
            name
        }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'maxHeight': max_height}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['height'] <= max_height


def test_patients_query_with_passed_minHeight(client, common_query_builder, min_height):
    query = common_query_builder("""{
        height
        samples
        slides {
            name
        }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'minHeight': min_height}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['height'] >= min_height


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


def test_patients_query_with_passed_maxWeight(client, common_query_builder, max_weight):
    query = common_query_builder("""{
        weight
        samples
        slides {
            name
        }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'maxWeight': max_weight}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['weight'] <= max_weight


def test_patients_query_with_passed_minWeight(client, common_query_builder, min_weight):
    query = common_query_builder("""{
        weight
        samples
        slides {
            name
        }
    }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'minWeight': min_weight}})
    json_data = json.loads(response.data)
    results = json_data['data']['patients']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result['weight'] >= min_weight


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
        assert len(slides) > 0
        for current_slide in slides:
            assert current_slide['name'] == slide
