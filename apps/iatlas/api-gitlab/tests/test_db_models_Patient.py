import pytest
from tests import app, NoneType
from flaskr.database import return_patient_query


def test_Patient(app):
    app()
    barcode = 'DO1328'

    query = return_patient_query()
    result = query.filter_by(barcode=barcode).first()

    assert result.barcode == barcode
    assert type(result.age) is int or NoneType
    assert type(result.ethnicity) is str or NoneType
    assert type(result.gender) is str or NoneType
    assert type(result.height) is int or NoneType
    assert type(result.race) is str or NoneType
    assert type(result.weight) is int or NoneType
    assert repr(result) == '<Patient %r>' % barcode
