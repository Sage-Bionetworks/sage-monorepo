import pytest
from tests import app
from flaskr.db_models import GeneFamily


def test_GeneFamily(app):
    app()
    name = 'Butyrophilins'
    result = GeneFamily.query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<GeneFamily %r>' % name
