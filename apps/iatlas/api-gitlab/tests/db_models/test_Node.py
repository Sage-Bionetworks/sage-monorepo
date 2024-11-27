import pytest
from tests import NoneType
from api.database import return_node_query


@pytest.fixture(scope='module')
def node_entrez_id(test_db):
    return 5817


@pytest.fixture(scope='module')
def node_gene_id(test_db, node_entrez_id):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(
        entrez_id=node_entrez_id).one_or_none()
    return id


def test_Node_with_relations(app, node_entrez_id, node_gene_id):
    string_representation_list = []
    separator = ', '

    relationships_to_load = ['data_set', 'gene', 'feature']
    query = return_node_query(*relationships_to_load)
    results = query.filter_by(node_gene_id=node_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        node_id = result.id
        string_representation = '<Node %r>' % node_id
        string_representation_list.append(string_representation)
        if result.data_set:
            assert result.data_set.id == result.dataset_id
        assert result.gene.entrez_id == node_entrez_id
        if result.feature:
            assert result.node_feature.id == result.node_feature_id
        assert result.edges_primary == []
        assert result.edges_secondary == []
        assert type(result.tag1) is NoneType
        assert result.node_gene_id == node_gene_id
        assert type(result.node_feature_id) is NoneType
        assert type(result.name) is str
        assert type(result.network) is str
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert type(result.x) is float or NoneType
        assert type(result.y) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_Node_with_edges_primary(app, node_gene_id):
    query = return_node_query('edges_primary')
    result = query.filter_by(node_gene_id=node_gene_id).first()

    if result.edges_primary:
        assert isinstance(result.edges_primary, list)
        # Don't need to iterate through every result.
        for edge_primary in result.edges_primary[0:2]:
            assert edge_primary.node_1_id == result.id


def test_Node_with_edges_secondary(app, node_gene_id):
    query = return_node_query('edges_secondary')
    result = query.filter_by(node_gene_id=node_gene_id).first()

    if result.edges_secondary:
        assert isinstance(result.edges_secondary, list)
        # Don't need to iterate through every result.
        for edge_secondary in result.edges_secondary[0:2]:
            assert edge_secondary.node_2_id == result.id


def test_Node_with_tags(app, node_gene_id):
    query = return_node_query('tag1', 'tag2')
    result = query.filter_by(name="TCGA_extracellular_network_C1:ACC_2").first()

    tag1 = result.tag1
    assert tag1.name == 'C1'

    tag2 = result.tag2
    assert tag2.name == 'ACC'


def test_Node_no_relations(app, node_gene_id):
    query = return_node_query()
    results = query.filter_by(node_gene_id=node_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.gene) is NoneType
        assert type(result.feature) is NoneType
        assert result.edges_primary == []
        assert result.edges_secondary == []
        assert type(result.tag1) is NoneType
        assert type(result.id) is str
        assert type(result.dataset_id) is str
        assert result.node_gene_id == node_gene_id
        assert type(result.network) is str
        assert type(result.node_feature_id) is NoneType
        assert type(result.name) is str
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert type(result.x) is float or NoneType
        assert type(result.y) is float or NoneType
