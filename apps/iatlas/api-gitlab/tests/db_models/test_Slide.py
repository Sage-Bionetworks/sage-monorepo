import pytest
from tests import NoneType
from api.database import return_slide_query


def test_Slide_with_relations(app, slide):
    relationships_to_load = ['patient']

    query = return_slide_query(*relationships_to_load)
    result = query.filter_by(name=slide).one_or_none()

    assert result
    assert result.patient.id == result.patient_id
    assert result.name == slide
    assert type(result.description) is str or NoneType
    assert type(result.patient_id) is int
    assert repr(result) == '<Slide %r>' % slide


def test_Slide_no_relations(app, slide):
    query = return_slide_query()
    result = query.filter_by(name=slide).one_or_none()

    assert result
    assert type(result.patient) is NoneType
    assert result.name == slide
    assert type(result.description) is str or NoneType
    assert type(result.patient_id) is int
