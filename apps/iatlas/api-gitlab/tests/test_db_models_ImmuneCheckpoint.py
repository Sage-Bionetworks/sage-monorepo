import pytest
from tests import app
from flaskr.database import return_immune_checkpoint_query
from flaskr.db_models import ImmuneCheckpoint


def test_ImmuneCheckpoint(app):
    app()
    name = 'Stimulatory'

    query = return_immune_checkpoint_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<ImmuneCheckpoint %r>' % name

    query = return_immune_checkpoint_query('name')
    result = query.filter_by(name=name).first()

    assert result.name == name
