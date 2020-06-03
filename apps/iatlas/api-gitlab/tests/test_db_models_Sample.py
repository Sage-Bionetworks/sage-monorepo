import pytest
from tests import app, NoneType
from flaskr.db_models import Sample


def test_Sample(app):
    app()
    name = 'DO1328'
    result = Sample.query.filter_by(name=name).first()

    assert result.name == name
    assert type(result.patient_id) is int or NoneType
    assert repr(result) == '<Sample %r>' % name
