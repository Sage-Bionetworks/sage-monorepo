import pytest
from sqlalchemy import and_
from tests import NoneType
from api.database import return_copy_number_result_query
from api.enums import direction_enum


@pytest.fixture(scope='module')
def cnr_feature():
    return 'EPIC_NK_Cells'


@pytest.fixture(scope='module')
def cnr_tag():
    return 'BLCA'


@pytest.fixture(scope='module')
def feature_id(test_db, cnr_feature):
    from api.db_models import Feature
    (id, ) = test_db.session.query(Feature.id).filter_by(
        name=cnr_feature).one_or_none()
    return id


@pytest.fixture(scope='module')
def cnr_tag_id(test_db, cnr_tag):
    from api.db_models import Tag
    (id, ) = test_db.session.query(Tag.id).filter_by(
        name=cnr_tag).one_or_none()
    return id


def test_CopyNumberResult_with_relations(app, data_set_id, entrez, gene_id, cnr_tag, cnr_tag_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['data_set', 'feature', 'gene', 'tag']

    query = return_copy_number_result_query(*relationships_to_join)
    results = query.filter_by(dataset_id=data_set_id).filter_by(
        gene_id=gene_id).filter_by(tag_id=cnr_tag_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        copy_number_result_id = result.id
        string_representation = '<CopyNumberResult %r>' % copy_number_result_id
        string_representation_list.append(string_representation)
        assert result.feature.id == result.feature_id
        assert result.data_set.id == data_set_id
        assert result.gene.entrez == entrez
        assert result.gene.id == gene_id
        assert result.tag.id == result.tag_id
        assert result.tag.name == cnr_tag
        assert result.gene_id == gene_id
        assert result.dataset_id == data_set_id
        assert type(result.feature_id) is int
        assert result.direction in direction_enum.enums
        assert type(result.mean_normal) is float or NoneType
        assert type(result.mean_cnv) is float or NoneType
        assert type(result.p_value) is float or NoneType
        assert type(result.log10_p_value) is float or NoneType
        assert type(result.t_stat) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_CopyNumberResult_no_relations(app, data_set_id, gene_id, cnr_tag_id, cnr_tag):
    query = return_copy_number_result_query()
    results = query.filter_by(dataset_id=data_set_id).filter_by(
        gene_id=gene_id).filter_by(tag_id=cnr_tag_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.data_set) is NoneType
        assert type(result.feature) is NoneType
        assert type(result.gene) is NoneType
        assert type(result.tag) is NoneType
        assert type(result.dataset_id) is int
        assert type(result.feature_id) is int
        assert result.gene_id == gene_id
        assert type(result.tag_id) is int
        assert result.direction in direction_enum.enums
        assert type(result.mean_normal) is float or NoneType
        assert type(result.mean_cnv) is float or NoneType
        assert type(result.p_value) is float or NoneType
        assert type(result.log10_p_value) is float or NoneType
        assert type(result.t_stat) is float or NoneType
