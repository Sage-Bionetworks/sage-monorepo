import pytest
from tests import NoneType
from api.database import return_publication_query


@pytest.fixture(scope='module')
def pubmed_id():
    return 19567593


def test_publication_with_relations(app, pubmed_id):
    query = return_publication_query('genes')
    result = query.filter_by(pubmed_id=pubmed_id).first()

    if result.genes:
        assert isinstance(result.genes, list)
        # Don't need to iterate through every result.
        for gene in result.genes[0:2]:
            assert type(gene.entrez) is int
    assert result.publication_gene_assoc == []
    assert result.pubmed_id == pubmed_id
    assert type(result.first_author_last_name) is str or NoneType
    assert type(result.journal) is str or NoneType
    assert type(result.title) is str or NoneType
    assert type(result.year) is str or NoneType
    assert repr(result) == '<Publication %r>' % pubmed_id


def test_publication_with_publication_gene_assoc(app, pubmed_id):
    query = return_publication_query('publication_gene_assoc')
    result = query.filter_by(pubmed_id=pubmed_id).first()

    if result.publication_gene_assoc:
        assert isinstance(result.publication_gene_assoc, list)
        # Don't need to iterate through every result.
        for publication_gene_rel in result.publication_gene_assoc[0:2]:
            assert publication_gene_rel.publication_id == result.id


def test_publication_no_relations(app, pubmed_id):
    query = return_publication_query()
    result = query.filter_by(pubmed_id=pubmed_id).first()

    assert result.genes == []
    assert result.publication_gene_assoc == []
    assert result.pubmed_id == pubmed_id
    assert type(result.first_author_last_name) is str or NoneType
    assert type(result.journal) is str or NoneType
    assert type(result.title) is str or NoneType
    assert type(result.year) is str or NoneType
