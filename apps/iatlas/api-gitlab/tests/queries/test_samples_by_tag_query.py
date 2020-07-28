import json
import pytest
from tests import NoneType


def test_samples_by_tag_query_with_passed_sample(client, sample):
    query = """query SamplesByTag(
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
            samples { name }
         }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'name': [sample]}})
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


def test_samples_by_tag_query_with_passed_patient(client, patient):
    query = """query SamplesByTag(
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


def test_samples_by_tag_query_with_no_args(client):
    query = """query SamplesByTag(
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


def test_samples_by_tag_query_with_passed_patient_and_sample(client, patient, sample):
    query = """query SamplesByTag(
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


def test_samples_by_tag_query_with_passed_data_set(client, data_set):
    query = """query SamplesByTag(
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
        '/api', json={'query': query, 'variables': {'dataSet': [data_set]}})
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


def test_samples_by_tag_query_with_passed_data_set_and_related(client, data_set, related):
    query = """query SamplesByTag(
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


def test_samples_by_tag_query_with_passed_feature_and_feature_class(client, chosen_feature, feature_class):
    query = """query SamplesByTag(
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


def test_samples_by_tag_query_with_passed_tag(client, tag):
    query = """query SamplesByTag(
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
        '/api', json={'query': query, 'variables': {'tag': [tag]}})
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


def test_samples_by_tag_query_with_all_args(client, data_set, related, tag, chosen_feature, feature_class, sample, patient):
    query = """query SamplesByTag(
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
        assert len(samples) > 0
        for current_sample in samples:
            assert current_sample['name'] == sample
            assert current_sample['patient']['barcode'] == patient
