import pytest
from tests import NoneType
from api.database import return_publication_query


@pytest.fixture(scope='module')
def publication():
    return '10.1016/j.immuni.2013.07.012_23890059'


@pytest.fixture(scope='module')
def publication_with_tags():
    return '10.1016/j.cell.2015.10.025_NA'


def test_publication_with_genes(app, publication):
    query = return_publication_query('genes')
    result = query.filter_by(name=publication).one_or_none()

    assert result
    assert isinstance(result.genes, list)
    assert len(result.genes) > 0
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.publication_gene_gene_type_assoc == []
    assert result.name == publication
    assert type(result.do_id) is str or NoneType
    assert type(result.first_author_last_name) is str or NoneType
    assert type(result.journal) is str or NoneType
    assert type(result.pubmed_id) is int or NoneType
    assert type(result.title) is str or NoneType
    assert type(result.year) is int or NoneType
    assert repr(result) == '<Publication %r>' % publication


def test_gene_type_with_gene_types(app, publication):
    query = return_publication_query('gene_types')
    result = query.filter_by(name=publication).one_or_none()

    assert result
    assert isinstance(result.gene_types, list)
    assert len(result.gene_types) > 0
    # Don't need to iterate through every result.
    for gene_type in result.gene_types[0:2]:
        assert type(gene_type.name) is str


def test_publication_with_publication_gene_gene_type_assoc(app, publication):
    query = return_publication_query('publication_gene_gene_type_assoc')
    result = query.filter_by(name=publication).one_or_none()

    assert result
    assert isinstance(result.publication_gene_gene_type_assoc, list)
    assert len(result.publication_gene_gene_type_assoc) > 0
    # Don't need to iterate through every result.
    for publication_gene_gene_type_rel in result.publication_gene_gene_type_assoc[0:2]:
        assert publication_gene_gene_type_rel.publication_id == result.id


def test_publication_with_tag_publication_assoc(app, publication_with_tags):
    query = return_publication_query('tag_publication_assoc')
    result = query.filter_by(name=publication_with_tags).one_or_none()

    assert result
    assert isinstance(result.tag_publication_assoc, list)
    assert len(result.tag_publication_assoc) > 0
    # Don't need to iterate through every result.
    for tag_publication_rel in result.tag_publication_assoc[0:2]:
        assert tag_publication_rel.publication_id == result.id


def test_publication_with_tags(app, publication_with_tags):
    query = return_publication_query('tags')
    result = query.filter_by(name=publication_with_tags).one_or_none()

    assert result
    assert isinstance(result.tags, list)
    assert len(result.tags) > 0
    # Don't need to iterate through every result.
    for tag in result.tags[0:5]:
        assert type(tag.name) is str
    assert result.genes == []
    assert result.gene_types == []
    assert result.publication_gene_gene_type_assoc == []
    assert result.name == publication_with_tags
    assert type(result.do_id) is str or NoneType
    assert type(result.first_author_last_name) is str or NoneType
    assert type(result.journal) is str or NoneType
    assert type(result.pubmed_id) is int or NoneType
    assert type(result.title) is str or NoneType
    assert type(result.year) is int or NoneType
    assert repr(result) == '<Publication %r>' % publication_with_tags


def test_publication_no_relations(app, publication):
    query = return_publication_query()
    result = query.filter_by(name=publication).one_or_none()

    assert result
    assert result.genes == []
    assert result.publication_gene_gene_type_assoc == []
    assert result.name == publication
    assert type(result.do_id) is str or NoneType
    assert type(result.first_author_last_name) is str or NoneType
    assert type(result.journal) is str or NoneType
    assert type(result.pubmed_id) is int or NoneType
    assert type(result.title) is str or NoneType
    assert type(result.year) is int or NoneType
