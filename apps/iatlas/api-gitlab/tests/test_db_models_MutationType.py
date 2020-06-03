import pytest
from tests import app, NoneType
from flaskr.db_models import MutationType


def test_MutationType(app):
    app()
    name = 'driver_mutation'
    result = MutationType.query.filter_by(name=name).first()

    assert result.name == name
    assert type(result.display) is str or NoneType
    assert repr(result) == '<MutationType %r>' % name
