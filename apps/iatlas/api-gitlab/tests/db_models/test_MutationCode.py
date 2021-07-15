import pytest
from api.database import return_mutation_code_query


@pytest.fixture(scope='module')
def code():
    return 'A146'


def test_MutationCode_with_mutations(app, code):
    query = return_mutation_code_query('mutations')
    result = query.filter_by(code=code).one_or_none()

    assert isinstance(result.mutations, list)
    assert len(result.mutations) > 0
    for mutation in result.mutations[0:2]:
        assert mutation.mutation_code_id == result.id
    assert result.code == code
    assert repr(result) == '<MutationCode %r>' % code


def test_MutationCode_no_relations(app, code):
    query = return_mutation_code_query()
    result = query.filter_by(code=code).one_or_none()

    assert result.mutations == []
    assert type(result.id) is int
    assert result.code == code
