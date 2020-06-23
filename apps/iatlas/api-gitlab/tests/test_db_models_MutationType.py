import pytest
from tests import NoneType
from flaskr.database import return_mutation_type_query


@pytest.fixture(scope='module')
def name():
    return 'driver_mutation'


def test_MutationType_with_relations(app, name):
    query = return_mutation_type_query(['mutations'])
    result = query.filter_by(name=name).first()

    if result.mutations:
        assert isinstance(result.mutations, list)
        # Don't need to iterate through every result.
        for mutation in result.mutations[0:2]:
            assert mutation.mutation_code_id == result.id

    assert result.name == name
    assert type(result.display) is str or NoneType
    assert repr(result) == '<MutationType %r>' % name


def test_MutationType_no_relations(app, name):
    query = return_mutation_type_query()
    result = query.filter_by(name=name).first()

    assert result.mutations == []
    assert type(result.id) is int
    assert result.name == name
    assert type(result.display) is str or NoneType
