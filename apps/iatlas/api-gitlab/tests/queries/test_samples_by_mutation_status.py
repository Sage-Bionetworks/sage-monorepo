import json
import pytest
from api.enums import status_enum
from tests import NoneType


@pytest.fixture(scope='module')
def mutation_id():
    return 777


@pytest.fixture(scope='module')
def mutation_status():
    return 'Mut'


# Sample id 1904
@pytest.fixture(scope='module')
def sample_name():
    return 'TCGA-38-7271'


@pytest.fixture(scope='module')
def common_query_builder():
    def f(query_fields):
        return """query SamplesByMutationStatus(
            $ageAtDiagnosis: [Int!]
            $ethnicity: [EthnicityEnum!]
            $gender: [GenderEnum!]
            $height: [Int!]
            $mutationId: [Int!]
            $mutationStatus: StatusEnum
            $patient: [String!]
            $race: [RaceEnum!]
            $sample: [String!]
            $weight: [Int!]
        ) {
            samplesByMutationStatus(
                ageAtDiagnosis: $ageAtDiagnosis
                ethnicity: $ethnicity
                gender: $gender
                height: $height
                mutationId: $mutationId
                mutationStatus: $mutationStatus
                patient: $patient
                race: $race
                sample: $sample
                weight: $weight
            )""" + query_fields + "}"
    return f


@pytest.fixture(scope='module')
def common_query(common_query_builder):
    return common_query_builder("""{
        status
        samples { name }
    }""")


def test_samples_by_mutation_status_query_with_passed_sample(client, common_query, sample_name):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'sample': [sample_name]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert current_sample['name'] == sample_name


def test_samples_by_mutation_status_query_with_passed_mutation_id(client, common_query, mutation_id):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'mutationId': [mutation_id]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert type(current_sample['name']) is str


def test_samples_by_mutation_status_query_with_no_args(client, common_query):
    response = client.post('/api', json={'query': common_query})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert type(current_sample['name']) is str


def test_samples_by_mutation_status_query_with_passed_mutation_status(client, common_query, mutation_status):
    response = client.post(
        '/api', json={'query': common_query, 'variables': {'mutationStatus': mutation_status}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] == mutation_status
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert type(current_sample['name']) is str


def test_samples_by_mutation_status_query_with_all_args(client, common_query, mutation_id, mutation_status, sample_name):
    response = client.post('/api', json={'query': common_query, 'variables': {
        'mutationId': [mutation_id],
        'mutationStatus': mutation_status,
        'sample': [sample_name]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] == mutation_status
        assert isinstance(samples, list)
        assert len(samples) == 1
        for current_sample in samples:
            assert current_sample['name'] == sample_name


def test_samples_by_mutation_status_query_with_passed_ageAtDiagnosis(client, common_query_builder, age_at_diagnosis):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { ageAtDiagnosis }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'ageAtDiagnosis': [age_at_diagnosis]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert current_sample['patient']['ageAtDiagnosis'] == age_at_diagnosis


def test_samples_by_mutation_status_query_with_passed_ethnicity(client, common_query_builder, ethnicity):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { ethnicity }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'ethnicity': [ethnicity]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert current_sample['patient']['ethnicity'] == ethnicity


def test_samples_by_mutation_status_query_with_passed_gender(client, common_query_builder, gender):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { gender }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'gender': [gender]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert current_sample['patient']['gender'] == gender


def test_samples_by_mutation_status_query_with_passed_height(client, common_query_builder, height):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { height }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'height': [height]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert current_sample['patient']['height'] == height


def test_samples_by_mutation_status_query_with_passed_patient(client, common_query_builder, patient):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { barcode }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'patient': [patient]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert current_sample['patient']['barcode'] == patient


def test_samples_by_mutation_status_query_with_passed_race(client, common_query_builder, race):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { race }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'race': [race]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert current_sample['patient']['race'] == race


def test_samples_by_mutation_status_query_with_passed_weight(client, common_query_builder, weight):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { weight }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'weight': [weight]}})
    json_data = json.loads(response.data)
    results = json_data['data']['samplesByMutationStatus']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        samples = result['samples']
        assert result['status'] in status_enum.enums
        assert isinstance(samples, list)
        assert len(samples) > 0
        for current_sample in samples[0:2]:
            assert current_sample['patient']['weight'] == weight
