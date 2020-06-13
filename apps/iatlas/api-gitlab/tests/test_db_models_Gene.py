import pytest
from tests import app, NoneType
from flaskr.database import return_gene_query
from flaskr.db_models import Gene


def test_Gene_with_relations(app):
    app()
    entrez = 3627
    hgnc = 'CXCL10'
    relationships_to_join = ['gene_family',
                             'gene_function',
                             'gene_type_assoc',
                             'gene_types',
                             'immune_checkpoint',
                             'node_type',
                             'pathway',
                             'samples',
                             'super_category',
                             'therapy_type']

    query = return_gene_query(['copy_number_results'])
    result = query.filter_by(entrez=entrez).first()

    if type(result.copy_number_results) is not NoneType:
        assert isinstance(result.copy_number_results, list)
        # Don't need to iterate through every result.
        for copy_number_result in result.copy_number_results[0:2]:
            assert copy_number_result.gene_id == result.id

    query = return_gene_query(['driver_results'])
    result = query.filter_by(entrez=entrez).first()

    if type(result.driver_results) is not NoneType:
        assert isinstance(result.driver_results, list)
        # Don't need to iterate through every result.
        for driver_result in result.driver_results[0:2]:
            assert driver_result.gene_id == result.id

    query = return_gene_query(*relationships_to_join)
    result = query.filter_by(entrez=entrez).first()

    if type(result.gene_family) is not NoneType:
        assert result.gene_family.id == result.gene_family_id
    if type(result.gene_function) is not NoneType:
        assert result.gene_function.id == result.gene_function_id
    if type(result.gene_type_assoc) is not NoneType:
        assert isinstance(result.gene_type_assoc, list)
        # Don't need to iterate through every result.
        for gene_type_rel in result.gene_type_assoc[0:2]:
            assert gene_type_rel.gene_id == result.id
    if type(result.gene_types) is not NoneType:
        assert isinstance(result.gene_types, list)
        # Don't need to iterate through every result.
        for gene_type in result.gene_types[0:2]:
            assert type(gene_type.name) is str
    if type(result.immune_checkpoint) is not NoneType:
        assert result.immune_checkpoint.id == result.immune_checkpoint_id
    if type(result.node_type) is not NoneType:
        assert result.node_type.id == result.node_type_id
    if type(result.pathway) is not NoneType:
        assert result.pathway.id == result.pathway_id
    if type(result.samples) is not NoneType:
        assert isinstance(result.samples, list)
        for sample in result.samples:
            assert type(sample.name) is str
    if type(result.super_category) is not NoneType:
        assert result.super_category.id == result.super_cat_id
    if type(result.therapy_type) is not NoneType:
        assert result.therapy_type.id == result.therapy_type_id
    assert result.entrez == entrez
    assert result.hgnc == hgnc
    assert type(result.description) is str or NoneType
    assert type(result.gene_family_id) is int or NoneType
    assert type(result.gene_function_id) is int or NoneType
    assert type(result.immune_checkpoint_id) is int or NoneType
    assert type(result.io_landscape_name) is str or NoneType
    assert isinstance(result.references, list) or NoneType
    assert type(result.node_type_id) is int or NoneType
    assert type(result.pathway_id) is int or NoneType
    assert type(result.super_cat_id) is int or NoneType
    assert type(result.therapy_type_id) is int or NoneType
    assert repr(result) == '<Gene %r>' % entrez


def test_Gene_no_relations(app):
    app()
    entrez = 3627
    hgnc = 'CXCL10'

    query = return_gene_query()
    result = query.filter_by(entrez=entrez).first()

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
    assert isinstance(result.references, list) or NoneType
    assert type(result.node_type_id) is int or NoneType
    assert type(result.pathway_id) is int or NoneType
    assert type(result.super_cat_id) is int or NoneType
    assert type(result.therapy_type_id) is int or NoneType
