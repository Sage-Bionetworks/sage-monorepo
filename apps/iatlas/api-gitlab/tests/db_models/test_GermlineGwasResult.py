import pytest
from tests import NoneType
from api.database import return_germline_gwas_result_query


@pytest.fixture(scope='module')
def ggr_feature(test_db):
    return 'Cell_Proportion_B_Cells_Memory_Binary_MedianLowHigh'


@pytest.fixture(scope='module')
def ggr_feature_id(test_db, ggr_feature):
    from api.db_models import Feature
    (id, ) = test_db.session.query(Feature.id).filter_by(
        name=ggr_feature).one_or_none()
    return id


@pytest.fixture(scope='module')
def ggr_snp(test_db):
    return '7:104003135:C:G'


@pytest.fixture(scope='module')
def ggr_snp_id(test_db, ggr_snp):
    from api.db_models import Snp
    (id, ) = test_db.session.query(Snp.id).filter_by(
        name=ggr_snp).one_or_none()
    return id


def test_GermlineGwasResult_with_relations(app, data_set, data_set_id, ggr_feature, ggr_feature_id, ggr_snp, ggr_snp_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['data_set', 'feature', 'snp']

    query = return_germline_gwas_result_query(*relationships_to_join)
    results = query.filter_by(dataset_id=data_set_id).filter_by(
        feature_id=ggr_feature_id).filter_by(
        snp_id=ggr_snp_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        germline_gwas_result_id = result.id
        string_representation = '<GermlineGwasResult %r>' % germline_gwas_result_id
        string_representation_list.append(string_representation)
        assert result.feature.id == ggr_feature_id
        assert result.feature.name == ggr_feature
        assert result.snp.id == ggr_snp_id
        assert result.snp.name == ggr_snp
        assert result.data_set.id == data_set_id
        assert result.data_set.name == data_set
        assert type(result.p_value) is float or NoneType
        assert type(result.maf) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_GermlineGwasResult_no_relations(app, data_set_id, ggr_feature_id, ggr_snp_id):
    query = return_germline_gwas_result_query()
    results = query.filter_by(dataset_id=data_set_id).filter_by(
        feature_id=ggr_feature_id).filter_by(
        snp_id=ggr_snp_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        germline_gwas_result_id = result.id
        string_representation = '<GermlineGwasResult %r>' % germline_gwas_result_id
        assert type(result.data_set) is NoneType
        assert type(result.feature) is NoneType
        assert type(result.snp) is NoneType
        assert result.dataset_id == data_set_id
        assert result.feature_id == ggr_feature_id
        assert result.snp_id == ggr_snp_id
        assert type(result.p_value) is float or NoneType
        assert type(result.maf) is float or NoneType
        assert repr(result) == string_representation


def test_GermlineGwasResult_no_filters(app):
    query = return_germline_gwas_result_query()
    results = query.limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        germline_gwas_result_id = result.id
        string_representation = '<GermlineGwasResult %r>' % germline_gwas_result_id
        assert type(result.data_set) is NoneType
        assert type(result.feature) is NoneType
        assert type(result.snp) is NoneType
        assert type(result.p_value) is float or NoneType
        assert type(result.maf) is float or NoneType
        assert repr(result) == string_representation
