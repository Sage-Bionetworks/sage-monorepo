import pytest
from tests import app, NoneType
from flaskr.db_models import DriverResult


def test_DriverResult(app):
    app()
    gene_id = 20
    string_representation_list = []
    separator = ', '

    results = DriverResult.query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        driver_result_id = result.id
        string_representation = '<DriverResult %r>' % driver_result_id
        string_representation_list.append(string_representation)
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
