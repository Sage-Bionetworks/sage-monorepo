import pytest
from tests import NoneType
from flaskr.database import return_gene_query
from flaskr.db_models import Gene


def test_Gene_with_relations(app, entrez, hgnc):
    relationships_to_join = ['gene_family',
                             'gene_function',
                             'gene_types',
                             'immune_checkpoint',
                             'node_type',
                             'pathway',
                             'publications',
                             'samples',
                             'super_category',
                             'therapy_type']

    query = return_gene_query(*relationships_to_join)
    result = query.filter_by(entrez=entrez).first()

    if result.gene_family:
        assert result.gene_family.id == result.gene_family_id
    if result.gene_function:
        assert result.gene_function.id == result.gene_function_id
    assert result.gene_type_assoc == []
    if result.gene_types:
        assert isinstance(result.gene_types, list)
        # Don't need to iterate through every result.
        for gene_type in result.gene_types[0:2]:
            assert type(gene_type.name) is str
    if result.immune_checkpoint:
        assert result.immune_checkpoint.id == result.immune_checkpoint_id
    if result.node_type:
        assert result.node_type.id == result.node_type_id
    if result.pathway:
        assert result.pathway.id == result.pathway_id
    if result.publications:
        assert isinstance(result.publications, list)
        # Don't need to iterate through every result.
        for publication in result.publications[0:2]:
            assert type(publication.pubmed_id) is int
    if result.samples:
        assert isinstance(result.samples, list)
        for sample in result.samples:
            assert type(sample.name) is str
    if result.super_category:
        assert result.super_category.id == result.super_cat_id
    if result.therapy_type:
        assert result.therapy_type.id == result.therapy_type_id
    assert result.entrez == entrez
    assert result.hgnc == hgnc
    assert type(result.description) is str or NoneType
    assert type(result.gene_family_id) is int or NoneType
    assert type(result.gene_function_id) is int or NoneType
    assert type(result.immune_checkpoint_id) is int or NoneType
    assert type(result.io_landscape_name) is str or NoneType
    assert type(result.node_type_id) is int or NoneType
    assert type(result.pathway_id) is int or NoneType
    assert type(result.super_cat_id) is int or NoneType
    assert type(result.therapy_type_id) is int or NoneType
    assert repr(result) == '<Gene %r>' % entrez


def test_Gene_with_copy_number_results(app, entrez):
    query = return_gene_query('copy_number_results')
    result = query.filter_by(entrez=entrez).first()

    if result.copy_number_results:
        assert isinstance(result.copy_number_results, list)
        # Don't need to iterate through every result.
        for copy_number_result in result.copy_number_results[0:2]:
            assert copy_number_result.gene_id == result.id


def test_Gene_with_driver_results(app, entrez):
    query = return_gene_query('driver_results')
    result = query.filter_by(entrez=entrez).first()

    if result.driver_results:
        assert isinstance(result.driver_results, list)
        # Don't need to iterate through every result.
        for driver_result in result.driver_results[0:2]:
            assert driver_result.gene_id == result.id


def test_Gene_with_gene_sample_assoc(app, entrez):
    query = return_gene_query('gene_sample_assoc')
    result = query.filter_by(entrez=entrez).first()

    if result.gene_sample_assoc:
        assert isinstance(result.gene_sample_assoc, list)
        # Don't need to iterate through every result.
        for gene_sample_rel in result.gene_sample_assoc[0:2]:
            assert gene_sample_rel.gene_id == result.id


def test_Gene_with_gene_type_assoc(app, entrez):
    query = return_gene_query('gene_type_assoc')
    result = query.filter_by(entrez=entrez).first()

    if result.gene_type_assoc:
        assert isinstance(result.gene_type_assoc, list)
        # Don't need to iterate through every result.
        for gene_type_rel in result.gene_type_assoc[0:2]:
            assert gene_type_rel.gene_id == result.id


def test_Gene_with_publication_gene_assoc(app, entrez):
    query = return_gene_query('publication_gene_assoc')
    result = query.filter_by(entrez=entrez).first()

    if result.publication_gene_assoc:
        assert isinstance(result.publication_gene_assoc, list)
        # Don't need to iterate through every result.
        for publication_gene_rel in result.publication_gene_assoc[0:2]:
            assert publication_gene_rel.gene_id == result.id


def test_Gene_no_relations(app, entrez, hgnc):
    query = return_gene_query()
    result = query.filter_by(entrez=entrez).first()

    assert result.copy_number_results == []
    assert result.driver_results == []
    assert result.gene_sample_assoc == []
    assert type(result.gene_family) is NoneType
    assert type(result.gene_function) is NoneType
    assert result.gene_types == []
    assert type(result.immune_checkpoint) is NoneType
    assert type(result.node_type) is NoneType
    assert type(result.pathway) is NoneType
    assert result.samples == []
    assert type(result.super_category) is NoneType
    assert type(result.therapy_type) is NoneType
    assert result.entrez == entrez
    assert result.hgnc == hgnc
    assert type(result.description) is str or NoneType
    assert type(result.gene_family_id) is int or NoneType
    assert type(result.gene_function_id) is int or NoneType
    assert type(result.immune_checkpoint_id) is int or NoneType
    assert type(result.io_landscape_name) is str or NoneType
    assert type(result.node_type_id) is int or NoneType
    assert type(result.pathway_id) is int or NoneType
    assert type(result.super_cat_id) is int or NoneType
    assert type(result.therapy_type_id) is int or NoneType
