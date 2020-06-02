import pytest
from tests import app
from flaskr.db_models import ImmuneCheckpoint


def test_ImmuneCheckpoint(app):
    app()
    name = 'Stimulatory'
    result = ImmuneCheckpoint.query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<ImmuneCheckpoint %r>' % name
