import pytest
from tests import NoneType
from api.database import return_cohort_to_gene_query


def test_CohortToGene_no_relations():
    query = return_cohort_to_gene_query()
    results = query.limit(3).all()

    assert isinstance(results, list)
    for result in results:
        assert type(result.gene_id) is int
        assert type(result.cohort_id) is int


def test_CohortToGene_with_tag_cohort(tcga_tag_cohort_name, tcga_tag_cohort_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['cohort', 'gene']

    query = return_cohort_to_gene_query(*relationships_to_join)
    results = query.filter_by(cohort_id=tcga_tag_cohort_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        id = result.id
        string_representation = '<CohortToGene %r>' % id
        string_representation_list.append(string_representation)
        assert type(result.gene_id) is int
        assert result.cohort_id == tcga_tag_cohort_id
        assert result.cohort.name == tcga_tag_cohort_name
        assert type(result.gene.hgnc) is str
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'


def test_CohortToGene_with_clinical_cohort(pcawg_clinical_cohort_name, pcawg_clinical_cohort_id):
    string_representation_list = []
    separator = ', '
    relationships_to_join = ['cohort', 'gene']

    query = return_cohort_to_gene_query(*relationships_to_join)
    results = query.filter_by(
        cohort_id=pcawg_clinical_cohort_id).limit(3).all()

    assert isinstance(results, list)
    for result in results:
        id = result.id
        string_representation = '<CohortToGene %r>' % id
        string_representation_list.append(string_representation)
        assert type(result.gene_id) is int
        assert result.cohort_id == pcawg_clinical_cohort_id
        assert result.cohort.name == pcawg_clinical_cohort_name
        assert type(result.gene.hgnc) is str
        assert repr(result) == string_representation
    assert repr(results) == '[' + separator.join(
        string_representation_list) + ']'
