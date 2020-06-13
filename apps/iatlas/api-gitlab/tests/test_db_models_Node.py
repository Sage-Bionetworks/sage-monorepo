import pytest
from tests import app, NoneType
from flaskr.database import return_node_query


def test_Node(app):
    app()
    gene_id = 30749
    string_representation_list = []
    separator = ', '
    relationships_to_load = ['gene', 'feature',
                             'edge_primary', 'edge_secondary']

    query = return_node_query('node_tag_assoc')
    result = query.filter_by(gene_id=gene_id).first()

    if type(result.node_tag_assoc) is not NoneType:
        assert isinstance(result.node_tag_assoc, list)
        # Don't need to iterate through every result.
        for node_tag_rel in result.node_tag_assoc[0:2]:
            assert node_tag_rel.node_id == result.id

    query = return_node_query('tags')
    result = query.filter_by(gene_id=gene_id).first()

    if type(result.tags) is not NoneType:
        assert isinstance(result.tags, list)
        # Don't need to iterate through every result.
        for tag in result.tags[0:2]:
            assert type(tag.name) is str

    query = return_node_query(*relationships_to_load)
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        node_id = result.id
        string_representation = '<Node %r>' % node_id
        string_representation_list.append(string_representation)
        if type(result.gene) is not NoneType:
            assert result.gene.id == result.gene_id
        if type(result.feature) is not NoneType:
            assert result.feature.id == result.feature_id
        if type(result.edges_primary) is not NoneType:
            assert isinstance(result.edges_primary, list)
            # Don't need to iterate through every result.
            for edge_primary in result.edges_primary[0:2]:
                assert edge_primary.node_1_id == result.id
        if type(result.edges_secondary) is not NoneType:
            assert isinstance(result.edges_secondary, list)
            # Don't need to iterate through every result.
            for edge_secondary in result.edges_secondary[0:2]:
                assert edge_secondary.node_2_id == result.id
        assert result.gene_id == gene_id
        assert type(result.feature_id) is NoneType
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert type(result.x) is float or NoneType
        assert type(result.y) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_Node(app):
    app()
    gene_id = 30749
    string_representation_list = []
    separator = ', '

    query = return_node_query()
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.id) is int
        assert result.gene_id == gene_id
        assert type(result.feature_id) is NoneType
        assert type(result.label) is str or NoneType
        assert type(result.score) is float or NoneType
        assert type(result.x) is float or NoneType
        assert type(result.y) is float or NoneType
