import pytest
from tests import NoneType
from api.database import return_rare_variant_pathway_associations_query


@pytest.fixture(scope='module')
def rvap_pathway(test_db):
    return 'MMR'


@pytest.fixture(scope='module')
def rvap_feature(test_db):
    return 'BCR_Richness'


@pytest.fixture(scope='module')
def rvap_feature_id(test_db, rvap_feature):
    from api.db_models import Feature
    (id, ) = test_db.session.query(Feature.id).filter_by(
        name=rvap_feature).one_or_none()
    return id


def test_RareVariantPathwayAssociation_with_relations(app, data_set, data_set_id, rvap_feature, rvap_feature_id, rvap_pathway):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['data_set', 'feature']

    query = return_rare_variant_pathway_associations_query(
        *relationships_to_join)
    results = query.filter_by(dataset_id=data_set_id).filter_by(
        feature_id=rvap_feature_id).filter_by(pathway=rvap_pathway).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:2]:
        rare_variant_pathway_association_id = result.id
        string_representation = '<RareVariantPathwayAssociation %r>' % rare_variant_pathway_association_id
        string_representation_list.append(string_representation)
        assert result.data_set.id == data_set_id
        assert result.data_set.name == data_set
        assert result.feature.id == rvap_feature_id
        assert result.feature.name == rvap_feature
        assert type(result.pathway) is str
        assert type(result.p_value) is float or NoneType
        assert type(result.min) is float or NoneType
        assert type(result.max) is float or NoneType
        assert type(result.mean) is float or NoneType
        assert type(result.q1) is float or NoneType
        assert type(result.q2) is float or NoneType
        assert type(result.q3) is float or NoneType
        assert type(result.n_mutants) is int or NoneType
        assert type(result.n_total) is int or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + \
        separator.join(string_representation_list) + ']'


def test_RareVariantPathwayAssociation_no_relations(app, data_set_id, rvap_feature_id, rvap_gene_id, rvap_tag_id, rvap_pathway):
    query = return_rare_variant_pathway_associations_query()
    results = query.filter_by(dataset_id=data_set_id).filter_by(
        feature_id=rvap_feature_id).filter_by(pathway=rvap_pathway).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert type(result.data_set) is NoneType
        assert type(result.feature) is NoneType
        assert result.dataset_id == data_set_id
        assert result.feature_id == rvap_feature_id
        assert type(result.pathway) is str
        assert type(result.p_value) is float or NoneType
        assert type(result.min) is float or NoneType
        assert type(result.max) is float or NoneType
        assert type(result.mean) is float or NoneType
        assert type(result.q1) is float or NoneType
        assert type(result.q2) is float or NoneType
        assert type(result.q3) is float or NoneType
        assert type(result.n_mutants) is int or NoneType
        assert type(result.n_total) is int or NoneType
