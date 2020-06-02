import pytest
from tests import app
from flaskr.db_models import TherapyType


def test_therapy_type(app):
    app()
    therapy_type_name = 'Other immunomodulator'
    result = TherapyType.query.filter_by(name=therapy_type_name).first()

    assert result.name == therapy_type_name
    assert repr(result) == '<TherapyType %r>' % therapy_type_name
