import pytest
from tests import app, NoneType
from flaskr.db_models import EdgeToTag


def test_EdgeToTag(app):
    pass
    # app()
    # edge_id = 1
    # string_representation_list = []
    # separator = ', '

    # results = EdgeToTag.query.filter_by(edge_id=edge_id).all()

    # assert isinstance(results, list)
    # for result in results:
    #     string_representation = '<EdgeToTag %r>' % edge_id
    #     string_representation_list.append(string_representation)
    #     assert result.edge_id == edge_id
    #     assert type(result.tag_id) is int
    #     assert repr(result) == string_representation
    # assert repr(results) == '[' + separator.join(
    #     string_representation_list) + ']'x
