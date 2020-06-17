import json
import pytest
from tests import client, NoneType
from flaskr.enums import unit_enum
from flaskr.database import return_feature_query


def test_features_query_with_feature(client):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature) {
            class
            display
            methodTag
            name
            order
            unit
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': ['TCGA'],
                                    'related': ['Immune_Subtype'],
                                    'feature': ['Neutrophils_Aggregate2']}})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["features"]

    assert isinstance(data_sets, list)
    assert len(data_sets) == 1
    for data_set in data_sets:
        assert type(data_set["class"]) is str
        assert type(data_set["display"]) is str or NoneType
        assert type(data_set["methodTag"]) is str or NoneType
        assert data_set["name"] == 'Neutrophils_Aggregate2'
        assert type(data_set["order"]) is int or NoneType
        assert data_set["unit"] in unit_enum.enums or type(
            data_set["unit"]) is NoneType


def test_features_query_no_feature(client):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature) {
            class
            display
            methodTag
            name
            order
            unit
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': ['TCGA'],
                                    'related': ['Immune_Subtype']}})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["features"]

    assert isinstance(data_sets, list)
    # Don't need to iterate through every result.
    for data_set in data_sets[0:2]:
        assert type(data_set["class"]) is str
        assert type(data_set["display"]) is str or NoneType
        assert type(data_set["methodTag"]) is str or NoneType
        assert type(data_set["name"]) is str
        assert type(data_set["order"]) is int or NoneType
        assert data_set["unit"] in unit_enum.enums or type(
            data_set["unit"]) is NoneType


def test_features_query_no_relations(client):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature) {
            display
            name
            order
            unit
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': ['TCGA'],
                                    'related': ['Immune_Subtype'],
                                    'feature': ['Neutrophils_Aggregate2']}})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["features"]

    assert isinstance(data_sets, list)
    assert len(data_sets) == 1
    for data_set in data_sets:
        assert 'class' not in data_set
        assert type(data_set["display"]) is str or NoneType
        assert 'methodTag' not in data_set
        assert data_set["name"] == 'Neutrophils_Aggregate2'
        assert type(data_set["order"]) is int or NoneType
        assert data_set["unit"] in unit_enum.enums or type(
            data_set["unit"]) is NoneType


def test_features_query_no_dataSet(client):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature) {
            display
            name
            order
            unit
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'related': ['Immune_Subtype'],
                                    'feature': ['Neutrophils_Aggregate2']}})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["features"]

    assert isinstance(data_sets, list)
    assert len(data_sets) == 1
    for data_set in data_sets:
        assert 'class' not in data_set
        assert type(data_set["display"]) is str or NoneType
        assert 'methodTag' not in data_set
        assert data_set["name"] == 'Neutrophils_Aggregate2'
        assert type(data_set["order"]) is int or NoneType
        assert data_set["unit"] in unit_enum.enums or type(
            data_set["unit"]) is NoneType


def test_features_query_no_related(client):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature) {
            display
            name
            order
            unit
        }
    }"""
    response = client.post(
        '/api', json={'query': query,
                      'variables': {'dataSet': ['TCGA'],
                                    'feature': ['Neutrophils_Aggregate2']}})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["features"]

    assert isinstance(data_sets, list)
    assert len(data_sets) == 1
    for data_set in data_sets:
        assert 'class' not in data_set
        assert type(data_set["display"]) is str or NoneType
        assert 'methodTag' not in data_set
        assert data_set["name"] == 'Neutrophils_Aggregate2'
        assert type(data_set["order"]) is int or NoneType
        assert data_set["unit"] in unit_enum.enums or type(
            data_set["unit"]) is NoneType


def test_features_query_no_args(client):
    query = """query Features($dataSet: [String!], $related: [String!], $feature: [String!]) {
        features(dataSet: $dataSet, related: $related, feature: $feature) {
            display
            name
            order
            unit
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    data_sets = json_data["data"]["features"]

    # Get the total number of features in the database.
    feature_count = return_feature_query('id').count()

    assert isinstance(data_sets, list)
    assert len(data_sets) == feature_count
