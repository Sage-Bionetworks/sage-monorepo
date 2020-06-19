import pytest
from tests import app, NoneType
from flaskr.database import return_sample_query


def test_Sample_with_relations(app):
    app()
    name = 'DO1328'

    query = return_sample_query('datasets')
    result = query.filter_by(name=name).first()

    if type(result.datasets) is not NoneType:
        assert isinstance(result.datasets, list)
        # Don't need to iterate through every result.
        for dataset in result.datasets[0:2]:
            assert type(dataset.name) is str

    query = return_sample_query('dataset_sample_assoc')
    result = query.filter_by(name=name).first()

    if type(result.dataset_sample_assoc) is not NoneType:
        assert isinstance(result.dataset_sample_assoc, list)
        # Don't need to iterate through every result.
        for dataset_sample_rel in result.dataset_sample_assoc[0:2]:
            assert dataset_sample_rel.sample_id == result.id

    query = return_sample_query('feature_sample_assoc')
    result = query.filter_by(name=name).first()

    if type(result.feature_sample_assoc) is not NoneType:
        assert isinstance(result.feature_sample_assoc, list)
        # Don't need to iterate through every result.
        for feature_sample_rel in result.feature_sample_assoc[0:2]:
            assert feature_sample_rel.sample_id == result.id

    query = return_sample_query('features')
    result = query.filter_by(name=name).first()

    if type(result.features) is not NoneType:
        assert isinstance(result.features, list)
        # Don't need to iterate through every result.
        for feature in result.features[0:2]:
            assert type(feature.name) is str

    query = return_sample_query('genes')
    result = query.filter_by(name=name).first()

    if type(result.genes) is not NoneType:
        assert isinstance(result.genes, list)
        # Don't need to iterate through every result.
        for gene in result.genes[0:2]:
            assert type(gene.entrez) is int

    query = return_sample_query('gene_sample_assoc')
    result = query.filter_by(name=name).first()

    if type(result.gene_sample_assoc) is not NoneType:
        assert isinstance(result.gene_sample_assoc, list)
        # Don't need to iterate through every result.
        for gene_sample_rel in result.gene_sample_assoc[0:2]:
            assert gene_sample_rel.sample_id == result.id

    query = return_sample_query('mutations')
    result = query.filter_by(name=name).first()

    if type(result.mutations) is not NoneType:
        assert isinstance(result.mutations, list)
        # Don't need to iterate through every result.
        for mutation in result.mutations[0:2]:
            assert type(mutation.id) is int

    query = return_sample_query('patient')
    result = query.filter_by(name=name).first()

    if type(result.patient) is not NoneType:
        assert result.patient.id == result.patient_id

    query = return_sample_query('sample_mutation_assoc')
    result = query.filter_by(name=name).first()

    if type(result.sample_mutation_assoc) is not NoneType:
        assert isinstance(result.sample_mutation_assoc, list)
        # Don't need to iterate through every result.
        for sample_mutation_rel in result.sample_mutation_assoc[0:2]:
            assert sample_mutation_rel.sample_id == result.id

    query = return_sample_query('tags')
    result = query.filter_by(name=name).first()

    if type(result.tags) is not NoneType:
        assert isinstance(result.tags, list)
        # Don't need to iterate through every result.
        for tag in result.tags[0:2]:
            assert type(tag.name) is str
    assert result.name == name
    assert type(result.patient_id) is int or NoneType
    assert repr(result) == '<Sample %r>' % name


def test_Sample_no_relations(app):
    app()
    name = 'DO1328'

    query = return_sample_query()
    result = query.filter_by(name=name).first()

    assert result.datasets == []
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
    assert result.name == name
    assert type(result.patient_id) is int or NoneType
