import pytest
from tests import app
from flaskr.db_models import MutationCode


def test_MutationCode(app):
    app()
    code = 'A146'
    result = MutationCode.query.filter_by(code=code).first()

    assert result.code == code
    assert repr(result) == '<MutationCode %r>' % code
