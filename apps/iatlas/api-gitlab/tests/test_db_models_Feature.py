import pytest
from tests import app, NoneType
from flaskr.database import return_feature_query
from flaskr.enums import unit_enum


def test_Feature(app):
    app()
    name = 'B_cells_memory'
    display = 'B Cells Memory'
    class_id = 11
    method_tag_id = 2

    query = return_feature_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert type(result.display) is str or NoneType
    assert result.unit in unit_enum.enums
    assert type(result.class_id) is int or NoneType
    assert type(result.method_tag_id) is int or NoneType
    assert repr(result) == '<Feature %r>' % name
