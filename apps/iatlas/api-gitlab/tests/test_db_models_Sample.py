import pytest
from tests import app, NoneType
from flaskr.database import return_sample_query


def test_Sample(app):
    app()
    name = 'DO1328'

    query = return_sample_query('mutations', 'tags')
    result = query.filter_by(name=name).first()

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
