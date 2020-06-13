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

    query = return_feature_query(['copy_number_results'])
    result = query.filter_by(name=name).first()

    if type(result.copy_number_results) is not NoneType:
        assert isinstance(result.copy_number_results, list)
        # Don't need to iterate through every result.
        for copy_number_result in result.copy_number_results[0:2]:
            assert copy_number_result.feature_id == result.id

    relationships_to_join = ['feature_class', 'method_tag', 'samples']

    query = return_feature_query(*relationships_to_join)
    result = query.filter_by(name=name).first()

    if type(result.feature_class) is not NoneType:
        assert type(result.feature_class.name) is str
    if type(result.method_tag) is not NoneType:
        assert type(result.method_tag.name) is str
    if type(result.samples) is not NoneType:
        assert isinstance(result.samples, list)
        # Don't need to iterate through every result.
        for sample in result.samples[0:2]:
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

    query = return_feature_query()
    result = query.filter_by(name=name).first()

    assert result.name == name
    assert type(result.display) is str or NoneType
    assert result.unit in unit_enum.enums
    assert type(result.class_id) is int or NoneType
    assert type(result.method_tag_id) is int or NoneType
