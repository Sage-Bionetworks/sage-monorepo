import pytest
from tests import NoneType
from flaskr.database import return_copy_number_result_query
from flaskr.enums import direction_enum


@pytest.fixture(scope='module')
def gene_id():
    return 1


def test_CopyNumberResult_with_relations(app, gene_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['feature', 'gene', 'tag']

    query = return_copy_number_result_query(*relationships_to_join)
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        copy_number_result_id = result.id
        string_representation = '<CopyNumberResult %r>' % copy_number_result_id
        string_representation_list.append(string_representation)
        if result.feature:
            assert result.feature.id == result.feature_id
        if result.gene:
            assert result.gene.id == gene_id
        if result.tag:
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


def test_CopyNumberResult_no_relations(app, gene_id):
    query = return_copy_number_result_query()
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.feature) is NoneType
        assert type(result.gene) is NoneType
        assert type(result.tag) is NoneType
        assert result.gene_id == gene_id
        assert type(result.feature_id) is int
        assert type(result.tag_id) is int
        assert result.direction in direction_enum.enums
        assert type(result.mean_normal) is float or NoneType
        assert type(result.mean_cnv) is float or NoneType
        assert type(result.p_value) is float or NoneType
        assert type(result.log10_p_value) is float or NoneType
        assert type(result.t_stat) is float or NoneType
