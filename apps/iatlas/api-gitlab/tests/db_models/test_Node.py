import pytest
from tests import NoneType
from api.database import return_node_query


@pytest.fixture(scope='module')
def node_entrez(test_db):
    return 5817


@pytest.fixture(scope='module')
def node_gene_id(test_db, node_entrez):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(
        entrez=node_entrez).one_or_none()
    return id


def test_Node_with_relations(app, node_entrez, node_gene_id):
    string_representation_list = []
    separator = ', '

    relationships_to_load = ['data_set', 'gene', 'feature']
    query = return_node_query(*relationships_to_load)
    results = query.filter_by(gene_id=node_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        node_id = result.id
        string_representation = '<Node %r>' % node_id
        string_representation_list.append(string_representation)
        if result.data_set:
            assert result.data_set.id == result.dataset_id
        assert result.gene.entrez == node_entrez
        assert result.gene.id == result.gene_id
        if result.feature:
            assert result.feature.id == result.feature_id
        assert result.edges_primary == []
        assert result.edges_secondary == []
        assert result.node_tag_assoc == []
        assert result.tags == []
        assert result.gene_id == node_gene_id
        assert type(result.feature_id) is NoneType
        assert type(result.name) is str
        assert type(result.network) is str
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert type(result.x) is float or NoneType
        assert type(result.y) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_Node_with_node_tag_assoc(app, node_gene_id):
    query = return_node_query('node_tag_assoc')
    result = query.filter_by(gene_id=node_gene_id).first()

    if result.node_tag_assoc:
        assert isinstance(result.node_tag_assoc, list)
        # Don't need to iterate through every result.
        for node_tag_rel in result.node_tag_assoc[0:2]:
            assert node_tag_rel.node_id == result.id


def test_Node_with_edges_primary(app, node_gene_id):
    query = return_node_query('edges_primary')
    result = query.filter_by(gene_id=node_gene_id).first()

    if result.edges_primary:
        assert isinstance(result.edges_primary, list)
        # Don't need to iterate through every result.
        for edge_primary in result.edges_primary[0:2]:
            assert edge_primary.node_1_id == result.id


def test_Node_with_edges_secondary(app, node_gene_id):
    query = return_node_query('edges_secondary')
    result = query.filter_by(gene_id=node_gene_id).first()

    if result.edges_secondary:
        assert isinstance(result.edges_secondary, list)
        # Don't need to iterate through every result.
        for edge_secondary in result.edges_secondary[0:2]:
            assert edge_secondary.node_2_id == result.id


def test_Node_with_tags(app, node_gene_id):
    query = return_node_query('tags')
    result = query.filter_by(gene_id=node_gene_id).first()

    if result.tags:
        assert isinstance(result.tags, list)
        # Don't need to iterate through every result.
        for tag in result.tags[0:2]:
            assert type(tag.name) is str


def test_Node_no_relations(app, node_gene_id):
    query = return_node_query()
    results = query.filter_by(gene_id=node_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.gene) is NoneType
        assert type(result.feature) is NoneType
        assert result.edges_primary == []
        assert result.edges_secondary == []
        assert result.node_tag_assoc == []
        assert result.tags == []
        assert type(result.id) is int
        assert type(result.dataset_id) is int or NoneType
        assert result.gene_id == node_gene_id
        assert type(result.network) is str
        assert type(result.feature_id) is NoneType
        assert type(result.name) is str
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert type(result.x) is float or NoneType
        assert type(result.y) is float or NoneType
