import pytest
from tests import app, NoneType
from flaskr.database import return_patient_query

barcode = 'DO1328'


def test_Patient_with_relations(app):
    app()
    relationships_to_load = ['samples', 'slides']

    query = return_patient_query(*relationships_to_load)
    result = query.filter_by(barcode=barcode).first()

    if result.samples:
        assert isinstance(result.samples, list)
        # Don't need to iterate through every result.
        for sample in result.samples[0:2]:
            assert type(sample.name) is str
    if result.slides:
        assert isinstance(result.slides, list)
        # Don't need to iterate through every result.
        for slide in result.slides[0:2]:
            assert type(slide.name) is str
    assert result.barcode == barcode
    assert type(result.age) is int or NoneType
    assert type(result.ethnicity) is str or NoneType
    assert type(result.gender) is str or NoneType
    assert type(result.height) is int or NoneType
    assert type(result.race) is str or NoneType
    assert type(result.weight) is int or NoneType
    assert repr(result) == '<Patient %r>' % barcode


def test_Patient_no_relations(app):
    app()

    query = return_patient_query()
    result = query.filter_by(barcode=barcode).first()

    assert result.samples == []
    assert result.slides == []
    assert result.barcode == barcode
    assert type(result.age) is int or NoneType
    assert type(result.ethnicity) is str or NoneType
    assert type(result.gender) is str or NoneType
    assert type(result.height) is int or NoneType
    assert type(result.race) is str or NoneType
    assert type(result.weight) is int or NoneType
