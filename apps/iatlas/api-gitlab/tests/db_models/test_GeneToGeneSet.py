import pytest
from api.database import return_gene_to_gene_set_query


@pytest.fixture(scope='module')
def gt_entrez_id(test_db):
    return 186


@pytest.fixture(scope='module')
def gt_gene_id(test_db, gt_entrez_id):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(
        entrez_id=gt_entrez_id).one_or_none()
    return id


def test_GeneToType_with_relations(app, gt_gene_id):
    relationships_to_join = ['genes', 'types']

    query = return_gene_to_gene_set_query(*relationships_to_join)
    results = query.filter_by(gene_id=gt_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.gene_id == gt_gene_id
        assert isinstance(result.genes, list)
        # Don't need to iterate through every result.
        for gene in result.genes[0:2]:
            assert gene.id == gt_gene_id
            assert type(gene.entrez_id) is int
        assert isinstance(result.gene_sets, list)
        # Don't need to iterate through every result.
        for gene_set in result.gene_sets[0:2]:
            assert type(gene_set.name) is str
        assert type(result.gene_set_id) is str
        assert repr(result) == '<GeneToGeneSet %r>' % gt_gene_id
    assert repr(results) == '[<GeneToGeneSet %r>]' % gt_gene_id


def test_GeneToGeneSet_no_relations(app, gt_gene_id):
    query = return_gene_to_gene_set_query()
    results = query.filter_by(gene_id=gt_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.genes == []
        assert result.gene_sets == []
        assert result.gene_id == gt_gene_id
        assert type(result.gene_set_id) is str
        assert type(result.id) is str
