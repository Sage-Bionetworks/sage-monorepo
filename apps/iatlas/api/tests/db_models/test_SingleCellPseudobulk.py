import pytest
from api.database import return_single_cell_pseudobulk_query


def test_query_with_no_relations():
    query = return_single_cell_pseudobulk_query()
    results = query.limit(3).all()
    assert isinstance(results, list)
    for result in results:
        assert isinstance(result.id, str)
        assert isinstance(result.cell_type, str)
        assert isinstance(result.gene_id, str)
        assert isinstance(result.sample_id, str)
        assert result.single_cell_seq_sum is not None
        assert repr(result).startswith("<SingleCellPseudobulk")
        assert result.sample is None
        assert result.gene is None


def test_query_with_relations():
    query = return_single_cell_pseudobulk_query("sample", "gene")
    results = query.limit(3).all()
    for result in results:
        assert result.sample.name
        assert result.gene.entrez_id
