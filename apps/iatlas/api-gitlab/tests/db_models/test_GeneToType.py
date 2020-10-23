import pytest
from api.database import return_gene_to_type_query


@pytest.fixture(scope='module')
def gt_entrez(test_db):
    return 186


@pytest.fixture(scope='module')
def gt_gene_id(test_db, gt_entrez):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(
        entrez=gt_entrez).one_or_none()
    return id


def test_GeneToType_with_relations(app, gt_gene_id):
    relationships_to_join = ['genes', 'types']

    query = return_gene_to_type_query(*relationships_to_join)
    results = query.filter_by(gene_id=gt_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.gene_id == gt_gene_id
        assert isinstance(result.genes, list)
        # Don't need to iterate through every result.
        for gene in result.genes[0:2]:
            assert gene.id == gt_gene_id
            assert type(gene.entrez) is int
        assert isinstance(result.types, list)
        # Don't need to iterate through every result.
        for gene_type in result.types[0:2]:
            assert type(gene_type.name) is str
        assert type(result.type_id) is int
        assert repr(result) == '<GeneToType %r>' % gt_gene_id
    assert repr(results) == '[<GeneToType %r>]' % gt_gene_id


def test_GeneToType_no_relations(app, gt_gene_id):
    query = return_gene_to_type_query()
    results = query.filter_by(gene_id=gt_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.genes == []
        assert result.types == []
        assert result.gene_id == gt_gene_id
        assert type(result.type_id) is int
