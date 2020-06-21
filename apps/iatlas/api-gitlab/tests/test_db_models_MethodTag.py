import pytest
from tests import app
from flaskr.database import return_method_tag_query

name = 'ExpSig'


def test_MethodTag_with_relations(app):
    app()
    relationships_to_join = ['features']

    query = return_method_tag_query(*relationships_to_join)
    result = query.filter_by(name=name).first()

    assert isinstance(result.features, list)
    # Don't need to iterate through every result.
    for feature in result.features[0:2]:
        assert type(feature.name) is str
    assert result.name == name
    assert repr(result) == '<MethodTag %r>' % name


def test_MethodTag_no_relations(app):
    app()

    query = return_method_tag_query()
    result = query.filter_by(name=name).first()

    assert result.features == []
    assert result.name == name
    assert repr(result) == '<MethodTag %r>' % name
