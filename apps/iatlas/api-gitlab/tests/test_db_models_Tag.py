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
        # Don't need to iterate through every result.
        for related_tag in result.related_tags[0:2]:
            assert type(related_tag.name) is str
    if type(result.samples) is not NoneType:
        assert isinstance(result.samples, list)
        # Don't need to iterate through every result.
        for sample in result.samples[0:2]:
            assert type(sample.name) is str
    if type(result.tags) is not NoneType:
        assert isinstance(result.tags, list)
        # Don't need to iterate through every result.
        for tag in result.tags[0:2]:
            assert type(tag.name) is str
    assert result.name == name
    assert type(result.characteristics) is str
    assert type(result.display) is str or NoneType
    assert type(result.color) is str or NoneType
    assert repr(result) == '<Tag %r>' % name
