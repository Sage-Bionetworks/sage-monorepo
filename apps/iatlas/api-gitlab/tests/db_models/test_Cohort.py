import pytest
from tests import NoneType
from decimal import Decimal
from api.database import return_cohort_query
import logging


@pytest.fixture(scope='module')
def tag_cohort_name():
    return 'tcga_immune_subtype'


@pytest.fixture(scope='module')
def tag_cohort_id(test_db, tag_cohort_name):
    from api.db_models import Cohort
    (id, ) = test_db.session.query(Cohort.id).filter_by(
        name=tag_cohort_name).one_or_none()
    return id


@pytest.fixture(scope='module')
def clinical_cohort_name():
    return 'tcga_gender'


@pytest.fixture(scope='module')
def clinical_cohort_id(test_db, clinical_cohort_name):
    from api.db_models import Cohort
    (id, ) = test_db.session.query(Cohort.id).filter_by(
        name=clinical_cohort_name).one_or_none()
    return id


def test_tag_cohort_no_relationships(app, tag_cohort_name, tag_cohort_id, related_id, data_set_id):
    query = return_cohort_query()
    result = query.filter_by(name=tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tag_cohort_id
    assert result.name == tag_cohort_name
    assert result.tag_id == related_id
    assert result.dataset_id == data_set_id
    assert type(result.clinical) is NoneType


def test_clinical_cohort_no_relationships(app, clinical_cohort_name, clinical_cohort_id, data_set_id):
    query = return_cohort_query()
    result = query.filter_by(name=clinical_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == clinical_cohort_id
    assert result.name == clinical_cohort_name
    assert type(result.tag_id) is NoneType
    assert result.dataset_id == data_set_id
    assert result.clinical == "Gender"


def test_cohort_samples_relationship(app, tag_cohort_name, tag_cohort_id, related_id, data_set_id):
    query = return_cohort_query('samples')
    result = query.filter_by(name=tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tag_cohort_id
    assert result.name == tag_cohort_name
    assert result.tag_id == related_id
    assert result.dataset_id == data_set_id
    assert type(result.clinical) is NoneType
    for sample in result.samples[0:2]:
        assert type(sample.id) is int
        assert type(sample.name) is str


def test_cohort_genes_relationship(app, tag_cohort_name, tag_cohort_id, related_id, data_set_id):
    query = return_cohort_query('genes')
    result = query.filter_by(name=tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tag_cohort_id
    assert result.name == tag_cohort_name
    assert result.tag_id == related_id
    assert result.dataset_id == data_set_id
    assert type(result.clinical) is NoneType
    for gene in result.genes[0:2]:
        assert type(gene.id) is int
        assert type(gene.entrez) is int
        assert type(gene.hgnc) is str


def test_cohort_features_relationship(app, tag_cohort_name, tag_cohort_id, related_id, data_set_id):
    query = return_cohort_query('features')
    result = query.filter_by(name=tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tag_cohort_id
    assert result.name == tag_cohort_name
    assert result.tag_id == related_id
    assert result.dataset_id == data_set_id
    assert type(result.clinical) is NoneType
    for feature in result.features[0:2]:
        assert type(feature.id) is int
        assert type(feature.name) is str
        assert type(feature.display) is str


def test_cohort_mutations_relationship(app, tag_cohort_name, tag_cohort_id, related_id, data_set_id):
    query = return_cohort_query('mutations')
    result = query.filter_by(name=tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tag_cohort_id
    assert result.name == tag_cohort_name
    assert result.tag_id == related_id
    assert result.dataset_id == data_set_id
    assert type(result.clinical) is NoneType
    for mutation in result.mutations[0:2]:
        assert type(mutation.id) is int
        assert type(mutation.gene_id) is int
        assert type(mutation.mutation_code_id) is int
        assert type(mutation.mutation_type_id) is int


def test_cohort_tag_relationship(app, tag_cohort_name, tag_cohort_id, related, related_id, data_set_id):
    query = return_cohort_query('tag')
    result = query.filter_by(name=tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tag_cohort_id
    assert result.name == tag_cohort_name
    assert result.tag_id == related_id
    assert result.dataset_id == data_set_id
    assert type(result.clinical) is NoneType
    assert result.tag.name == related


def test_cohort_dataset_relationship(app, tag_cohort_name, tag_cohort_id, related_id, data_set_id, data_set):
    query = return_cohort_query('data_set')
    result = query.filter_by(name=tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tag_cohort_id
    assert result.name == tag_cohort_name
    assert result.tag_id == related_id
    assert result.dataset_id == data_set_id
    assert type(result.clinical) is NoneType
    assert result.data_set.name == data_set
