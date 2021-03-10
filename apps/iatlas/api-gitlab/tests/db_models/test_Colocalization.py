import pytest
from tests import NoneType
from decimal import Decimal
from api.database import return_colocalization_query


@pytest.fixture(scope='module')
def coloc_feature(test_db):
    return 'Bindea_CD8_T_cells'


@pytest.fixture(scope='module')
def coloc_feature_id(test_db, coloc_feature):
    from api.db_models import Feature
    (id, ) = test_db.session.query(Feature.id).filter_by(
        name=coloc_feature).one_or_none()
    return id


@pytest.fixture(scope='module')
def coloc_gene_entrez(test_db):
    return 23779


@pytest.fixture(scope='module')
def coloc_gene_id(test_db, coloc_gene_entrez):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(
        entrez=coloc_gene_entrez).one_or_none()
    return id


@pytest.fixture(scope='module')
def coloc_snp_name(test_db):
    return "22:45019343:A:G"


@pytest.fixture(scope='module')
def coloc_snp_id(test_db, coloc_snp_name):
    from api.db_models import Snp
    (id, ) = test_db.session.query(Snp.id).filter_by(
        name=coloc_snp_name).one_or_none()
    return id


@pytest.fixture(scope='module')
def coloc_qtl_type(test_db):
    return "sQTL"


@pytest.fixture(scope='module')
def coloc_ecaviar_pp(test_db):
    return "C2"


@pytest.fixture(scope='module')
def coloc_plot_type(test_db):
    return "3 Level Plot"


def test_Colocalization_with_relations(app, data_set, data_set_id, coloc_feature, coloc_feature_id, coloc_gene_entrez, coloc_gene_id, coloc_snp_name, coloc_snp_id, coloc_qtl_type, coloc_ecaviar_pp, coloc_plot_type):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['data_set', 'feature', 'snp', 'gene']

    query = return_colocalization_query(*relationships_to_join)
    results = query.filter_by(dataset_id=data_set_id).filter_by(coloc_dataset_id=data_set_id).filter_by(feature_id=coloc_feature_id).filter_by(
        snp_id=coloc_snp_id).filter_by(gene_id=coloc_gene_id).filter_by(qtl_type=coloc_qtl_type).filter_by(ecaviar_pp=coloc_ecaviar_pp).filter_by(plot_type=coloc_plot_type).limit(3).all()

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        colocalization_id = result.id
        string_representation = '<Colocalization %r>' % colocalization_id
        string_representation_list.append(string_representation)
        assert result.data_set.id == data_set_id
        assert result.data_set.name == data_set
        assert result.feature.id == coloc_feature_id
        assert result.feature.name == coloc_feature
        assert result.snp.id == coloc_snp_id
        assert result.snp.name == coloc_snp_name
        assert result.gene.id == coloc_gene_id
        assert result.gene.entrez == coloc_gene_entrez
        assert result.qtl_type == coloc_qtl_type
        assert result.ecaviar_pp == coloc_ecaviar_pp
        assert result.plot_type == coloc_plot_type
        assert type(result.splice_loc) is str or NoneType
        assert type(result.splice_loc) is str
        assert type(result.plot_link) is str
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_Colocalization_no_relations(app, data_set_id, coloc_feature_id, coloc_gene_id, coloc_snp_id):
    query = return_colocalization_query()
    results = query.filter_by(dataset_id=data_set_id).filter_by(coloc_dataset_id=data_set_id).filter_by(feature_id=coloc_feature_id).filter_by(
        snp_id=coloc_snp_id).filter_by(gene_id=coloc_gene_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        colocalization_id = result.id
        string_representation = '<Colocalization %r>' % colocalization_id
        assert type(result.data_set) is NoneType
        assert type(result.feature) is NoneType
        assert type(result.snp) is NoneType
        assert type(result.gene) is NoneType
        assert result.dataset_id == data_set_id
        assert result.feature_id == coloc_feature_id
        assert result.snp_id == coloc_snp_id
        assert result.gene_id == coloc_gene_id
        assert type(result.qtl_type) is str
        assert type(result.ecaviar_pp) is str or NoneType
        assert type(result.plot_type) is str or NoneType
        assert type(result.splice_loc) is str or NoneType
        assert type(result.splice_loc) is str or NoneType
        assert type(result.plot_link) is str or NoneType
        assert repr(result) == string_representation
