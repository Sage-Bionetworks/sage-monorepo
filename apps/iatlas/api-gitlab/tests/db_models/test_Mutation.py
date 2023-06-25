import pytest
from tests import NoneType
from api.database import return_mutation_query
from api.db_models import Mutation


@pytest.fixture(scope='module')
def mutation_entrez_id(test_db):
    return 92


@pytest.fixture(scope='module')
def mutation_gene_id(test_db, mutation_entrez_id):
    from api.db_models import Gene
    (id, ) = test_db.session.query(Gene.id).filter_by(
        entrez_id=mutation_entrez_id).one_or_none()
    return id


def test_Mutation_no_relations(app, mutation_gene_id):
    query = return_mutation_query()
    results = query.filter_by(gene_id=mutation_gene_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.id) is str
        assert type(result.name) is str
        assert type(result.mutation_code) is str
        assert type(result.mutation_type_id) is str

        assert type(result.gene) is NoneType
        assert type(result.mutation_type) is NoneType
        assert result.samples == []

        assert result.gene_id == mutation_gene_id
        assert type(result.gene_id) is str


def test_Mutation_with_relations(app, mutation_entrez_id, mutation_gene_id):
    string_representation_list = []
    separator = ', '
    relationships_to_load = [
        'gene', 'mutation_code', 'mutation_type', 'samples']

    query = return_mutation_query(*relationships_to_load)
    results = query.filter_by(gene_id=mutation_gene_id).limit(3).all()

    assert isinstance(results, list)
    assert len(results) > 0
    for result in results:
        mutation_id = result.id
        string_representation = '<Mutation %r>' % mutation_id
        string_representation_list.append(string_representation)
        assert type(result.name) is str
        assert result.gene.entrez_id == mutation_entrez_id
        assert result.gene.id == mutation_gene_id
        if result.samples:
            assert isinstance(result.samples, list)
            for sample in result.samples[0:2]:
                assert type(sample.id) is str
        assert result.gene_id == mutation_gene_id
        assert type(result.gene_id) is str
        assert type(result.mutation_code) is str
        assert type(result.mutation_type_id) is str
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_Mutation_with_sample_mutation_assoc(app, mutation_gene_id):
    query = return_mutation_query('sample_mutation_assoc')
    result = query.filter_by(gene_id=mutation_gene_id).first()

    if result.sample_mutation_assoc:
        assert isinstance(result.sample_mutation_assoc, list)
        for sample_mutation_rel in result.sample_mutation_assoc[0:2]:
            assert sample_mutation_rel.mutation_id == result.id
