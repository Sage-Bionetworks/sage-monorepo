import pytest
from tests import app, NoneType
from flaskr.database import return_sample_query


def test_Sample_with_relations(app):
    app()
    name = 'DO1328'
    relationships_to_join = ['features', 'mutations', 'tags']

    query = return_sample_query(*relationships_to_join)
    result = query.filter_by(name=name).first()

    if type(result.features) is not NoneType:
        assert isinstance(result.features, list)
        for feature in result.features:
            assert type(feature.name) is str
    if type(result.mutations) is not NoneType:
        assert isinstance(result.mutations, list)
        for mutation in result.mutations:
            assert type(mutation.id) is int
    if type(result.tags) is not NoneType:
        assert isinstance(result.tags, list)
        for tag in result.tags:
            assert type(tag.name) is str
    assert result.name == name
    assert type(result.patient_id) is int or NoneType
    assert repr(result) == '<Sample %r>' % name


def test_Sample_no_relations(app):
    app()
    name = 'DO1328'
    fields_to_return = ['id', 'name', 'patient_id']

    query = return_sample_query(*fields_to_return)
    result = query.filter_by(name=name).first()

    assert type(result.id) is int
    assert result.name == name
    assert type(result.patient_id) is int or NoneType
