import json
import pytest
from tests import client, NoneType
from flaskr.enums import unit_enum
from flaskr.database import return_feature_class_query


def test_featuresByClass_query_with_feature(client):
    query = """query FeaturesByClass($dataSet: [String!], $related: [String!], $feature: [String!]) {
        featuresByClass(dataSet: $dataSet, related: $related, feature: $feature) {
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
                      'variables': {'dataSet': ['TCGA'],
                                    'related': ['Immune_Subtype'],
                                    'feature': ['Neutrophils_Aggregate2']}})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["featuresByClass"]

    assert isinstance(data_sets, list)
    assert len(data_sets) == 1
    for data_set in data_sets:
        assert type(data_set["class"]) is str
        assert isinstance(data_set["features"], list)
        # Don't need to iterate through every result.
        for feature in data_set["features"][0:2]:
            assert feature["class"] == data_set["class"]
            assert type(feature["display"]) is str or NoneType
            assert type(feature["methodTag"]) is str or NoneType
            assert feature["name"] == 'Neutrophils_Aggregate2'
            assert type(feature["order"]) is int or NoneType
            assert feature["unit"] in unit_enum.enums or type(
                feature["unit"]) is NoneType


def test_featuresByClass_query_no_feature(client):
    query = """query FeaturesByClass($dataSet: [String!], $related: [String!], $feature: [String!]) {
        featuresByClass(dataSet: $dataSet, related: $related, feature: $feature) {
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
                      'variables': {'dataSet': ['TCGA'],
                                    'related': ['Immune_Subtype']}})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["featuresByClass"]

    assert isinstance(data_sets, list)
    # Don't need to iterate through every result.
    for data_set in data_sets[0:2]:
        assert type(data_set["class"]) is str
        assert isinstance(data_set["features"], list)
        # Don't need to iterate through every result.
        for feature in data_set["features"][0:2]:
            assert feature["class"] == data_set["class"]
            assert type(feature["display"]) is str or NoneType
            assert type(feature["methodTag"]) is str or NoneType
            assert type(feature["name"]) is str
            assert type(feature["order"]) is int or NoneType
            assert feature["unit"] in unit_enum.enums or type(
                feature["unit"]) is NoneType


def test_featuresByClass_query_no_relations(client):
    query = """query FeaturesByClass($dataSet: [String!], $related: [String!], $feature: [String!]) {
        featuresByClass(dataSet: $dataSet, related: $related, feature: $feature) {
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
                      'variables': {'dataSet': ['TCGA'],
                                    'related': ['Immune_Subtype'],
                                    'feature': ['Neutrophils_Aggregate2']}})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["featuresByClass"]

    assert isinstance(data_sets, list)
    assert len(data_sets) == 1
    for data_set in data_sets:
        assert type(data_set["class"]) is str
        assert isinstance(data_set["features"], list)
        # Don't need to iterate through every result.
        for feature in data_set["features"][0:2]:
            assert 'class' not in feature
            assert type(feature["display"]) is str or NoneType
            assert 'methodTag' not in feature
            assert feature["name"] == 'Neutrophils_Aggregate2'
            assert type(feature["order"]) is int or NoneType
            assert type(
                feature["unit"]) is NoneType or feature["unit"] in unit_enum.enums


def test_featuresByClass_query_no_dataSet(client):
    query = """query FeaturesByClass($dataSet: [String!], $related: [String!], $feature: [String!]) {
        featuresByClass(dataSet: $dataSet, related: $related, feature: $feature) {
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
                      'variables': {'related': ['Immune_Subtype'],
                                    'feature': ['Neutrophils_Aggregate2']}})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["featuresByClass"]

    assert isinstance(data_sets, list)
    assert len(data_sets) == 1
    for data_set in data_sets:
        assert type(data_set["class"]) is str
        assert isinstance(data_set["features"], list)
        # Don't need to iterate through every result.
        for feature in data_set["features"][0:2]:
            assert 'class' not in feature
            assert type(feature["display"]) is str or NoneType
            assert 'methodTag' not in feature
            assert feature["name"] == 'Neutrophils_Aggregate2'
            assert type(feature["order"]) is int or NoneType
            assert type(
                feature["unit"]) is NoneType or feature["unit"] in unit_enum.enums


def test_featuresByClass_query_no_related(client):
    query = """query FeaturesByClass($dataSet: [String!], $related: [String!], $feature: [String!]) {
        featuresByClass(dataSet: $dataSet, related: $related, feature: $feature) {
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
                      'variables': {'dataSet': ['TCGA'],
                                    'feature': ['Neutrophils_Aggregate2']}})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["featuresByClass"]

    assert isinstance(data_sets, list)
    assert len(data_sets) == 1
    for data_set in data_sets:
        assert type(data_set["class"]) is str
        assert isinstance(data_set["features"], list)
        # Don't need to iterate through every result.
        for feature in data_set["features"][0:2]:
            assert 'class' not in feature
            assert type(feature["display"]) is str or NoneType
            assert 'methodTag' not in feature
            assert feature["name"] == 'Neutrophils_Aggregate2'
            assert type(feature["order"]) is int or NoneType
            assert type(
                feature["unit"]) is NoneType or feature["unit"] in unit_enum.enums


def test_featuresByClass_query_no_args(client):
    query = """query FeaturesByClass($dataSet: [String!], $related: [String!], $feature: [String!]) {
        featuresByClass(dataSet: $dataSet, related: $related, feature: $feature) {
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
    data_sets = json_data["data"]["featuresByClass"]

    # Get the total number of features in the database.
    class_count = return_feature_class_query('id').count()

    assert isinstance(data_sets, list)
    assert len(data_sets) == class_count
