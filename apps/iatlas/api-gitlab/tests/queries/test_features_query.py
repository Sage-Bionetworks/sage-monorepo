import json
import pytest
from tests import NoneType
from api.enums import unit_enum
from api.database import return_feature_query


@pytest.fixture(scope='module')
def feature_name():
    return 'Eosinophils'


@pytest.fixture(scope='module')
def germline_category():
    return 'Leukocyte Subset ES'


@pytest.fixture(scope='module')
def germline_module():
    return 'Cytotoxic'


@pytest.fixture(scope='module')
def max_value():
    return 5.7561021


@pytest.fixture(scope='module')
def min_value():
    return 0.094192693


@pytest.fixture(scope='module')
def common_query():
    return """query Features(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        features(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            display
            name
            order
            unit
            germline_module
            germline_category
        }
    }"""


def test_features_query_with_feature(client, feature_name, germline_category, germline_module):
    query = """query Features(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        features(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            class
            display
            methodTag
            name
            order
            unit
            germline_module
            germline_category
            samples {
                name
                value
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query, 'variables': {'feature': [feature_name]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    for feature in features:
        samples = feature['samples']
        assert type(feature['class']) is str
        assert type(feature['display']) is str or NoneType
        assert type(feature['methodTag']) is str or NoneType
        assert feature['name'] == feature_name
        assert type(feature['order']) is int or NoneType
        assert feature['unit'] in unit_enum.enums or type(
            feature['unit']) is NoneType
        assert type(feature['germline_module']) is str or NoneType
        assert type(feature['germline_category']) is str or NoneType
        assert isinstance(samples, list)
        assert len(samples) > 0
        # Don't need to iterate through every result.
        for current_sample in samples[0:2]:
            assert type(current_sample['name']) is str
            assert type(current_sample['value']) is float


def test_features_query_with_feature_no_sample_or_value(client, data_set, related, chosen_feature):
    query = """query Features(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        features(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) { name }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) == 1


def test_features_query_no_feature(client, data_set, related):
    query = """query Features(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        features(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            class
            display
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    # Don't need to iterate through every result.
    for feature in features[0:2]:
        assert type(feature['class']) is str
        assert type(feature['display']) is str or NoneType


def test_features_query_with_passed_sample(client, common_query, data_set, related, sample):
    response = client.post(
        '/api', json={'query': common_query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'sample': [sample]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    # Don't need to iterate through every result.
    for feature in features[0:2]:
        assert type(feature['display']) is str or NoneType
        assert type(feature['name']) is str
        assert type(feature['order']) is int or NoneType
        assert feature['unit'] in unit_enum.enums or type(
            feature['unit']) is NoneType


def test_features_query_max_value(client, data_set, related, chosen_feature):
    query = """query Features(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        features(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            name
            valueMax
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    # Don't need to iterate through every result.
    for feature in features[0:2]:
        assert feature['name'] == chosen_feature
        assert type(feature['valueMax']) is float


def test_features_query_min_value(client, data_set, related, chosen_feature):
    query = """query Features(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        features(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            name
            valueMin
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    # Don't need to iterate through every result.
    for feature in features[0:2]:
        assert feature['name'] == chosen_feature
        assert type(feature['valueMin']) is float


def test_features_query_with_passed_max_value(client, data_set, related, chosen_feature, max_value):
    query = """query Features(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        features(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            name
            valueMax
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature],
                                    'maxValue': max_value}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    # Don't need to iterate through every result.
    for feature in features[0:2]:
        assert feature['name'] == chosen_feature
        assert feature['valueMax'] <= max_value


def test_features_query_with_passed_min_value(client, data_set, related, chosen_feature, min_value):
    query = """query Features(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        features(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            name
            valueMin
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature],
                                    'minValue': min_value}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    # Don't need to iterate through every result.
    for feature in features[0:2]:
        assert feature['name'] == chosen_feature
        assert feature['valueMin'] >= min_value


def test_features_query_no_relations(client, common_query, data_set, related, chosen_feature):
    response = client.post(
        '/api', json={'query': common_query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) == 1
    for feature in features:
        assert 'class' not in feature
        assert type(feature['display']) is str or NoneType
        assert 'methodTag' not in feature
        assert feature['name'] == chosen_feature
        assert type(feature['order']) is int or NoneType
        assert type(
            feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_features_query_no_dataSet(client, common_query, related, chosen_feature):
    response = client.post(
        '/api', json={'query': common_query,
                      'variables': {'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) == 1
    for feature in features:
        assert 'class' not in feature
        assert type(feature['display']) is str or NoneType
        assert 'methodTag' not in feature
        assert feature['name'] == chosen_feature
        assert type(feature['order']) is int or NoneType
        assert type(
            feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_features_query_no_related(client, common_query, data_set, chosen_feature):
    response = client.post(
        '/api', json={'query': common_query,
                      'variables': {'dataSet': [data_set],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) == 1
    for feature in features:
        assert 'class' not in feature
        assert type(feature['display']) is str or NoneType
        assert 'methodTag' not in feature
        assert feature['name'] == chosen_feature
        assert type(feature['order']) is int or NoneType
        assert type(
            feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_features_query_no_args(client, common_query):
    response = client.post('/api', json={'query': common_query})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    # Get the total number of features in the database.
    feature_count = return_feature_query('id').count()

    assert isinstance(features, list)
    assert len(features) == feature_count


def test_features_query_with_feature_class(client, data_set, related, chosen_feature, feature_class):
    query = """query Features(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        features(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            class
            name
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature],
                                    'featureClass': [feature_class]}})
    json_data = json.loads(response.data)
    features = json_data['data']['features']

    assert isinstance(features, list)
    assert len(features) > 0
    for feature in features:
        assert feature['class'] == feature_class
        assert feature['name'] == chosen_feature
