import json
import pytest
from sqlalchemy import orm
from tests import app
from flaskr.database import build_option_args


def test_build_option_args(app):
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
