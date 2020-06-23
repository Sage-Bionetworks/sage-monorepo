import pytest
from flaskr.database import return_publication_to_gene_query

gene_id = 1535


def test_PublicationToGene_with_relations(app):
    app()
    string_representation_list = []
    separator = ', '
    relationships_to_load = ['genes', 'publications']

    query = return_publication_to_gene_query(*relationships_to_load)
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<PublicationToGene %r>' % gene_id
        string_representation_list.append(string_representation)
        if result.genes:
            assert isinstance(result.genes, list)
            # Don't need to iterate through every result.
            for gene in result.genes[0:2]:
                assert gene.id == gene_id
        if result.publications:
            assert isinstance(result.publications, list)
            # Don't need to iterate through every result.
            for publication in result.publications[0:2]:
                assert type(publication.pubmed_id) is int
        assert result.gene_id == gene_id
        assert type(result.publication_id) is int
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_PublicationToGene_no_relations(app):
    app()

    query = return_publication_to_gene_query()
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.genes == []
        assert result.publications == []
        assert result.gene_id == gene_id
        assert type(result.publication_id) is int
