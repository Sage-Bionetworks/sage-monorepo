import pytest
from tests import app, NoneType
from flaskr.db_models import CopyNumberResult
from flaskr.enums import direction_enum


def test_CopyNumberResult(app):
    app()
    gene_id = 1
    string_representation_list = []
    separator = ', '

    results = CopyNumberResult.query.filter_by(gene_id=gene_id).all()

    assert isinstance(results, list)
    for result in results:
        copy_number_result_id = result.id
        string_represntation = '<CopyNumberResult %r>' % copy_number_result_id
        string_representation_list.append(string_represntation)
        assert result.gene_id == gene_id
        assert type(result.feature_id) is int
        assert type(result.tag_id) is int
        assert result.direction in direction_enum.enums
        assert type(result.mean_normal) is float or NoneType
        assert type(result.mean_cnv) is float or NoneType
        assert type(result.p_value) is float or NoneType
        assert type(result.log10_p_value) is float or NoneType
        assert type(result.t_stat) is float or NoneType
        assert repr(result) == string_represntation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
