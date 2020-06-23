import pytest
from flaskr.database import return_immune_checkpoint_query
from flaskr.db_models import ImmuneCheckpoint


@pytest.fixture(scope='module')
def name():
    return 'Stimulatory'


def test_ImmuneCheckpoint_with_relations(app, name):
    query = return_immune_checkpoint_query('genes')
    result = query.filter_by(name=name).first()

    assert isinstance(result.genes, list)
    # Don't need to iterate through every result.
    for gene in result.genes[0:2]:
        assert type(gene.entrez) is int
    assert result.name == name
    assert repr(result) == '<ImmuneCheckpoint %r>' % name


def test_ImmuneCheckpoint_no_relations(app, name):
    query = return_immune_checkpoint_query()
    result = query.filter_by(name=name).first()

    assert result.genes == []
    assert result.name == name
