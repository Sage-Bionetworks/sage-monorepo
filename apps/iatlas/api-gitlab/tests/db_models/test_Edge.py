import pytest
from tests import NoneType
from api.database import return_edge_query


@pytest.fixture(scope='module')
def node_1():
    return 'tcga_ecn_229715'


@pytest.fixture(scope='module')
def node_1_id(test_db, node_1):
    from api.db_models import Node
    (id, ) = test_db.session.query(Node.id).filter_by(
        name=node_1).one_or_none()
    return id


def test_Edge_with_relations(app, node_1, node_1_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['node_1', 'node_2']

    query = return_edge_query(*relationships_to_join)
    results = query.filter_by(node_1_id=node_1_id).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<Edge %r>' % result.id
        string_representation_list.append(string_representation)
        assert result.node_1.id == node_1_id
        assert result.node_1.name == node_1
        assert result.node_2.id == result.node_2_id
        assert result.node_1_id == node_1_id
        assert type(result.node_2_id) is int
        assert type(result.name) is str
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_Edge_no_relations(app, node_1_id):
    query = return_edge_query()
    results = query.filter_by(node_1_id=node_1_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.node_1) is NoneType
        assert type(result.node_2) is NoneType
        assert result.node_1_id == node_1_id
        assert type(result.node_2_id) is int
        assert type(result.name) is str
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
