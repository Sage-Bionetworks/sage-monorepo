import pytest
from tests import app, NoneType
from flaskr.database import return_copy_number_result_query
from flaskr.enums import direction_enum


def test_CopyNumberResult_with_relations(app):
    app()
    gene_id = 1
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['feature', 'gene', 'tag']

    query = return_copy_number_result_query(*relationships_to_join)
    results = query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        copy_number_result_id = result.id
        string_representation = '<CopyNumberResult %r>' % copy_number_result_id
        string_representation_list.append(string_representation)
        if type(result.feature) is not NoneType:
            assert result.feature.id == result.feature_id
        if type(result.gene) is not NoneType:
            assert result.gene.id == gene_id
        if type(result.tag) is not NoneType:
            assert result.tag.id == result.tag_id
        assert result.gene_id == gene_id
        assert type(result.feature_id) is int
        assert type(result.tag_id) is int
        assert result.direction in direction_enum.enums
        assert type(result.mean_normal) is float or NoneType
        assert type(result.mean_cnv) is float or NoneType
        assert type(result.p_value) is float or NoneType
        assert type(result.log10_p_value) is float or NoneType
        assert type(result.t_stat) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_CopyNumberResult_no_relations(app):
    app()
    gene_id = 1
    string_representation_list = []
    separator = ', '
    fields_to_return = ['id',
                        'direction',
                        'mean_normal',
                        'mean_cnv',
                        'p_value',
                        'log10_p_value',
                        't_stat',
                        'feature_id',
                        'gene_id',
                        'tag_id']

    query = return_copy_number_result_query(*fields_to_return)
    results = query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        copy_number_result_id = result.id
        string_representation = '<CopyNumberResult %r>' % copy_number_result_id
        string_representation_list.append(string_representation)
        assert result.gene_id == gene_id
        assert type(result.feature_id) is int
        assert type(result.tag_id) is int
        assert result.direction in direction_enum.enums
        assert type(result.mean_normal) is float or NoneType
        assert type(result.mean_cnv) is float or NoneType
        assert type(result.p_value) is float or NoneType
        assert type(result.log10_p_value) is float or NoneType
        assert type(result.t_stat) is float or NoneType
        assert repr(result) == string_representation
