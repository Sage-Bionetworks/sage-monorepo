import pytest
from tests import app
from flaskr.database import return_pathway_query


def test_Pathway(app):
    app()
    name = 'Antigen'

    query = return_pathway_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<Pathway %r>' % name
