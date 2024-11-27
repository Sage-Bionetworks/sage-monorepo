import pytest
from api.database import return_cell_to_feature_query

def test_query_with_no_relations():
    query = return_cell_to_feature_query()
    results = query.limit(3).all()
    assert isinstance(results, list)
    for result in results:
        assert isinstance(result.id, str)
        assert isinstance(result.cell_id, str)
        assert isinstance(result.feature_id, str)
        assert result.feature_value
        assert repr(result).startswith("<CellToFeature")
        assert result.feature is None
        assert result.cell is None

def test_query_with_relations():
    query = return_cell_to_feature_query("feature", "cell")
    results = query.limit(3).all()
    for result in results:
        assert result.feature.name
        assert result.cell.name