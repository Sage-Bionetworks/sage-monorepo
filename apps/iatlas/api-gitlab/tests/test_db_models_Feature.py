import pytest
from tests import app, NoneType
from flaskr.database import return_feature_query
from flaskr.enums import unit_enum


def test_Feature_with_relations(app):
    app()
    name = 'B_cells_memory'
    display = 'B Cells Memory'
    class_id = 11
    method_tag_id = 2
    relationships_to_join = ['feature_class', 'method_tag', 'samples']

    query = return_feature_query(*relationships_to_join)
    result = query.filter_by(name=name).first()

    if type(result.feature_class) is not NoneType:
        assert type(result.feature_class.name) is str
    if type(result.method_tag) is not NoneType:
        assert type(result.method_tag.name) is str
    if type(result.samples) is not NoneType:
        assert isinstance(result.samples, list)
        for sample in result.samples:
            assert type(sample.name) is str
    assert result.name == name
    assert type(result.display) is str or NoneType
    assert result.unit in unit_enum.enums
    assert type(result.class_id) is int or NoneType
    assert type(result.method_tag_id) is int or NoneType
    assert repr(result) == '<Feature %r>' % name


def test_Feature_no_relations(app):
    app()
    name = 'B_cells_memory'
    display = 'B Cells Memory'
    class_id = 11
    method_tag_id = 2
    fields_to_return = ['id', 'name', 'display',
                        'order', 'unit', 'class_id', 'method_tag_id']

    query = return_feature_query(*fields_to_return)
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert type(result.display) is str or NoneType
    assert result.unit in unit_enum.enums
    assert type(result.class_id) is int or NoneType
    assert type(result.method_tag_id) is int or NoneType
