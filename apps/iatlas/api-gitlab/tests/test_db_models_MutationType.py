import pytest
from tests import app, NoneType
from flaskr.database import return_mutation_type_query


def test_MutationType(app):
    app()
    name = 'driver_mutation'

    query = return_mutation_type_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert type(result.display) is str or NoneType
    assert repr(result) == '<MutationType %r>' % name
