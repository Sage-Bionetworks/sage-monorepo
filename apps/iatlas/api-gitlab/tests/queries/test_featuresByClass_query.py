import json
import pytest
from tests import NoneType
from api.enums import unit_enum
from api.database import return_feature_class_query


def test_featuresByClass_query_with_feature(client, data_set, related, chosen_feature):
    query = """query FeaturesByClass(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByClass(
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
            features {
                class
                display
                methodTag
                name
                order
                samples {
                    name
                    value
                }
                unit
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    results = json_data['data']['featuresByClass']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        features = result['features']
        assert type(result['class']) is str
        assert isinstance(features, list)
        assert len(features) > 0
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            samples = feature['samples']
            assert feature['class'] == result['class']
            assert type(feature['display']) is str or NoneType
            assert type(feature['methodTag']) is str or NoneType
            assert feature['name'] == chosen_feature
            assert type(feature['order']) is int or NoneType
            assert isinstance(samples, list)
            assert len(samples) > 0
            # Don't need to iterate through every result.
            for current_sample in samples[0:2]:
                assert type(current_sample['name']) is str
                assert type(current_sample['value']) is float
            assert feature['unit'] in unit_enum.enums or type(
                feature['unit']) is NoneType


def test_featuresByClass_query_with_feature_no_sample_or_value(client, data_set, related, chosen_feature):
    query = """query FeaturesByClass(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByClass(
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
            features {
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    results = json_data['data']['featuresByClass']

    assert isinstance(results, list)
    assert len(results) == 1


def test_featuresByClass_query_no_feature(client, data_set, related):
    query = """query FeaturesByClass(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByClass(
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
            features {
                class
                display
                methodTag
                name
                order
                unit
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related]}})
    json_data = json.loads(response.data)
    results = json_data['data']['featuresByClass']

    assert isinstance(results, list)
    # Don't need to iterate through every result.
    for result in results[0:2]:
        features = result['features']
        assert type(result['class']) is str
        assert isinstance(features, list)
        assert len(features) > 0
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            assert feature['class'] == result['class']
            assert type(feature['display']) is str or NoneType
            assert type(feature['methodTag']) is str or NoneType
            assert type(feature['name']) is str
            assert type(feature['order']) is int or NoneType
            assert feature['unit'] in unit_enum.enums or type(
                feature['unit']) is NoneType


def test_featuresByClass_query_no_relations(client, data_set, related, chosen_feature):
    query = """query FeaturesByClass(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByClass(
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
            features {
                display
                name
                order
                unit
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    results = json_data['data']['featuresByClass']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        features = result['features']
        assert type(result['class']) is str
        assert isinstance(features, list)
        assert len(features) == 1
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            assert 'class' not in feature
            assert type(feature['display']) is str or NoneType
            assert 'methodTag' not in feature
            assert feature['name'] == chosen_feature
            assert type(feature['order']) is int or NoneType
            assert type(
                feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_featuresByClass_query_no_data_set(client, related, chosen_feature):
    query = """query FeaturesByClass(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByClass(
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
            features {
                display
                name
                order
                unit
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'related': [related],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    results = json_data['data']['featuresByClass']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        features = result['features']
        assert type(result['class']) is str
        assert isinstance(features, list)
        assert len(features) == 1
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            assert 'class' not in feature
            assert type(feature['display']) is str or NoneType
            assert 'methodTag' not in feature
            assert feature['name'] == chosen_feature
            assert type(feature['order']) is int or NoneType
            assert type(
                feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_featuresByClass_query_no_related(client, data_set, chosen_feature):
    query = """query FeaturesByClass(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByClass(
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
            features {
                display
                name
                order
                unit
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'feature': [chosen_feature]}})
    json_data = json.loads(response.data)
    results = json_data['data']['featuresByClass']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        features = result['features']
        assert type(result['class']) is str
        assert isinstance(features, list)
        assert len(features) == 1
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            assert 'class' not in feature
            assert type(feature['display']) is str or NoneType
            assert 'methodTag' not in feature
            assert feature['name'] == chosen_feature
            assert type(feature['order']) is int or NoneType
            assert type(
                feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_featuresByClass_query_no_args(client):
    query = """query FeaturesByClass(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByClass(
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
            features {
                display
                name
                order
                unit
            }
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    results = json_data['data']['featuresByClass']

    # Get the total number of features in the database.
    class_count = return_feature_class_query('id').count()

    assert isinstance(results, list)
    assert len(results) == class_count


def test_featuresByClass_query_with_feature_class(client, data_set, related, chosen_feature, feature_class):
    query = """query FeaturesByClass(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByClass(
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
            features {
                class
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': [data_set],
                                    'related': [related],
                                    'feature': [chosen_feature],
                                    'featureClass': [feature_class]}})
    json_data = json.loads(response.data)
    results = json_data['data']['featuresByClass']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        features = result['features']
        assert result['class'] == feature_class
        assert isinstance(features, list)
        assert len(features) == 1
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            assert feature['class'] == feature_class
            assert feature['name'] == chosen_feature


def test_featuresByClass_query_with_just_feature_class(client, feature_class):
    query = """query FeaturesByClass(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByClass(
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
            features {
                class
                name
            }
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'featureClass': [feature_class]}})
    json_data = json.loads(response.data)
    results = json_data['data']['featuresByClass']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        features = result['features']
        assert result['class'] == feature_class
        assert isinstance(features, list)
        assert len(features) > 0
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            assert feature['class'] == feature_class
            assert type(feature['name']) is str


def test_featuresByClass_query_with_just_feature_and_feature_class(client, feature_class):
    query = """query FeaturesByClass(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByClass(
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
            features {
                class
                name
                samples {
                    name
                    value
                }
            }
        }
    }"""
    chosen_feature = 'NP_mean'
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'feature': [chosen_feature],
                                    'featureClass': [feature_class]}})
    json_data = json.loads(response.data)
    results = json_data['data']['featuresByClass']

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        features = result['features']
        assert result['class'] == feature_class
        assert isinstance(features, list)
        assert len(features) > 0
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            samples = feature['samples']
            assert feature['class'] == feature_class
            assert feature['name'] == chosen_feature
            assert isinstance(samples, list)
            assert len(samples) > 0
            # Don't need to iterate through every result.
            for current_sample in samples[0:2]:
                assert type(current_sample['name']) is str
                assert type(current_sample['value']) is float
