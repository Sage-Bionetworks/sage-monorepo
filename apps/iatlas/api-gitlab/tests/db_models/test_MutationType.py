import pytest
from tests import NoneType
from api.database import return_mutation_type_query
from api.db_models import MutationType


@pytest.fixture(scope='module')
def mutation_type_name():
    return 'driver_mutation'


def test_MutationType_with_relations(app, mutation_type_name):
    query = return_mutation_type_query('mutations')
    result = query.filter_by(name=mutation_type_name).first()

    if result.mutations:
        assert isinstance(result.mutations, list)
        # Don't need to iterate through every result.
        for mutation in result.mutations[0:2]:
            assert type(mutation.id) is str
            assert type(mutation.name) is str
            assert type(mutation.mutation_code) is str

    assert result.name == mutation_type_name
    assert type(result.display) is str
    assert repr(result) == '<MutationType %r>' % mutation_type_name


def test_MutationType_no_relations(app, mutation_type_name):
    query = return_mutation_type_query()
    result = query.filter_by(name=mutation_type_name).first()
    assert result.mutations == []
    assert type(result.id) is str
    assert result.name == mutation_type_name
    assert type(result.display) is str or NoneType
