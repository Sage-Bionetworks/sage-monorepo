import json
import os
import pytest
from tests import app, NoneType
from flaskr.db_models import Feature
from flaskr.enums import unit_enum


def test_Feature(app):
    app()
    name = 'B_cells_memory'
    display = 'B Cells Memory'
    class_id = 11
    method_tag_id = 2

    result = Feature.query.filter_by(name=name).first()

    assert result.name == name
    assert type(result.display) is str or NoneType
    assert result.unit in unit_enum.enums
    assert type(result.class_id) is int or NoneType
    assert type(result.method_tag_id) is int or NoneType
    assert repr(result) == '<Feature %r>' % name
