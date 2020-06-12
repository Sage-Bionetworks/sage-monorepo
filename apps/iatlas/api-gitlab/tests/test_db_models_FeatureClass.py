import pytest
from tests import app
from flaskr.database import return_feature_class_query


def test_FeatureClass(app):
    app()
    name = 'Adaptive Receptor - B cell'
    fields_to_return = ['name']

    query = return_feature_class_query(*fields_to_return)
    result = query.filter_by(name=name).first()

    assert result.name == name

    query = return_feature_class_query()
    result = query.filter_by(name=name).first()

    assert type(result.id) is int
    assert result.name == name
    assert repr(result) == '<FeatureClass %r>' % name
