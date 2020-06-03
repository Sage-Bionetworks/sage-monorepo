import pytest
from tests import app, NoneType
from flaskr.db_models import Tag


def test_Tag(app):
    app()
    name = 'ACC'
    result = Tag.query.filter_by(name=name).first()

    assert result.name == name
    assert type(result.characteristics) is str
    assert type(result.display) is str or NoneType
    assert type(result.color) is str or NoneType
    assert repr(result) == '<Tag %r>' % name
