import pytest
from tests import NoneType
from api.database import return_driver_result_query


@pytest.fixture(scope='module')
def feature_id():
    return 70


@pytest.fixture(scope='module')
def gene_id():
    return 20825


@pytest.fixture(scope='module')
def mutation_code_id():
    return 2


@pytest.fixture(scope='module')
def tag_id():
    return 16


def test_DriverResult_with_relations(app, data_set_id, feature_id, gene_id, tag_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['dataset',
                             'feature', 'gene', 'mutation_code', 'tag']

    query = return_driver_result_query(*relationships_to_join)
    results = query.filter_by(dataset_id=data_set_id).filter_by(
        feature_id=feature_id).filter_by(
        feature_id=gene_id).filter_by(tag_id=tag_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        driver_result_id = result.id
        string_representation = '<DriverResult %r>' % driver_result_id
        string_representation_list.append(string_representation)
        assert result.data_set.id == data_set_id
        assert result.feature.id == feature_id
        assert result.gene.id == gene_id
        assert result.mutation_code.id == result.mutation_code_id
        assert result.tag.id == tag_id
        assert result.dataset_id == data_set_id
        assert result.feature_id == feature_id
        assert result.gene_id == gene_id
        assert type(result.mutation_code_id) is int or NoneType
        assert result.tag_id == tag_id
        assert type(result.p_value) is float or NoneType
        assert type(result.fold_change) is float or NoneType
        assert type(result.log10_p_value) is float or NoneType
        assert type(result.log10_fold_change) is float or NoneType
        assert type(result.n_mut) is int or NoneType
        assert type(result.n_wt) is int or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_DriverResult_no_relations(app, data_set_id, feature_id, gene_id, tag_id):
    query = return_driver_result_query()
    results = query.filter_by(dataset_id=data_set_id).filter_by(
        feature_id=feature_id).filter_by(
        feature_id=gene_id).filter_by(tag_id=tag_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.data_set) is NoneType
        assert type(result.feature) is NoneType
        assert type(result.gene) is NoneType
        assert type(result.mutation_code) is NoneType
        assert type(result.tag) is NoneType
        assert result.dataset_id == data_set_id
        assert result.feature_id == feature_id
        assert result.gene_id == gene_id
        assert type(result.mutation_code_id) is int or NoneType
        assert result.tag_id == tag_id
        assert type(result.p_value) is float or NoneType
        assert type(result.fold_change) is float or NoneType
        assert type(result.log10_p_value) is float or NoneType
        assert type(result.log10_fold_change) is float or NoneType
        assert type(result.n_mut) is int or NoneType
        assert type(result.n_wt) is int or NoneType
