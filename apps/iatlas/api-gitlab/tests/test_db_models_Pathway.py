import pytest
from tests import app
from flaskr.db_models import Pathway


def test_Pathway(app):
    app()
    name = 'Antigen'
    result = Pathway.query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<Pathway %r>' % name
