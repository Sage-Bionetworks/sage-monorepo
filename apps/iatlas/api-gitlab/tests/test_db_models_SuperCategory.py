import pytest
from tests import app
from flaskr.db_models import SuperCategory


def test_SuperCategory(app):
    app()
    name = 'Receptor'
    result = SuperCategory.query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<SuperCategory %r>' % name
