import pytest
from tests import app, NoneType
from flaskr.database import return_slide_query


def test_Slide_with_relations(app):
    app()
    name = 'TCGA-05-4244-01Z-00-DX1'
    relationships_to_load = ['patients']

    query = return_slide_query(*relationships_to_load)
    result = query.filter_by(name=name).first()

    if type(result.patient) is not NoneType:
        assert result.patient.id == result.patient_id
    assert result.name == name
    assert type(result.description) is str or NoneType
    assert type(result.patient_id) is int or NoneType
    assert repr(result) == '<Slide %r>' % name


def test_Slide_no_relations(app):
    app()
    name = 'TCGA-05-4244-01Z-00-DX1'

    query = return_slide_query()
    result = query.filter_by(name=name).first()

    assert type(result.patient) is NoneType
    assert result.name == name
    assert type(result.description) is str or NoneType
    assert type(result.patient_id) is int or NoneType
