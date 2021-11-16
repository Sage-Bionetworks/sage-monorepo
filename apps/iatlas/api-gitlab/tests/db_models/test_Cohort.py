from tests import NoneType
from api.database import return_cohort_query


def test_tag_cohort_no_relationships(app, tcga_tag_cohort_name, tcga_tag_cohort_id, related_id3, data_set_id):
    query = return_cohort_query()
    result = query.filter_by(name=tcga_tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tcga_tag_cohort_id
    assert result.name == tcga_tag_cohort_name
    assert result.tag_id == related_id3
    assert result.dataset_id == data_set_id


def test_dataset_cohort_no_relationships(app, pcawg_cohort_name, pcawg_cohort_id, pcawg_data_set_id):
    query = return_cohort_query()
    result = query.filter_by(name=pcawg_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == pcawg_cohort_id
    assert result.name == pcawg_cohort_name
    assert type(result.tag_id) is NoneType
    assert result.dataset_id == pcawg_data_set_id


def test_cohort_samples_relationship(app, tcga_tag_cohort_name, tcga_tag_cohort_id, related_id3, data_set_id):
    query = return_cohort_query('samples')
    result = query.filter_by(name=tcga_tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tcga_tag_cohort_id
    assert result.name == tcga_tag_cohort_name
    assert result.tag_id == related_id3
    assert result.dataset_id == data_set_id
    for sample in result.samples[0:2]:
        assert type(sample.id) is int
        assert type(sample.name) is str


def test_cohort_genes_relationship(app, tcga_tag_cohort_name, tcga_tag_cohort_id, related_id3, data_set_id):
    query = return_cohort_query('genes')
    result = query.filter_by(name=tcga_tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tcga_tag_cohort_id
    assert result.name == tcga_tag_cohort_name
    assert result.tag_id == related_id3
    assert result.dataset_id == data_set_id
    for gene in result.genes[0:2]:
        assert type(gene.id) is int
        assert type(gene.entrez) is int
        assert type(gene.hgnc) is str


def test_cohort_features_relationship(app, tcga_tag_cohort_name, tcga_tag_cohort_id, related_id3, data_set_id):
    query = return_cohort_query('features')
    result = query.filter_by(name=tcga_tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tcga_tag_cohort_id
    assert result.name == tcga_tag_cohort_name
    assert result.tag_id == related_id3
    assert result.dataset_id == data_set_id
    for feature in result.features[0:2]:
        assert type(feature.id) is int
        assert type(feature.name) is str
        assert type(feature.display) is str


def test_cohort_mutations_relationship(app, tcga_tag_cohort_name, tcga_tag_cohort_id, related_id3, data_set_id):
    query = return_cohort_query('mutations')
    result = query.filter_by(name=tcga_tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tcga_tag_cohort_id
    assert result.name == tcga_tag_cohort_name
    assert result.tag_id == related_id3
    assert result.dataset_id == data_set_id
    for mutation in result.mutations[0:2]:
        assert type(mutation.id) is int
        assert type(mutation.gene_id) is int
        assert type(mutation.mutation_code_id) is int
        assert type(mutation.mutation_type_id) is int


def test_cohort_tag_relationship(app, tcga_tag_cohort_name, tcga_tag_cohort_id, related3, related_id3, data_set_id):
    query = return_cohort_query('tag')
    result = query.filter_by(name=tcga_tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tcga_tag_cohort_id
    assert result.name == tcga_tag_cohort_name
    assert result.tag_id == related_id3
    assert result.dataset_id == data_set_id
    assert result.tag.name == related3


def test_cohort_dataset_relationship(app, tcga_tag_cohort_name, tcga_tag_cohort_id, related_id3, data_set_id, data_set):
    query = return_cohort_query('data_set')
    result = query.filter_by(name=tcga_tag_cohort_name).one_or_none()
    string_representation = '<Cohort %r>' % result.name
    assert repr(result) == string_representation
    assert result
    assert result.id == tcga_tag_cohort_id
    assert result.name == tcga_tag_cohort_name
    assert result.tag_id == related_id3
    assert result.dataset_id == data_set_id
    assert result.data_set.name == data_set
