import json
import pytest
from tests import NoneType
from api.enums import unit_enum
from api.database import return_feature_class_query


def test_featuresByTag_query_with_feature(client, data_set, related, chosen_feature):
    query = """query FeaturesByTag(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            tag
            characteristics
            shortDisplay
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
    results = json_data['data']['featuresByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        features = result['features']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert isinstance(features, list)
        assert len(features) > 0
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            samples = feature['samples']
            assert type(feature['class']) is str or NoneType
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
            assert type(
                feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_featuresByTag_query_no_feature(client, data_set, related):
    query = """query FeaturesByTag(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            tag
            characteristics
            longDisplay
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
    results = json_data['data']['featuresByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    # Don't need to iterate through every result.
    for result in results[0:2]:
        features = result['features']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['longDisplay']) is str or NoneType
        assert isinstance(features, list)
        assert len(features) > 0
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            assert type(feature['class']) is str or NoneType
            assert type(feature['display']) is str or NoneType
            assert type(feature['methodTag']) is str or NoneType
            assert type(feature['name']) is str
            assert type(feature['order']) is int or NoneType
            assert type(
                feature['unit']) is NoneType or feature['unit'] in unit_enum.enums


def test_featuresByTag_query_no_relations(client, data_set, related, chosen_feature):
    query = """query FeaturesByTag(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            tag
            characteristics
            shortDisplay
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
    results = json_data['data']['featuresByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        features = result['features']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
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


def test_featuresByTag_query_with_feature_class(client, data_set, related, chosen_feature, feature_class):
    query = """query FeaturesByTag(
            $dataSet: [String!]
            $related: [String!]
            $tag: [String!]
            $feature: [String!]
            $featureClass: [String!]
            $sample: [String!]
            $minValue: Float
            $maxValue: Float
        ) {
        featuresByTag(
            dataSet: $dataSet
            related: $related
            tag: $tag
            feature: $feature
            featureClass: $featureClass
            sample: $sample
            minValue: $minValue
            maxValue: $maxValue
        ) {
            tag
            characteristics
            shortDisplay
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
    results = json_data['data']['featuresByTag']

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        features = result['features']
        assert type(result['tag']) is str
        assert type(result['characteristics']) is str or NoneType
        assert type(result['shortDisplay']) is str or NoneType
        assert isinstance(features, list)
        assert len(features) == 1
        # Don't need to iterate through every result.
        for feature in features[0:2]:
            assert feature['class'] == feature_class
            assert feature['name'] == chosen_feature
