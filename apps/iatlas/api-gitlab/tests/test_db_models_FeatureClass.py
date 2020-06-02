import pytest
from tests import app
from flaskr.db_models import FeatureClass


def test_FeatureClass(app):
    app()
    name = 'Adaptive Receptor - B cell'
    result = FeatureClass.query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<FeatureClass %r>' % name
