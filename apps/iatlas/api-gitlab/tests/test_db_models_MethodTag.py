import pytest
from tests import app
from flaskr.db_models import MethodTag


def test_MethodTag(app):
    app()
    name = 'ExpSig'
    result = MethodTag.query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<MethodTag %r>' % name
