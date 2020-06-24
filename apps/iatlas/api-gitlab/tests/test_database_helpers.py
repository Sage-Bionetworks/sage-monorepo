import pytest
from sqlalchemy import orm
from sqlalchemy.dialects import postgresql
from api.database.database_helpers import (
    build_general_query, build_option_args, build_query_args)
from api.db_models import Base, Feature
from tests import db


class MockModel(Base):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<MockModel %r>' % self.id


def test_build_general_query(test_db):
    model = Feature
    query_arg_1 = 'id'
    query_arg_2 = 'name'
    accepted_query_args = [query_arg_1, query_arg_2]
    option_value_1 = 'feature_class'
    accepted_option_args = [option_value_1]
    test_1 = build_general_query(
        model, args=[query_arg_1,
                     query_arg_2, option_value_1], accepted_option_args=accepted_option_args,
        accepted_query_args=accepted_query_args)
    test_2 = build_general_query(
        model, args=[query_arg_1,
                     query_arg_2], accepted_option_args=accepted_option_args,
        accepted_query_args=accepted_query_args)
    test_3 = build_general_query(
        model, args=[], accepted_option_args=accepted_option_args,
        accepted_query_args=accepted_query_args)

    assert str(test_1.statement.compile(dialect=postgresql.dialect())) == str(test_db.session.query(model).options(
        orm.joinedload(option_value_1)).statement.compile(dialect=postgresql.dialect()))

    assert str(test_2.statement.compile(dialect=postgresql.dialect())) == str(
        test_db.session.query(getattr(model, query_arg_1), getattr(model, query_arg_2)).statement.compile(dialect=postgresql.dialect()))

    assert str(test_3.statement.compile(dialect=postgresql.dialect())) == str(
        test_db.session.query(model).statement.compile(dialect=postgresql.dialect()))


def test_build_option_args():
    expected_value_1 = 'nice'
    expected_value_2 = 'good'
    accepted_args = [expected_value_1, expected_value_2]
    test_1 = build_option_args(
        expected_value_1, accepted_args=accepted_args)
    test_2 = build_option_args(
        expected_value_1, expected_value_2, accepted_args=accepted_args)
    assert test_1 and isinstance(test_1, list)
    assert test_2 and isinstance(test_2, list)
    assert not build_option_args(expected_value_1)
    assert not build_option_args(expected_value_1, [])


def test_build_query_args():
    arg_1 = 'id'
    arg_2 = 'name'
    accepted_args = [arg_1, arg_2]
    test_1 = build_query_args(MockModel, arg_1, arg_2,
                              accepted_args=accepted_args)
    test_2 = build_query_args(MockModel, arg_1, arg_2)
    test_3 = build_query_args(MockModel)
    assert test_1 == [MockModel.id, MockModel.name]
    assert test_2 == [MockModel]
    assert test_3 == [MockModel]
