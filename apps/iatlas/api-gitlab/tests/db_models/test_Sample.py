import pytest
from tests import NoneType
from api.database import return_sample_query


def test_Sample_with_data_sets(app, sample):
    query = return_sample_query('data_sets')
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert result.name == sample
    assert isinstance(result.data_sets, list)
    assert len(result.data_sets) > 0
    # Don't need to iterate through every result.
    for data_set in result.data_sets[0:2]:
        assert type(data_set.name) is str


def test_Sample_with_dataset_sample_assoc(app, sample):
    query = return_sample_query('dataset_sample_assoc')
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert result.name == sample
    assert isinstance(result.dataset_sample_assoc, list)
    assert len(result.dataset_sample_assoc) > 0
    # Don't need to iterate through every result.
    for dataset_sample_rel in result.dataset_sample_assoc[0:2]:
        assert dataset_sample_rel.sample_id == result.id


def test_Sample_with_feature_sample_assoc(app, sample):
    query = return_sample_query('feature_sample_assoc')
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert result.name == sample
    assert isinstance(result.feature_sample_assoc, list)
    assert len(result.feature_sample_assoc) > 0
    # Don't need to iterate through every result.
    for feature_sample_rel in result.feature_sample_assoc[0:2]:
        assert feature_sample_rel.sample_id == result.id


def test_Sample_with_feature(app, sample):
    query = return_sample_query('features')
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert result.name == sample
    assert isinstance(result.features, list)
    assert len(result.features) > 0
    # Don't need to iterate through every result.
    for feature in result.features[0:2]:
        assert type(feature.name) is str


def test_Sample_with_genes(app, sample):
    query = return_sample_query('genes')
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert result.name == sample
    assert isinstance(result.genes, list)
    assert len(result.genes) > 0
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int


def test_Sample_with_gene_sample_assoc(app, sample):
    query = return_sample_query('gene_sample_assoc')
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert result.name == sample
    assert isinstance(result.gene_sample_assoc, list)
    assert len(result.gene_sample_assoc) > 0
    # Don't need to iterate through every result.
    for gene_sample_rel in result.gene_sample_assoc[0:2]:
        assert gene_sample_rel.sample_id == result.id


def test_Sample_with_mutations(app, sample):
    query = return_sample_query('mutations')
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert result.name == sample
    assert isinstance(result.mutations, list)
    assert len(result.mutations) > 0
    # Don't need to iterate through every result.
    for mutation in result.mutations[0:2]:
        assert type(mutation.id) is int


def test_Sample_with_patient(app, sample):
    query = return_sample_query('patient')
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert result.name == sample
    assert result.patient.id == result.patient_id


def test_Sample_with_sample_mutation_assoc(app, sample):
    query = return_sample_query('sample_mutation_assoc')
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert result.name == sample
    assert isinstance(result.sample_mutation_assoc, list)
    assert len(result.sample_mutation_assoc) > 0
    # Don't need to iterate through every result.
    for sample_mutation_rel in result.sample_mutation_assoc[0:2]:
        assert sample_mutation_rel.sample_id == result.id


def test_Sample_with_tags(app, sample):
    query = return_sample_query('tags')
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert isinstance(result.tags, list)
    assert len(result.tags) > 0
    # Don't need to iterate through every result.
    for tag in result.tags[0:2]:
        assert type(tag.name) is str
    assert result.dataset_sample_assoc == []
    assert result.gene_sample_assoc == []
    assert result.feature_sample_assoc == []
    assert result.sample_mutation_assoc == []
    assert result.name == sample
    assert type(result.patient_id) is int or NoneType
    assert repr(result) == '<Sample %r>' % sample


def test_Sample_no_relations(app, sample):
    query = return_sample_query()
    result = query.filter_by(name=sample).one_or_none()

    assert result
    assert result.data_sets == []
    assert result.dataset_sample_assoc == []
    assert result.gene_sample_assoc == []
    assert result.feature_sample_assoc == []
    assert result.sample_mutation_assoc == []
    assert result.features == []
    assert result.genes == []
    assert result.mutations == []
    assert type(result.patient) is NoneType
    assert result.tags == []
    assert type(result.id) is int
    assert result.name == sample
    assert type(result.patient_id) is int or NoneType
