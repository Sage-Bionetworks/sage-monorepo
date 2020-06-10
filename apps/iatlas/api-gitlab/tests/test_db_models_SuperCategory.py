import pytest
from tests import app
from flaskr.database import return_super_category_query


def test_SuperCategory(app):
    app()
    name = 'Receptor'

    query = return_super_category_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<SuperCategory %r>' % name
