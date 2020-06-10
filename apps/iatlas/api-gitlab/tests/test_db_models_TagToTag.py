import pytest
from tests import app, NoneType
from flaskr.database import return_tag_to_tag_query
from flaskr.db_models import TagToTag


def test_TagToTag(app):
    app()
    tag_id = 64
    string_representation_list = []
    separator = ', '

    query = return_tag_to_tag_query('related_tags', 'tags')
    results = query.filter_by(tag_id=tag_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<TagToTag %r>' % tag_id
        string_representation_list.append(string_representation)
        if type(result.related_tags) is not NoneType:
            assert isinstance(result.related_tags, list)
            for related_tag in result.related_tags:
                assert type(related_tag.name) is str
        if type(result.tags) is not NoneType:
            assert isinstance(result.tags, list)
            for tag in result.tags:
                assert tag.id == tag_id
        assert result.tag_id == tag_id
        assert type(result.related_tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
