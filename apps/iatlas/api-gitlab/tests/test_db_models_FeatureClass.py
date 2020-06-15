import pytest
from tests import app
from flaskr.database import return_feature_class_query


def test_FeatureClass_with_relations(app):
    app()
    name = 'Adaptive Receptor - B cell'
    relationships_to_join = ['features']

    query = return_feature_class_query(*relationships_to_join)
    result = query.filter_by(name=name).first()

    assert isinstance(result.features, list)
    # Don't need to iterate through every result.
    for feature in result.features[0:2]:
        assert type(feature.name) is str
    assert type(result.id) is int
    assert result.name == name
    assert repr(result) == '<FeatureClass %r>' % name


def test_FeatureClass_no_relations(app):
    app()
    name = 'Adaptive Receptor - B cell'

    query = return_feature_class_query()
    result = query.filter_by(name=name).first()

    assert result.features == []
    assert type(result.id) is int
    assert result.name == name
