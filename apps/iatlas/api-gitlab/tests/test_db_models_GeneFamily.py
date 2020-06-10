import pytest
from tests import app
from flaskr.database import return_gene_family_query


def test_GeneFamily(app):
    app()
    name = 'Butyrophilins'

    query = return_gene_family_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert repr(result) == '<GeneFamily %r>' % name
