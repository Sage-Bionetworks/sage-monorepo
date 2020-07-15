import pytest
from tests import NoneType
from api.database import return_gene_type_query


@pytest.fixture(scope='module')
def gene_type():
    return 'extra_cellular_network'


@pytest.fixture(scope='module')
def gene_type_1():
    return 'immunomodulator'


def test_gene_type_with_genes(app, gene_type):
    query = return_gene_type_query('genes')
    result = query.filter_by(name=gene_type).one_or_none()

    assert result
    assert isinstance(result.genes, list)
    assert len(result.genes) > 0
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.gene_type_assoc == []
    assert result.name == gene_type
    assert type(result.display) is str or NoneType
    assert repr(result) == '<GeneType %r>' % gene_type


def test_gene_type_with_gene_type_assoc(app, gene_type):
    query = return_gene_type_query('gene_type_assoc')
    result = query.filter_by(name=gene_type).one_or_none()

    assert result
    assert isinstance(result.gene_type_assoc, list)
    assert len(result.gene_type_assoc) > 0
    # Don't need to iterate through every result.
    for gene_type_rel in result.gene_type_assoc[0:2]:
        assert gene_type_rel.type_id == result.id


def test_gene_type_with_publications(app, gene_type_1):
    query = return_gene_type_query('publications')
    result = query.filter_by(name=gene_type_1).one_or_none()

    assert result
    assert isinstance(result.publications, list)
    assert len(result.publications) > 0
    # Don't need to iterate through every result.
    for publication in result.publications[0:2]:
        assert type(publication.name) is str


def test_gene_type_with_publication_gene_gene_type_assoc(app, gene_type_1):
    query = return_gene_type_query('publication_gene_gene_type_assoc')
    result = query.filter_by(name=gene_type_1).one_or_none()

    assert result
    assert isinstance(result.publication_gene_gene_type_assoc, list)
    assert len(result.publication_gene_gene_type_assoc) > 0
    # Don't need to iterate through every result.
    for publication_gene_gene_type_rel in result.publication_gene_gene_type_assoc[0:2]:
        assert publication_gene_gene_type_rel.gene_type_id == result.id


def test_gene_type_no_relations(app, gene_type):
    query = return_gene_type_query()
    result = query.filter_by(name=gene_type).one_or_none()

    assert result
    assert result.gene_type_assoc == []
    assert result.genes == []
    assert result.publications == []
    assert result.publication_gene_gene_type_assoc == []
    assert type(result.id) is int
    assert result.name == gene_type
    assert type(result.display) is str or NoneType
