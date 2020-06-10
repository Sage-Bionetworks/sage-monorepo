import pytest
from tests import app
from flaskr.database import return_gene_function_query


def test_GeneFunction(app):
    app()
    name = 'Granzyme'

    query = return_gene_function_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<GeneFunction %r>' % name
