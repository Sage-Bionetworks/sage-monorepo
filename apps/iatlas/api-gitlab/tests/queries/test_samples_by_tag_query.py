import json
import pytest
from tests import NoneType


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query SamplesByTag(
        $ageAtDiagnosis: [Int!]
        $dataSet: [String!]
        $ethnicity: [EthnicityEnum!]
        $feature: [String!]
        $featureClass: [String!]
        $gender: [GenderEnum!]
        $height: [Int!]
        $name: [String!]
        $patient: [String!]
        $race: [RaceEnum!]
        $related: [String!]
        $tag: [String!]
        $weight: [Int!]
    ) {
        samplesByTag(
            ageAtDiagnosis: $ageAtDiagnosis
            dataSet: $dataSet
            ethnicity: $ethnicity
            feature: $feature
            featureClass: $featureClass
            gender: $gender
            height: $height
            name: $name
            patient: $patient
            race: $race
            related: $related
            tag: $tag
            weight: $weight
        )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder("""{
            tag
            samples { name }
         }""")


def test_samples_by_tag_query_with_passed_sample(client, common_query, sample):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'name': [sample]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['name'] == sample


def test_samples_by_tag_query_with_passed_patient(client, common_query_builder, patient):
    query = common_query_builder("""{
                                    tag
                                    characteristics
                                    samples {
                                        patient { barcode }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'patient': [patient]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['patient']['barcode'] == patient


def test_samples_by_tag_query_with_no_args(client, common_query_builder):
    query = common_query_builder("""{
                                    tag
                                    color
                                    samples { name }
                                }""")
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert type(result['color']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert type(current_sample['name']) is str


def test_samples_by_tag_query_with_passed_patient_and_sample(client, common_query_builder, patient, sample):
    query = common_query_builder("""{
                                    tag
                                    display
                                    samples {
                                        name
                                        patient { barcode }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'patient': [patient],
            'sample': [sample]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert type(result['display']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['name'] == sample
            assert current_sample['patient']['barcode'] == patient


def test_samples_by_tag_query_with_passed_data_set(client, common_query, data_set):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'dataSet': [data_set]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 1
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert type(current_sample['name']) is str


def test_samples_by_tag_query_with_passed_data_set_and_related(client, common_query, data_set, related):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'dataSet': [data_set],
            'related': [related]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert type(current_sample['name']) is str


def test_samples_by_tag_query_with_passed_feature_and_feature_class(client, common_query, chosen_feature, feature_class):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {
            'feature': [chosen_feature],
            'featureClass': [feature_class]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert type(current_sample['name']) is str


def test_samples_by_tag_query_with_passed_tag(client, common_query, tag):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'tag': [tag]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        samples = result['samples']
        assert result['tag'] == tag
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert type(current_sample['name']) is str


def test_samples_by_tag_query_with_all_args(client, common_query_builder, data_set, related, tag, chosen_feature, feature_class, sample, patient):
    query = common_query_builder("""{
                                    tag
                                    samples {
                                        name
                                        patient { barcode }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [data_set],
            'related': [related],
            'tag': [tag],
            'feature': [chosen_feature],
            'featureClass': [feature_class],
            'sample': [sample],
            'patient': [patient]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        samples = result['samples']
        assert result['tag'] == tag
        assert isinstance(samples, list)
        assert len(samples) == 1
        for current_sample in samples:
            assert current_sample['name'] == sample
            assert current_sample['patient']['barcode'] == patient


def test_samples_by_tag_query_with_passed_ageAtDiagnosis(client, common_query_builder, age_at_diagnosis):
    query = common_query_builder("""{
                                    tag
                                    samples {
                                        patient { ageAtDiagnosis }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'ageAtDiagnosis': [age_at_diagnosis]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['patient']['ageAtDiagnosis'] == age_at_diagnosis


def test_samples_by_tag_query_with_passed_ethnicity(client, common_query_builder, ethnicity):
    query = common_query_builder("""{
                                    tag
                                    samples {
                                        patient { ethnicity }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'ethnicity': [ethnicity]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['patient']['ethnicity'] == ethnicity


def test_samples_by_tag_query_with_passed_gender(client, common_query_builder, gender):
    query = common_query_builder("""{
                                    tag
                                    samples {
                                        patient { gender }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'gender': [gender]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['patient']['gender'] == gender


def test_samples_by_tag_query_with_passed_height(client, common_query_builder, height):
    query = common_query_builder("""{
                                    tag
                                    samples {
                                        patient { height }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'height': [height]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['patient']['height'] == height


def test_samples_by_tag_query_with_passed_race(client, common_query_builder, race):
    query = common_query_builder("""{
                                    tag
                                    samples {
                                        patient { race }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'race': [race]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['patient']['race'] == race


def test_samples_by_tag_query_with_passed_weight(client, common_query_builder, weight):
    query = common_query_builder("""{
                                    tag
                                    samples {
                                        patient { weight }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'weight': [weight]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert type(result['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['patient']['weight'] == weight
