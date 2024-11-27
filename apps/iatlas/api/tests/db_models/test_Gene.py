import pytest
from tests import NoneType
from api.database import return_gene_query
from api.db_models import Gene


def test_Gene_with_relations(app, entrez_id, hgnc_id):
    relationships_to_join = [
        "gene_family",
        "gene_function",
        "gene_sets",
        "immune_checkpoint",
        "gene_pathway",
        "samples",
        "super_category",
        "therapy_type",
    ]

    query = return_gene_query(*relationships_to_join)
    result = query.filter_by(entrez_id=entrez_id).one_or_none()

    assert result
    # assert result.gene_set_assoc == []
    if result.gene_sets:
        assert isinstance(result.gene_sets, list)
        for gene_set in result.gene_sets[0:2]:
            assert type(gene_set.name) is str
    if result.samples:
        assert isinstance(result.samples, list)
        for sample in result.samples:
            assert type(sample.name) is str
    assert result.entrez_id == entrez_id
    assert result.hgnc_id == hgnc_id
    assert type(result.description) is str or NoneType
    assert type(result.gene_family) is str or NoneType
    assert type(result.gene_function) is str or NoneType
    assert type(result.immune_checkpoint) is str or NoneType
    assert type(result.io_landscape_name) is str or NoneType
    assert type(result.gene_pathway) is str or NoneType
    assert type(result.super_category) is str or NoneType
    assert type(result.therapy_type) is str or NoneType
    assert repr(result) == "<Gene %r>" % entrez_id


def test_Gene_with_publications(app, entrez_id, hgnc_id):
    query = return_gene_query("publications")
    result = query.filter_by(entrez_id=entrez_id).one_or_none()

    assert result
    assert result.gene_set_assoc == []
    assert isinstance(result.publications, list)
    for publication in result.publications[0:2]:
        assert type(publication.pubmed_id) is int
    assert result.entrez_id == entrez_id
    assert result.hgnc_id == hgnc_id
    assert repr(result) == "<Gene %r>" % entrez_id


def test_Gene_with_copy_number_results(app, entrez_id):
    query = return_gene_query("copy_number_results")
    result = query.filter_by(entrez_id=entrez_id).one_or_none()

    assert result
    assert isinstance(result.copy_number_results, list)
    # Don't need to iterate through every result.
    for copy_number_result in result.copy_number_results[0:2]:
        assert copy_number_result.gene_id == result.id


def test_Gene_with_gene_sample_assoc(app, entrez_id):
    query = return_gene_query("gene_sample_assoc")
    result = query.filter_by(entrez_id=entrez_id).one_or_none()

    assert result
    assert isinstance(result.gene_sample_assoc, list)
    # Don't need to iterate through every result.
    for gene_sample_rel in result.gene_sample_assoc[0:2]:
        assert gene_sample_rel.gene_id == result.id


def test_Gene_with_gene_set_assoc(app, entrez_id):
    query = return_gene_query("gene_set_assoc")
    result = query.filter_by(entrez_id=entrez_id).one_or_none()

    assert result
    assert isinstance(result.gene_set_assoc, list)
    # Don't need to iterate through every result.
    for gene_set_rel in result.gene_set_assoc[0:2]:
        assert gene_set_rel.gene_id == result.id


def test_Gene_with_publication_gene_gene_set_assoc(app, entrez_id):
    query = return_gene_query("publication_gene_gene_set_assoc")
    result = query.filter_by(entrez_id=entrez_id).one_or_none()

    assert result
    assert isinstance(result.publication_gene_gene_set_assoc, list)
    # Don't need to iterate through every result.
    for publication_gene_gene_set_rel in result.publication_gene_gene_set_assoc[0:2]:
        assert publication_gene_gene_set_rel.gene_id == result.id


def test_Gene_no_relations(app, entrez_id, hgnc_id):
    query = return_gene_query()
    result = query.filter_by(entrez_id=entrez_id).one_or_none()

    assert result
    assert result.copy_number_results == []
    assert result.gene_sample_assoc == []
    assert result.gene_sets == []
    assert result.samples == []
    assert result.entrez_id == entrez_id
    assert result.hgnc_id == hgnc_id
    assert type(result.description) is str or NoneType
    assert type(result.gene_family) is str or NoneType
    assert type(result.gene_function) is str or NoneType
    assert type(result.immune_checkpoint) is str or NoneType
    assert type(result.io_landscape_name) is str or NoneType
    assert type(result.gene_pathway) is str or NoneType
    assert type(result.super_category) is str or NoneType
    assert type(result.therapy_type) is str or NoneType


def test_Gene_io_target(app):

    query = return_gene_query("gene_pathway", "therapy_type")
    result = query.filter_by(entrez_id=55).one_or_none()

    assert type(result.gene_pathway) is str
    assert result.gene_pathway == "Innate Immune System"

    assert type(result.therapy_type) is str
    assert result.therapy_type == "Targeted by Other Immuno-Oncology Therapy Type"
