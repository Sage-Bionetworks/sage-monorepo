import pytest
from tests import app, NoneType
from flaskr.db_models import Slide


def test_Slide(app):
    app()
    name = 'TCGA-05-4244-01Z-00-DX1'
    result = Slide.query.filter_by(name=name).first()

    assert result.name == name
    assert type(result.description) is str or NoneType
    assert type(result.patient_id) is int or NoneType
    assert repr(result) == '<Slide %r>' % name
