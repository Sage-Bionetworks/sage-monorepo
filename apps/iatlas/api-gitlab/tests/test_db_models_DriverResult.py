import pytest
from tests import app, NoneType
from flaskr.database import return_driver_result_query
from flaskr.db_models import DriverResult


def test_DriverResult_with_relations(app):
    app()
    gene_id = 20
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['feature', 'gene', 'mutation_code', 'tag']

    query = return_driver_result_query(*relationships_to_join)
    results = query.filter(DriverResult.gene_id == gene_id).all()

    assert isinstance(results, list)
    for result in results:
        driver_result_id = result.id
        string_representation = '<DriverResult %r>' % driver_result_id
        string_representation_list.append(string_representation)
        if type(result.feature) is not NoneType:
            assert result.feature.id == result.feature_id
        if type(result.gene) is not NoneType:
            assert result.gene.id == gene_id
        if type(result.mutation_code) is not NoneType:
            assert result.mutation_code.id == result.mutation_code_id
        if type(result.tag) is not NoneType:
            assert result.tag.id == result.tag_id
        assert result.gene_id == gene_id
        assert type(result.feature_id) is int or NoneType
        assert type(result.mutation_code_id) is int or NoneType
        assert type(result.tag_id) is int or NoneType
        assert type(result.p_value) is float or NoneType
        assert type(result.fold_change) is float or NoneType
        assert type(result.log10_p_value) is float or NoneType
        assert type(result.log10_fold_change) is float or NoneType
        assert type(result.n_wt) is int or NoneType
        assert type(result.n_wt) is int or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_DriverResult_no_relations(app):
    app()
    gene_id = 20
    string_representation_list = []
    separator = ', '

    query = return_driver_result_query()
    results = query.filter(DriverResult.gene_id == gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.gene_id == gene_id
        assert type(result.feature_id) is int or NoneType
        assert type(result.mutation_code_id) is int or NoneType
        assert type(result.tag_id) is int or NoneType
        assert type(result.p_value) is float or NoneType
        assert type(result.fold_change) is float or NoneType
        assert type(result.log10_p_value) is float or NoneType
        assert type(result.log10_fold_change) is float or NoneType
        assert type(result.n_wt) is int or NoneType
        assert type(result.n_wt) is int or NoneType
