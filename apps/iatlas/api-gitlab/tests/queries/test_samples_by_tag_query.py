import json
import pytest
from tests import NoneType


@pytest.fixture(scope='module')
def sample_name():
    return 'DO1328'


@pytest.fixture(scope='module')
def patient_barcode():
    return 'DO1328'


@pytest.fixture(scope='module')
def tag_name():
    return 'C1'


def test_samples_by_tag_query_with_passed_sample_name(client, sample_name):
    query = """query Samples(
        $dataSet: [String!]
        $related: [String!]
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $name: [String!]
        $patient: [String!]
    ) {
        samplesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            name: $name
            patient: $patient
        ) {
            tag
            samples {
                name
            }
         }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': [sample_name]}})
    json_data = json.loads(response.data)
    samples_by_tag = json_data['data']['samplesByTag']

    assert isinstance(samples, list)
    assert len(samples) == 1
    for group in samples_by_tag[0:2]:
        samples = group['samples']
        assert type(group['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert sample['name'] == sample_name


def test_samples_by_tag_query_with_passed_patient_barcode(client, patient_barcode):
    query = """query Samples(
        $dataSet: [String!]
        $related: [String!]
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $name: [String!]
        $patient: [String!]
    ) {
        samplesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            name: $name
            patient: $patient
        ) {
            tag
            characteristics
            samples {
                patient {
                    barcode
                }
            }
         }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'patient': [patient_barcode]}})
    json_data = json.loads(response.data)
    samples_by_tag = json_data['data']['samplesByTag']

    assert isinstance(samples, list)
    assert len(samples) == 1
    for group in samples_by_tag[0:2]:
        samples = group['samples']
        assert type(group['tag']) is str
        assert type(group['characteristics']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str
            assert sample['patient']['barcode'] == patient_barcode


def test_samples_by_tag_query_with_no_args(client):
    query = """query Samples(
        $dataSet: [String!]
        $related: [String!]
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $name: [String!]
        $patient: [String!]
    ) {
        samplesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            name: $name
            patient: $patient
        ) {
            tag
            color
            samples {
                name
            }
         }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    samples_by_tag = json_data['data']['samplesByTag']

    assert isinstance(samples, list)
    assert len(samples) == 1
    for group in samples_by_tag[0:2]:
        samples = group['samples']
        assert type(group['tag']) is str
        assert type(group['color']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str


def test_samples_by_tag_query_with_passed_patient_and_sample(client, patient_barcode, sample_name):
    query = """query Samples(
        $dataSet: [String!]
        $related: [String!]
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $name: [String!]
        $patient: [String!]
    ) {
        samplesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            name: $name
            patient: $patient
        ) {
            tag
            display
            samples {
                name
                patient {
                    barcode
                }
            }
         }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'patient': [patient_barcode],
            'sample': [sample_name]}})
    json_data = json.loads(response.data)
    samples_by_tag = json_data['data']['samplesByTag']

    assert isinstance(samples, list)
    assert len(samples) == 1
    for group in samples_by_tag[0:2]:
        samples = group['samples']
        assert type(group['tag']) is str
        assert type(group['display']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert sample['name'] == sample_name
            assert sample['patient']['barcode'] == patient_barcode


def test_samples_by_tag_query_with_passed_data_set(client, dataset):
    query = """query Samples(
        $dataSet: [String!]
        $related: [String!]
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $name: [String!]
        $patient: [String!]
    ) {
        samplesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            name: $name
            patient: $patient
        ) {
            tag
            samples {
                name
            }
         }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'dataSet': [dataset]}})
    json_data = json.loads(response.data)
    samples_by_tag = json_data['data']['samplesByTag']

    assert isinstance(samples, list)
    assert len(samples) == 1
    for group in samples_by_tag[0:2]:
        samples = group['samples']
        assert type(group['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str


def test_samples_by_tag_query_with_passed_data_set_and_related(client, dataset, related):
    query = """query Samples(
        $dataSet: [String!]
        $related: [String!]
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $name: [String!]
        $patient: [String!]
    ) {
        samplesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            name: $name
            patient: $patient
        ) {
            tag
            samples {
                name
            }
         }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [dataset],
            'related': [related]}})
    json_data = json.loads(response.data)
    samples_by_tag = json_data['data']['samplesByTag']

    assert isinstance(samples, list)
    assert len(samples) == 1
    for group in samples_by_tag[0:2]:
        samples = group['samples']
        assert type(group['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str


def test_samples_by_tag_query_with_passed_feature_and_feature_class(client, chosen_feature, feature_class):
    query = """query Samples(
        $dataSet: [String!]
        $related: [String!]
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $name: [String!]
        $patient: [String!]
    ) {
        samplesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            name: $name
            patient: $patient
        ) {
            tag
            samples {
                name
            }
         }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'feature': [chosen_feature],
            'featureClass': [feature_class]}})
    json_data = json.loads(response.data)
    samples_by_tag = json_data['data']['samplesByTag']

    assert isinstance(samples, list)
    assert len(samples) == 1
    for group in samples_by_tag[0:2]:
        samples = group['samples']
        assert type(group['tag']) is str
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str


def test_samples_by_tag_query_with_passed_tag(client, tag_name):
    query = """query Samples(
        $dataSet: [String!]
        $related: [String!]
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $name: [String!]
        $patient: [String!]
    ) {
        samplesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            name: $name
            patient: $patient
        ) {
            tag
            samples {
                name
            }
         }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'tag': [tag_name]}})
    json_data = json.loads(response.data)
    samples_by_tag = json_data['data']['samplesByTag']

    assert isinstance(samples, list)
    assert len(samples) == 1
    for group in samples_by_tag[0:2]:
        samples = group['samples']
        assert group['tag'] == tag_name
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert type(sample['name']) is str


def test_samples_by_tag_query_with_all_args(client, dataset, related, tag_name, chosen_feature, feature_class, sample_name, patient_barcode):
    query = """query Samples(
        $dataSet: [String!]
        $related: [String!]
        $tag: [String!]
        $feature: [String!]
        $featureClass: [String!]
        $name: [String!]
        $patient: [String!]
    ) {
        samplesByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            name: $name
            patient: $patient
        ) {
            tag
            samples {
                name
                patient {
                    barcode
                }
            }
         }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {
            'dataSet': [dataset],
            'related': [related],
            'tag': [tag_name],
            'feature': [chosen_feature],
            'featureClass': [feature_class],
            'sample': [sample_name],
            'patient': [patient_barcode]}})
    json_data = json.loads(response.data)
    samples_by_tag = json_data['data']['samplesByTag']

    assert isinstance(samples, list)
    assert len(samples) == 1
    for group in samples_by_tag[0:2]:
        samples = group['samples']
        assert group['tag'] == tag_name
        assert isinstance(samples, list)
        assert len(samples) > 0
        for sample in samples:
            assert sample['name'] == sample_name
            assert sample['patient']['barcode'] == patient_barcode
