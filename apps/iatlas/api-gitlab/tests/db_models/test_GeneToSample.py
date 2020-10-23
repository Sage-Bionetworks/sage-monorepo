import pytest
from tests import NoneType
from api.database import return_gene_to_sample_query


@pytest.fixture(scope='module')
def gs_entrez(test_db):
    return 1


@pytest.fixture(scope='module')
def gs_gene_id(test_db, gs_entrez):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(
        entrez=gs_entrez).one_or_none()
    return id


def test_GeneToSample_with_relations(app, gs_entrez, gs_gene_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['gene', 'sample']

    query = return_gene_to_sample_query(*relationships_to_join)
    results = query.filter_by(gene_id=gs_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<GeneToSample %r>' % gs_gene_id
        string_representation_list.append(string_representation)
        assert result.gene.entrez == gs_entrez
        assert type(result.sample.name) is str
        assert result.gene_id == gs_gene_id
        assert type(result.sample_id) is int
        assert type(result.rna_seq_expr) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_GeneToSample_no_relations(app, gs_gene_id):
    query = return_gene_to_sample_query()
    results = query.filter_by(gene_id=gs_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.gene) is NoneType
        assert type(result.sample) is NoneType
        assert result.gene_id == gs_gene_id
        assert type(result.sample_id) is int
        assert type(result.rna_seq_expr) is float or NoneType
