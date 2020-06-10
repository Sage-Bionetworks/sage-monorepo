import pytest
from tests import app
from flaskr.database import return_mutation_code_query


def test_MutationCode(app):
    app()
    code = 'A146'

    query = return_mutation_code_query()
    result = query.filter_by(code=code).first()

    assert result.code == code
    assert repr(result) == '<MutationCode %r>' % code
