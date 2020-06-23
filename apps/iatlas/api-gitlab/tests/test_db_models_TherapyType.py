import pytest
from flaskr.database import return_therapy_type_query

name = 'T-cell targeted immunomodulator'


def test_TherapyType_with_relations(app):
    app()

    query = return_therapy_type_query('genes')
    result = query.filter_by(name=name).first()

    assert isinstance(result.genes, list)
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.name == name
    assert repr(result) == '<TherapyType %r>' % name


def test_TherapyType_no_relations(app):
    app()

    query = return_therapy_type_query()
    result = query.filter_by(name=name).first()

    assert result.genes == []
    assert result.name == name
