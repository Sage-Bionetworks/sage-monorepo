import pytest
from tests import app
from flaskr.db_models import GeneFunction


def test_GeneFunction(app):
    app()
    name = 'Granzyme'
    result = GeneFunction.query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<GeneFunction %r>' % name
