import pytest
from tests import app
from flaskr.database import return_therapy_type_query


def test_TherapyType(app):
    app()
    name = 'T-cell targeted immunomodulator'

    query = return_therapy_type_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<TherapyType %r>' % name
