import pytest
from tests import NoneType
from api.database import return_patient_query
from api.enums import ethnicity_enum, gender_enum, race_enum


@pytest.fixture(scope='module')
def name():
    return 'TCGA-WN-AB4C'


def test_Patient_with_relations(app, name):
    relationships_to_load = ['samples', 'slides']

    query = return_patient_query(*relationships_to_load)
    result = query.filter_by(name=name).one_or_none()

    if result.samples:
        assert isinstance(result.samples, list)
        assert len(result.samples) > 0
        # Don't need to iterate through every result.
        for sample in result.samples[0:2]:
            assert type(sample.name) is str
    if result.slides:
        assert isinstance(result.slides, list)
        assert len(result.slides) > 0
        # Don't need to iterate through every result.
        for slide in result.slides[0:2]:
            assert type(slide.name) is str
    assert result.name == name
    assert type(result.age_at_diagnosis) is int or NoneType
    assert type(result.ethnicity) in ethnicity_enum.enums or NoneType
    assert type(result.gender) in gender_enum.enums or NoneType
    assert type(result.height) is int or NoneType
    assert type(result.race) in race_enum.enums or NoneType
    assert type(result.weight) is int or NoneType
    assert repr(result) == '<Patient %r>' % name


def test_Patient_no_relations(app, name):
    query = return_patient_query()
    result = query.filter_by(name=name).one_or_none()

    assert result.samples == []
    assert result.slides == []
    assert result.name == name
    assert type(result.age_at_diagnosis) is int or NoneType
    assert type(result.ethnicity) in ethnicity_enum.enums or NoneType
    assert type(result.gender) in gender_enum.enums or NoneType
    assert type(result.height) is int or NoneType
    assert type(result.race) in race_enum.enums or NoneType
    assert type(result.weight) is int or NoneType
