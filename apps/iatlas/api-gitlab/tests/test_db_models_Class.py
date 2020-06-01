import json
import os
import pytest
from tests import app, client
from flaskr.db_models import Class


def test_Class(app):
    app()
    feature_class = 'Adaptive Receptor - B cell'
    result = Class.query.filter_by(name=feature_class).first()

    assert result.name == feature_class
