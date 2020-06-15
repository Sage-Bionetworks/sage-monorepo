import pytest
from tests import app, NoneType
from flaskr.database import return_tag_to_tag_query
from flaskr.db_models import TagToTag


def test_TagToTag_with_relations(app):
    app()
    tag_id = 11
    string_representation_list = []
    separator = ', '

    query = return_tag_to_tag_query('related_tags', 'tags')
    results = query.filter_by(tag_id=tag_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<TagToTag %r>' % tag_id
        string_representation_list.append(string_representation)
        if type(result.related_tags) is not NoneType:
            assert isinstance(result.related_tags, list)
            # Don't need to iterate through every result.
            for related_tag in result.related_tags[0:2]:
                assert type(related_tag.name) is str
        if type(result.tags) is not NoneType:
            assert isinstance(result.tags, list)
            # Don't need to iterate through every result.
            for tag in result.tags[0:2]:
                assert tag.id == tag_id
        assert result.tag_id == tag_id
        assert type(result.related_tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_TagToTag_no_relations(app):
    app()
    tag_id = 64

    query = return_tag_to_tag_query()
    results = query.filter_by(tag_id=tag_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.related_tags == []
        assert result.tags == []
        assert result.tag_id == tag_id
        assert type(result.related_tag_id) is int
