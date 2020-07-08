import pytest
from api.database import return_super_category_query


@pytest.fixture(scope='module')
def name():
    return 'Receptor'


def test_SuperCategory_with_relations(app, name):
    query = return_super_category_query('genes')
    result = query.filter_by(name=name).first()

    assert isinstance(result.genes, list)
    assert len(result.genes) > 0
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.name == name
    assert repr(result) == '<SuperCategory %r>' % name


def test_SuperCategory_no_relations(app, name):
    query = return_super_category_query()
    result = query.filter_by(name=name).first()

    assert result.genes == []
    assert result.name == name
