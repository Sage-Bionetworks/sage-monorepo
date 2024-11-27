import pytest
from api.database import return_publication_to_gene_to_gene_set_query


@pytest.fixture(scope='module')
def pggt_entrez_id(test_db):
    return 958


@pytest.fixture(scope='module')
def pggt_gene_id(test_db, pggt_entrez_id):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(
        entrez_id=pggt_entrez_id).one_or_none()
    return id


def test_PublicationToGeneToGeneSet_with_genes(app, pggt_entrez_id, pggt_gene_id):
    string_representation_list = []
    separator = ', '

    query = return_publication_to_gene_to_gene_set_query('genes')
    results = query.filter_by(gene_id=pggt_gene_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        string_representation = '<PublicationToGeneToGeneSet %r>' % pggt_gene_id
        string_representation_list.append(string_representation)
        assert isinstance(result.genes, list)
        assert len(result.genes) > 0
        # Don't need to iterate through every result.
        for gene in result.genes[0:2]:
            assert gene.entrez_id == pggt_entrez_id
            assert gene.id == pggt_gene_id
        assert result.gene_id == pggt_gene_id
        assert type(result.publication_id) is str
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_PublicationToGeneToGeneSet_with_publications(app, pggt_gene_id):
    query = return_publication_to_gene_to_gene_set_query('publications')
    results = query.filter_by(gene_id=pggt_gene_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert isinstance(result.publications, list)
        assert len(result.publications) > 0
        # Don't need to iterate through every result.
        for publication in result.publications[0:2]:
            assert type(publication.pubmed_id) is int
        assert result.gene_id == pggt_gene_id
        assert type(result.publication_id) is str


def test_PublicationToGeneToGeneSet_with_gene_types(app, pggt_gene_id):
    query = return_publication_to_gene_to_gene_set_query('gene_sets')
    results = query.filter_by(gene_id=pggt_gene_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert isinstance(result.gene_sets, list)
        assert len(result.gene_sets) > 0
        # Don't need to iterate through every result.
        for gene_set in result.gene_sets[0:2]:
            assert type(gene_set.name) is str
        assert result.gene_id == pggt_gene_id
        assert type(result.gene_set_id) is str


def test_PublicationToGeneToGeneSet_no_relations(app, pggt_gene_id):
    query = return_publication_to_gene_to_gene_set_query()
    results = query.filter_by(gene_id=pggt_gene_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result.genes == []
        assert result.publications == []
        assert result.gene_sets == []
        assert result.gene_id == pggt_gene_id
        assert type(result.publication_id) is str
        assert type(result.gene_set_id) is str
