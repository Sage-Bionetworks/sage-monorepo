import pytest
from api.database import return_tag_to_publication_query


@pytest.fixture(scope='module')
def publication_title_with_tag():
    return 'Comprehensive Pan-Genomic Characterization of Adrenocortical Carcinoma.'


@pytest.fixture(scope='module')
def publication_id_with_tag(test_db, publication_title_with_tag):
    from api.db_models import Publication
    (id, ) = test_db.session.query(Publication.id).filter_by(
        title=publication_title_with_tag).one_or_none()
    return id


def test_TagToPublication_with_relations(app, publication_title_with_tag, publication_id_with_tag):
    string_representation_list = []
    separator = ', '

    query = return_tag_to_publication_query('publications', 'tags')
    results = query.filter_by(publication_id=publication_id_with_tag).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        string_representation = '<TagToPublication %r>' % result.tag_id
        string_representation_list.append(string_representation)
        assert isinstance(result.publications, list)
        assert len(result.publications) > 0
        # Don't need to iterate through every result.
        for publication in result.publications[0:5]:
            assert publication.id == publication_id_with_tag
            assert publication.title == publication_title_with_tag
        assert isinstance(result.tags, list)
        assert len(result.tags) > 0
        # Don't need to iterate through every result.
        for tag in result.tags[0:5]:
            assert type(tag.name) is str
        assert result.publication_id == publication_id_with_tag
        assert type(result.tag_id) is str
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_TagToPublication_no_relations(app, publication_id_with_tag):
    query = return_tag_to_publication_query()
    results = query.filter_by(publication_id=publication_id_with_tag).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results[0:5]:
        assert result.publications == []
        assert result.tags == []
        assert result.publication_id == publication_id_with_tag
        assert type(result.tag_id) is str
