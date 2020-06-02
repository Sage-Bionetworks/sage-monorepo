import json
import os
import pytest
from tests import app, client
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
    assert result.display == display
    assert result.unit in unit_enum.enums
    assert result.class_id == class_id
    assert result.method_tag_id == method_tag_id
    assert repr(result) == '<Feature %r>' % name
