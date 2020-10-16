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
            $ethnicity: [EthnicityEnum!]
            $gender: [GenderEnum!]
            $maxAgeAtDiagnosis: Int
            $maxHeight: Float
            $maxWeight: Float
            $minAgeAtDiagnosis: Int
            $minHeight: Float
            $minWeight: Float
            $mutationId: [Int!]
            $mutationStatus: StatusEnum
            $patient: [String!]
            $race: [RaceEnum!]
            $sample: [String!]
        ) {
            samplesByMutationStatus(
                ethnicity: $ethnicity
                gender: $gender
                maxAgeAtDiagnosis: $maxAgeAtDiagnosis
                maxHeight: $maxHeight
                maxWeight: $maxWeight
                minAgeAtDiagnosis: $minAgeAtDiagnosis
                minHeight: $minHeight
                minWeight: $minWeight
                mutationId: $mutationId
                mutationStatus: $mutationStatus
                patient: $patient
                race: $race
                sample: $sample
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


def test_samples_by_mutation_status_query_with_passed_maxAgeAtDiagnosis(client, common_query_builder, max_age_at_diagnosis):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { ageAtDiagnosis }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'maxAgeAtDiagnosis': max_age_at_diagnosis}})
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
            assert current_sample['patient']['ageAtDiagnosis'] <= max_age_at_diagnosis


def test_samples_by_mutation_status_query_with_passed_minAgeAtDiagnosis(client, common_query_builder, min_age_at_diagnosis):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { ageAtDiagnosis }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'minAgeAtDiagnosis': min_age_at_diagnosis}})
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
            assert current_sample['patient']['ageAtDiagnosis'] >= min_age_at_diagnosis


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


def test_samples_by_mutation_status_query_with_passed_maxHeight(client, common_query_builder, max_height):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { height }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'maxHeight': max_height}})
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
            assert current_sample['patient']['height'] <= max_height


def test_samples_by_mutation_status_query_with_passed_minHeight(client, common_query_builder, min_height):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { height }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'minHeight': min_height}})
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
            assert current_sample['patient']['height'] >= min_height


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


def test_samples_by_mutation_status_query_with_passed_maxWeight(client, common_query_builder, max_weight):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { weight }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'maxWeight': max_weight}})
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
            assert current_sample['patient']['weight'] <= max_weight


def test_samples_by_mutation_status_query_with_passed_minWeight(client, common_query_builder, min_weight):
    query = common_query_builder("""{
                                    status
                                    samples {
                                        patient { weight }
                                    }
                                }""")
    response = client.post(
        '/api', json={'query': query, 'variables': {'minWeight': min_weight}})
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
            assert current_sample['patient']['weight'] >= min_weight
