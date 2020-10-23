import pytest
from api.database import return_tag_to_tag_query
from api.db_models import TagToTag


@pytest.fixture(scope='module')
def tt_tag(test_db):
    return 'AML.3'


@pytest.fixture(scope='module')
def tag_id(test_db, tt_tag):
    from api.db_models import Tag
    (id, ) = test_db.session.query(Tag.id).filter_by(
        name=tt_tag).one_or_none()
    return id


def test_TagToTag_with_relations(app, tt_tag, tag_id):
    string_representation_list = []
    separator = ', '

    query = return_tag_to_tag_query('related_tags', 'tags')
    results = query.filter_by(tag_id=tag_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        string_representation = '<TagToTag %r>' % tag_id
        string_representation_list.append(string_representation)
        assert isinstance(result.related_tags, list)
        assert len(result.related_tags) > 0
        # Don't need to iterate through every result.
        for related_tag in result.related_tags[0:2]:
            assert type(related_tag.name) is str
        assert isinstance(result.tags, list)
        assert len(result.tags) > 0
        # Don't need to iterate through every result.
        for tag in result.tags[0:2]:
            assert tag.id == tag_id
            assert tag.name == tt_tag
        assert result.tag_id == tag_id
        assert type(result.related_tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_TagToTag_no_relations(app, tag_id):
    query = return_tag_to_tag_query()
    results = query.filter_by(tag_id=tag_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result.related_tags == []
        assert result.tags == []
        assert result.tag_id == tag_id
        assert type(result.related_tag_id) is int
