import pytest
from api.database import return_publication_to_gene_to_gene_type_query


@pytest.fixture(scope='module')
def gene_id():
    return 789


def test_PublicationToGeneToGeneType_with_genes(app, gene_id):
    string_representation_list = []
    separator = ', '

    query = return_publication_to_gene_to_gene_type_query('genes')
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        string_representation = '<PublicationToGeneToGeneType %r>' % gene_id
        string_representation_list.append(string_representation)
        assert isinstance(result.genes, list)
        assert len(result.genes) > 0
        # Don't need to iterate through every result.
        for gene in result.genes[0:2]:
            assert gene.id == gene_id
        assert result.gene_id == gene_id
        assert type(result.publication_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_PublicationToGeneToGeneType_with_publications(app, gene_id):
    query = return_publication_to_gene_to_gene_type_query('publications')
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert isinstance(result.publications, list)
        assert len(result.publications) > 0
        # Don't need to iterate through every result.
        for publication in result.publications[0:2]:
            assert type(publication.pubmed_id) is int
        assert result.gene_id == gene_id
        assert type(result.publication_id) is int


def test_PublicationToGeneToGeneType_with_gene_types(app, gene_id):
    query = return_publication_to_gene_to_gene_type_query('gene_types')
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert isinstance(result.gene_types, list)
        assert len(result.gene_types) > 0
        # Don't need to iterate through every result.
        for gene_type in result.gene_types[0:2]:
            assert type(gene_type.name) is str
        assert result.gene_id == gene_id
        assert type(result.gene_type_id) is int


def test_PublicationToGeneToGeneType_no_relations(app, gene_id):
    query = return_publication_to_gene_to_gene_type_query()
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        assert result.genes == []
        assert result.publications == []
        assert result.gene_types == []
        assert result.gene_id == gene_id
        assert type(result.publication_id) is int
        assert type(result.gene_type_id) is int
