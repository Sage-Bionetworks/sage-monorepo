import pytest
from tests import NoneType
from flaskr.database import return_gene_to_sample_query


@pytest.fixture(scope='module')
def gene_id():
    return 1


def test_GeneToSample_with_relations(app, gene_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['genes', 'samples']

    query = return_gene_to_sample_query(*relationships_to_join)
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        string_representation = '<GeneToSample %r>' % gene_id
        string_representation_list.append(string_representation)
        assert isinstance(result.genes, list)
        # Don't need to iterate through every result.
        for gene in result.genes[0:2]:
            assert type(gene.entrez) is int
        assert isinstance(result.samples, list)
        # Don't need to iterate through every result.
        for sample in result.samples[0:2]:
            assert type(sample.name) is str
        assert result.gene_id == gene_id
        assert type(result.sample_id) is int
        assert type(result.rna_seq_expr) is float or NoneType
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_GeneToSample_no_relations(app, gene_id):
    query = return_gene_to_sample_query()
    results = query.filter_by(gene_id=gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert result.genes == []
        assert result.samples == []
        assert result.gene_id == gene_id
        assert type(result.sample_id) is int
        assert type(result.rna_seq_expr) is float or NoneType
