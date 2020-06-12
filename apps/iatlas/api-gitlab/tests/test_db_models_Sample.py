import pytest
from tests import app, NoneType
from flaskr.database import return_sample_query


def test_Sample_with_relations(app):
    app()
    name = 'DO1328'

    query = return_sample_query(*['features'])
    result = query.filter_by(name=name).first()

    if type(result.features) is not NoneType:
        assert isinstance(result.features, list)
        for feature in result.features:
            assert type(feature.name) is str

    query = return_sample_query(*['genes'])
    result = query.filter_by(name=name).first()

    if type(result.genes) is not NoneType:
        assert isinstance(result.genes, list)
        for gene in result.genes:
            assert type(gene.entrez) is int

    query = return_sample_query(*['mutations'])
    result = query.filter_by(name=name).first()

    if type(result.mutations) is not NoneType:
        assert isinstance(result.mutations, list)
        for mutation in result.mutations:
            assert type(mutation.id) is int

    query = return_sample_query(*['tags'])
    result = query.filter_by(name=name).first()

    if type(result.tags) is not NoneType:
        assert isinstance(result.tags, list)
        for tag in result.tags:
            assert type(tag.name) is str
    assert result.name == name
    assert type(result.patient_id) is int or NoneType
    assert repr(result) == '<Sample %r>' % name


def test_Sample_no_relations(app):
    app()
    name = 'DO1328'

    query = return_sample_query()
    result = query.filter_by(name=name).first()

    assert result.features == []
    assert result.genes == []
    assert result.mutations == []
    assert result.tags == []
    assert type(result.id) is int
    assert result.name == name
    assert type(result.patient_id) is int or NoneType
    assert repr(result) == '<Sample %r>' % name
