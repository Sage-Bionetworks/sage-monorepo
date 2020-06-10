import pytest
from tests import app
from flaskr.database import return_method_tag_query


def test_MethodTag(app):
    app()
    name = 'ExpSig'

    query = return_method_tag_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<MethodTag %r>' % name
