import pytest
from api.database import return_tag_to_publication_query


@pytest.fixture(scope='module')
def tp_publication(test_db):
    return '10.1016/j.cell.2015.12.028_26824661'


@pytest.fixture(scope='module')
def publication_id(test_db, tp_publication):
    from api.db_models import Publication
    (id, ) = test_db.session.query(Publication.id).filter_by(
        name=tp_publication).one_or_none()
    return id


def test_TagToPublication_with_relations(app, tp_publication, publication_id):
    string_representation_list = []
    separator = ', '

    query = return_tag_to_publication_query('publications', 'tags')
    results = query.filter_by(publication_id=publication_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        string_representation = '<TagToPublication %r>' % result.tag_id
        string_representation_list.append(string_representation)
        assert isinstance(result.publications, list)
        assert len(result.publications) > 0
        # Don't need to iterate through every result.
        for publication in result.publications[0:5]:
            assert publication.id == publication_id
            assert publication.name == tp_publication
        assert isinstance(result.tags, list)
        assert len(result.tags) > 0
        # Don't need to iterate through every result.
        for tag in result.tags[0:5]:
            assert type(tag.name) is str
        assert result.publication_id == publication_id
        assert type(result.tag_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_TagToPublication_no_relations(app, publication_id):
    query = return_tag_to_publication_query()
    results = query.filter_by(publication_id=publication_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:5]:
        assert result.publications == []
        assert result.tags == []
        assert result.publication_id == publication_id
        assert type(result.tag_id) is int
