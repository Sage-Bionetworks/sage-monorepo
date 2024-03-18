import pytest
from api.database import return_single_cell_pseudobulk_feature_query

def test_query_with_no_relations():
    query = return_single_cell_pseudobulk_feature_query()
    results = query.limit(3).all()
    assert isinstance(results, list)
    for result in results:
        assert isinstance(result.id, str)
        assert isinstance(result.cell_type, str)
        assert isinstance(result.sample_id, str)
        assert isinstance(result.feature_id, str)
        assert result.value is not None
        assert repr(result).startswith("<SingleCellPseudobulkFeature")
        assert result.sample is None
        assert result.feature is None

def test_query_with_relations():
    query = return_single_cell_pseudobulk_feature_query("sample", "feature")
    results = query.limit(3).all()
    for result in results:
        assert result.sample.name
        assert result.feature.name