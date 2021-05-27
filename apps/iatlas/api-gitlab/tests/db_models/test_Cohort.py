import pytest
from tests import NoneType
from decimal import Decimal
from api.database import return_cohort_query
import logging

'''
@pytest.fixture(scope='module')
def coloc_feature(test_db):
    return 'Bindea_aDC'


@pytest.fixture(scope='module')
def coloc_feature_id(test_db, coloc_feature):
    from api.db_models import Feature
    (id, ) = test_db.session.query(Feature.id).filter_by(
        name=coloc_feature).one_or_none()
    return id


@pytest.fixture(scope='module')
def coloc_snp_name(test_db):
    return "18:55726795:A:T"


@pytest.fixture(scope='module')
def coloc_snp_id(test_db, coloc_snp_name):
    from api.db_models import Snp
    (id, ) = test_db.session.query(Snp.id).filter_by(
        name=coloc_snp_name).one_or_none()
    return id


@pytest.fixture(scope='module')
def coloc_gene_entrez(test_db):
    return 4677


@pytest.fixture(scope='module')
def coloc_gene_id(test_db, coloc_gene_entrez):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(
        entrez=coloc_gene_entrez).one_or_none()
    return id


@pytest.fixture(scope='module')
def coloc_data_set1(test_db):
    return "TCGA"


@pytest.fixture(scope='module')
def coloc_data_set1_id(test_db, coloc_data_set1):
    from api.db_models import Dataset
    (id, ) = test_db.session.query(Dataset.id).filter_by(
        name=coloc_data_set1).one_or_none()
    return id


@pytest.fixture(scope='module')
def coloc_data_set2(test_db):
    return "GTEX"


@pytest.fixture(scope='module')
def coloc_data_set2_id(test_db, coloc_data_set2):
    from api.db_models import Dataset
    (id, ) = test_db.session.query(Dataset.id).filter_by(
        name=coloc_data_set2).one_or_none()
    return id


@pytest.fixture(scope='module')
def coloc_qtl_type1(test_db):
    return "sQTL"


@pytest.fixture(scope='module')
def coloc_qtl_type2(test_db):
    return "eQTL"


@pytest.fixture(scope='module')
def coloc_ecaviar_pp(test_db):
    return "C1"


@pytest.fixture(scope='module')
def coloc_plot_type1(test_db):
    return "3 Level Plot"


@pytest.fixture(scope='module')
def coloc_plot_type2(test_db):
    return "Expanded Region"


@pytest.fixture(scope='module')
def coloc_splice_loc(test_db):
    return "intron retention 67961"


@pytest.fixture(scope='module')
def coloc_tissue(test_db):
    return "Artery Aorta"
'''


def test_cohort_relationships(app):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['data_set', 'tag',
                             'samples', 'features', 'genes', 'mutations']

    query = return_cohort_query(*relationships_to_join)
    results = query.all()

    assert isinstance(results, list)
    assert len(results) > 1
    for result in results[0:3]:
        string_representation = '<Cohort %r>' % result.name
        string_representation_list.append(string_representation)
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


'''
def test_eQTL_Colocalization_with_relations(app, data_set, data_set_id, coloc_data_set2, coloc_data_set2_id, coloc_feature, coloc_feature_id, coloc_gene_entrez, coloc_gene_id, coloc_snp_name, coloc_snp_id, coloc_qtl_type2, coloc_plot_type2, coloc_tissue):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['data_set',
                             'coloc_data_set', 'feature', 'snp', 'gene']

    query = return_colocalization_query(*relationships_to_join)
    results = query.filter_by(dataset_id=data_set_id).filter_by(coloc_dataset_id=coloc_data_set2_id).filter_by(feature_id=coloc_feature_id).filter_by(
        snp_id=coloc_snp_id).filter_by(gene_id=coloc_gene_id).filter_by(qtl_type=coloc_qtl_type2).filter_by(plot_type=coloc_plot_type2).filter_by(tissue=coloc_tissue).all()

    assert isinstance(results, list)
    assert len(results) == 1
    for result in results:
        colocalization_id = result.id
        string_representation = '<Colocalization %r>' % colocalization_id
        string_representation_list.append(string_representation)
        assert result.data_set.id == data_set_id
        assert result.data_set.name == data_set
        assert result.coloc_data_set.id == coloc_data_set2_id
        assert result.coloc_data_set.name == coloc_data_set2
        assert result.feature.id == coloc_feature_id
        assert result.feature.name == coloc_feature
        assert result.snp.id == coloc_snp_id
        assert result.snp.name == coloc_snp_name
        assert result.gene.id == coloc_gene_id
        assert result.gene.entrez == coloc_gene_entrez
        assert result.qtl_type == coloc_qtl_type2
        assert result.ecaviar_pp is None
        assert result.plot_type == coloc_plot_type2
        assert result.tissue == coloc_tissue
        assert result.splice_loc is None
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
'''
