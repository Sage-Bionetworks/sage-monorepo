import pytest
from tests import app
from flaskr.database import return_node_type_query


def test_NodeType(app):
    app()
    name = 'Ligand'

    query = return_node_type_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<NodeType %r>' % name
