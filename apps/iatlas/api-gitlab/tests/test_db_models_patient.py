import pytest
from tests import app
from flaskr.db_models import Patient


def test_patient_type(app):
    app()
    patient_type_barcode = 'DO1328'
    result = Patient.query.filter_by(barcode=patient_type_barcode).first()

    assert result.barcode == patient_type_barcode
    assert repr(result) == '<Patient %r>' % patient_type_barcode
