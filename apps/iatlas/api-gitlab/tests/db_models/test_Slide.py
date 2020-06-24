import pytest
from tests import NoneType
from api.database import return_slide_query


@pytest.fixture(scope='module')
def name():
    return 'TCGA-05-4244-01Z-00-DX1'


def test_Slide_with_relations(app, name):
    relationships_to_load = ['patients']

    query = return_slide_query(*relationships_to_load)
    result = query.filter_by(name=name).first()

    if result.patient:
        assert result.patient.id == result.patient_id
    assert result.name == name
    assert type(result.description) is str or NoneType
    assert type(result.patient_id) is int or NoneType
    assert repr(result) == '<Slide %r>' % name


def test_Slide_no_relations(app, name):
    query = return_slide_query()
    result = query.filter_by(name=name).first()

    assert type(result.patient) is NoneType
    assert result.name == name
    assert type(result.description) is str or NoneType
    assert type(result.patient_id) is int or NoneType
