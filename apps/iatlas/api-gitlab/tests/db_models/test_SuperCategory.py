import pytest
from api.database import return_super_category_query


@pytest.fixture(scope='module')
def super_category():
    return 'Receptor'


def test_SuperCategory_with_relations(app, super_category):
    query = return_super_category_query('genes')
    result = query.filter_by(name=super_category).one_or_none()

    assert result
    assert isinstance(result.genes, list)
    assert len(result.genes) > 0
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.name == super_category
    assert repr(result) == '<SuperCategory %r>' % super_category


def test_SuperCategory_no_relations(app, super_category):
    query = return_super_category_query()
    result = query.filter_by(name=super_category).one_or_none()

    assert result
    assert result.genes == []
    assert result.name == super_category
