import pytest
from tests import app, NoneType
from flaskr.database import return_gene_query
from flaskr.db_models import Gene


def test_Gene_with_relations(app):
    app()
    entrez = 3627
    hgnc = 'CXCL10'

    query = return_gene_query('gene_family',
                              'gene_function',
                              'immune_checkpoint',
                              'node_type',
                              'pathway',
                              'super_category',
                              'therapy_type')
    result = query.filter_by(entrez=entrez).first()

    if type(result.gene_family) is not NoneType:
        assert result.gene_family.id == result.gene_family_id
    if type(result.gene_function) is not NoneType:
        assert result.gene_function.id == result.gene_function_id
    if type(result.immune_checkpoint) is not NoneType:
        assert result.immune_checkpoint.id == result.immune_checkpoint_id
    if type(result.node_type) is not NoneType:
        assert result.node_type.id == result.node_type_id
    if type(result.pathway) is not NoneType:
        assert result.pathway.id == result.pathway_id
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

    if type(result.gene_family) is not NoneType:
        assert result.gene_family.id == result.gene_family_id
    if type(result.gene_function) is not NoneType:
        assert result.gene_function.id == result.gene_function_id
    if type(result.gene_types) is not NoneType:
        assert isinstance(result.gene_types, list)
        for gene_type in result.gene_types:
            assert type(gene_type.name) is str
    if type(result.immune_checkpoint) is not NoneType:
        assert result.immune_checkpoint.id == result.immune_checkpoint_id
    if type(result.node_type) is not NoneType:
        assert result.node_type.id == result.node_type_id
    if type(result.pathway) is not NoneType:
        assert result.pathway.id == result.pathway_id
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
