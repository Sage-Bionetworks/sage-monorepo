import pytest
from api.database import return_therapy_type_query


@pytest.fixture(scope='module')
def therapy_type():
    return 'T-cell targeted immunomodulator'


def test_TherapyType_with_relations(app, therapy_type):
    query = return_therapy_type_query('genes')
    result = query.filter_by(name=therapy_type).first()

    assert isinstance(result.genes, list)
    assert len(result.genes) > 0
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.name == therapy_type
    assert repr(result) == '<TherapyType %r>' % therapy_type


def test_TherapyType_no_relations(app, therapy_type):
    query = return_therapy_type_query()
    result = query.filter_by(name=therapy_type).first()

    assert result.genes == []
    assert result.name == therapy_type
