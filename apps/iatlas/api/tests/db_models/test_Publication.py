import pytest
from tests import NoneType
from api.database import return_publication_query


@pytest.fixture(scope="module")
def publication_title_with_tag():
    return "Comprehensive Pan-Genomic Characterization of Adrenocortical Carcinoma."


@pytest.fixture(scope="module")
def publication_title_with_gene():
    return "Immunotherapy: Beyond Anti-PD-1 and Anti-PD-L1 Therapies."


def test_publication_with_genes(app, publication_title_with_gene):
    query = return_publication_query("genes")
    result = query.filter_by(title=publication_title_with_gene).one_or_none()

    assert result
    assert isinstance(result.genes, list)
    assert len(result.genes) > 0
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez_id) is int
    assert result.publication_gene_gene_set_assoc == []
    assert result.title == publication_title_with_gene
    assert type(result.do_id) is str or NoneType
    assert type(result.first_author_last_name) is str or NoneType
    assert type(result.journal) is str or NoneType
    assert type(result.pubmed_id) is int or NoneType
    assert type(result.year) is int or NoneType
    assert repr(result) == "<Publication %r>" % publication_title_with_gene


def test_with_gene_sets(app, publication_title_with_gene):
    query = return_publication_query("gene_sets")
    result = query.filter_by(title=publication_title_with_gene).one_or_none()

    assert result
    assert isinstance(result.gene_sets, list)
    assert len(result.gene_sets) > 0
    # Don't need to iterate through every result.
    for gene_set in result.gene_sets[0:2]:
        assert type(gene_set.name) is str


def test_publication_with_publication_gene_gene_set_assoc(
    app, publication_title_with_gene
):
    query = return_publication_query("publication_gene_gene_set_assoc")
    result = query.filter_by(title=publication_title_with_gene).one_or_none()

    assert result
    assert isinstance(result.publication_gene_gene_set_assoc, list)
    assert len(result.publication_gene_gene_set_assoc) > 0
    # Don't need to iterate through every result.
    for publication_gene_gene_set_rel in result.publication_gene_gene_set_assoc[0:2]:
        assert publication_gene_gene_set_rel.publication_id == result.id


def test_publication_with_tag_publication_assoc(app, publication_title_with_tag):
    query = return_publication_query("tag_publication_assoc")
    result = query.filter_by(title=publication_title_with_tag).one_or_none()

    assert result
    assert isinstance(result.tag_publication_assoc, list)
    assert len(result.tag_publication_assoc) > 0
    # Don't need to iterate through every result.
    for tag_publication_rel in result.tag_publication_assoc[0:2]:
        assert tag_publication_rel.publication_id == result.id


def test_publication_with_tags(app, publication_title_with_tag):
    query = return_publication_query("tags")
    result = query.filter_by(title=publication_title_with_tag).one_or_none()

    assert result
    assert isinstance(result.tags, list)
    assert len(result.tags) > 0
    # Don't need to iterate through every result.
    for tag in result.tags[0:5]:
        assert type(tag.name) is str
    assert result.genes == []
    assert result.gene_sets == []
    assert result.publication_gene_gene_set_assoc == []
    assert result.title == publication_title_with_tag
    assert type(result.do_id) is str or NoneType
    assert type(result.first_author_last_name) is str or NoneType
    assert type(result.journal) is str or NoneType
    assert type(result.pubmed_id) is int or NoneType
    assert type(result.year) is int or NoneType
    assert repr(result) == "<Publication %r>" % publication_title_with_tag


def test_publication_no_relations(app, publication_title_with_tag):
    query = return_publication_query()
    result = query.filter_by(title=publication_title_with_tag).one_or_none()

    assert result
    assert result.genes == []
    assert result.publication_gene_gene_set_assoc == []
    assert result.title == publication_title_with_tag
    assert type(result.do_id) is str or NoneType
    assert type(result.first_author_last_name) is str or NoneType
    assert type(result.journal) is str or NoneType
    assert type(result.pubmed_id) is int or NoneType
    assert type(result.year) is int or NoneType
