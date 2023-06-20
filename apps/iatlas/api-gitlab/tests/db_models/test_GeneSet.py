import pytest
from tests import NoneType
from api.database import return_gene_set_query


@pytest.fixture(scope='module')
def gene_set():
    return 'extracellular_network'


@pytest.fixture(scope='module')
def gene_set_1():
    return 'immunomodulator'


def test_gene_set_with_genes(app, gene_set):
    query = return_gene_set_query('genes')
    result = query.filter_by(name=gene_set).one_or_none()

    assert result
    assert isinstance(result.genes, list)
    assert len(result.genes) > 0
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez_id) is int
    assert result.gene_set_assoc == []
    assert result.name == gene_set
    assert type(result.display) is str or NoneType
    assert repr(result) == '<GeneSet %r>' % gene_set


def test_gene_set_with_gene_set_assoc(app, gene_set):
    query = return_gene_set_query('gene_set_assoc')
    result = query.filter_by(name=gene_set).one_or_none()

    assert result
    assert isinstance(result.gene_set_assoc, list)
    assert len(result.gene_set_assoc) > 0
    # Don't need to iterate through every result.
    for gene_set_rel in result.gene_set_assoc[0:2]:
        assert gene_set_rel.gene_set_id == result.id


def test_gene_set_with_publications(app, gene_set_1):
    query = return_gene_set_query('publications')
    result = query.filter_by(name=gene_set_1).one_or_none()

    assert result
    assert isinstance(result.publications, list)
    assert len(result.publications) > 0
    # Don't need to iterate through every result.
    for publication in result.publications[0:2]:
        assert type(publication.title) is str


def test_gene_set_with_publication_gene_gene_set_assoc(app, gene_set_1):
    query = return_gene_set_query('publication_gene_gene_set_assoc')
    result = query.filter_by(name=gene_set_1).one_or_none()

    assert result
    assert isinstance(result.publication_gene_gene_set_assoc, list)
    assert len(result.publication_gene_gene_set_assoc) > 0
    # Don't need to iterate through every result.
    for publication_gene_gene_set_rel in result.publication_gene_gene_set_assoc[0:2]:
        assert publication_gene_gene_set_rel.gene_set_id == result.id


def test_gene_set_no_relations(app, gene_set):
    query = return_gene_set_query()
    result = query.filter_by(name=gene_set).one_or_none()

    assert result
    assert result.gene_set_assoc == []
    assert result.genes == []
    assert result.publications == []
    assert result.publication_gene_gene_set_assoc == []
    assert type(result.id) is str
    assert result.name == gene_set
    assert type(result.display) is str or NoneType
