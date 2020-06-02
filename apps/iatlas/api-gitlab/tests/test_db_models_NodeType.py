import pytest
from tests import app
from flaskr.db_models import NodeType


def test_NodeType(app):
    app()
    name = 'Ligand'
    result = NodeType.query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<NodeType %r>' % name
