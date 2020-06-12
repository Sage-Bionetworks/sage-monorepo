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
                             'immune_checkpoint',
                             'node_type',
                             'pathway',
                             'super_category',
                             'therapy_type']

    query = return_gene_query(*relationships_to_join)
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
    fields_to_return = ['entrez',
                        'hgnc',
                        'description',
                        'friendly_name',
                        'io_landscape_name',
                        'references',
                        'gene_family_id',
                        'gene_function_id',
                        'immune_checkpoint_id',
                        'node_type_id',
                        'pathway_id',
                        'super_cat_id',
                        'therapy_type_id']

    query = return_gene_query(*fields_to_return)
    result = query.filter_by(entrez=entrez).first()

    assert not hasattr(result, 'gene_family')
    assert not hasattr(result, 'gene_function')
    assert not hasattr(result, 'gene_types')
    assert not hasattr(result, 'immune_checkpoint')
    assert not hasattr(result, 'node_type')
    assert not hasattr(result, 'pathway')
    assert not hasattr(result, 'super_category')
    assert not hasattr(result, 'therapy_type')
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
