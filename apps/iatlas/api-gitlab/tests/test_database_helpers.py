import pytest
from sqlalchemy import orm
from flaskr.database.database_helpers import build_option_args, build_query_args
from flaskr.db_models import Base
from . import db


class MockModel(Base):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, nullable=False)

    def __repr__(self):
        return '<MockModel %r>' % self.id


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
    assert test_2 == []
    assert test_3 == MockModel
