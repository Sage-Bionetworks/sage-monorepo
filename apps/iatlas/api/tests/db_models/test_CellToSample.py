import pytest
from api.database import return_cell_to_sample_query


def test_query_with_no_relations():
    query = return_cell_to_sample_query()
    results = query.limit(3).all()
    assert isinstance(results, list)
    for result in results:
        assert isinstance(result.id, str)
        assert isinstance(result.cell_id, str)
        assert isinstance(result.sample_id, str)
        assert repr(result).startswith("<CellToSample")
        assert result.sample is None
        assert result.cell is None


def test_query_with_relations():
    query = return_cell_to_sample_query("sample", "cell")
    results = query.limit(3).all()
    for result in results:
        assert result.sample.name
        assert result.cell.name
