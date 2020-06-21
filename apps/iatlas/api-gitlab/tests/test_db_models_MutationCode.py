import pytest
from tests import app
from flaskr.database import return_mutation_code_query

code = 'A146'


def test_MutationCode_with_mutations(app):
    app()

    query = return_mutation_code_query(['mutations'])
    result = query.filter_by(code=code).first()

    if result.mutations:
        assert isinstance(result.mutations, list)
        # Don't need to iterate through every result.
        for mutation in result.mutations[0:2]:
            assert mutation.mutation_code_id == result.id
    assert result.code == code
    assert repr(result) == '<MutationCode %r>' % code


def test_MutationCode_with_driver_results(app):
    app()

    query = return_mutation_code_query(['driver_results'])
    result = query.filter_by(code=code).first()

    if result.driver_results:
        assert isinstance(result.driver_results, list)
        # Don't need to iterate through every result.
        for driver_result in result.driver_results[0:2]:
            assert driver_result.mutation_code_id == result.id


def test_MutationCode_no_relations(app):
    app()

    query = return_mutation_code_query()
    result = query.filter_by(code=code).first()

    assert result.driver_results == []
    assert result.mutations == []
    assert type(result.id) is int
    assert result.code == code
