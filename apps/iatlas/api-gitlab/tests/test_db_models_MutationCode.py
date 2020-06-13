import pytest
from tests import app, NoneType
from flaskr.database import return_mutation_code_query


def test_MutationCode_with_relations(app):
    app()
    code = 'A146'

    query = return_mutation_code_query(['driver_results'])
    result = query.filter_by(code=code).first()

    if type(result.driver_results) is not NoneType:
        assert isinstance(result.driver_results, list)
        # Don't need to iterate through every result.
        for driver_result in result.driver_results[0:2]:
            assert driver_result.mutation_code_id == result.id

    query = return_mutation_code_query(['mutations'])
    result = query.filter_by(code=code).first()

    if type(result.mutations) is not NoneType:
        assert isinstance(result.mutations, list)
        # Don't need to iterate through every result.
        for mutation in result.mutations[0:2]:
            assert mutation.mutation_code_id == result.id

    query = return_mutation_code_query()
    result = query.filter_by(code=code).first()

    assert result.code == code
    assert repr(result) == '<MutationCode %r>' % code


def test_MutationCode_no_relations(app):
    app()
    code = 'A146'

    query = return_mutation_code_query()
    result = query.filter_by(code=code).first()

    assert type(result.id) is int
    assert result.code == code
