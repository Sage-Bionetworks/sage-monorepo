import pytest
from tests import app, NoneType
from flaskr.database import return_tag_query
from flaskr.db_models import Tag


def test_Tag(app):
    app()
    name = 'ACC'

    query = return_tag_query('related_tags', 'samples', 'tags')
    result = query.filter_by(name=name).first()

    if type(result.related_tags) is not NoneType:
        assert isinstance(result.related_tags, list)
        for related_tag in result.related_tags:
            assert type(related_tag.name) is str
    if type(result.samples) is not NoneType:
        assert isinstance(result.samples, list)
        for sample in result.samples:
            assert type(sample.name) is str
    if type(result.tags) is not NoneType:
        assert isinstance(result.tags, list)
        for tag in result.tags:
            assert type(tag.name) is str
    assert result.name == name
    assert type(result.characteristics) is str
    assert type(result.display) is str or NoneType
    assert type(result.color) is str or NoneType
    assert repr(result) == '<Tag %r>' % name
