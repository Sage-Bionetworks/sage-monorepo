import pytest
from tests import app
from flaskr.db_models import TherapyType


def test_TherapyType(app):
    app()
    name = 'T-cell targeted immunomodulator'
    result = TherapyType.query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<TherapyType %r>' % name
