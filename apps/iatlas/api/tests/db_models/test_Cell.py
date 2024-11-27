import pytest
from api.database import return_cell_query


def test_cell_query():
    query = return_cell_query()
    results = query.limit(3).all()
    assert isinstance(results, list)
    assert len(results) == 3
    for cell in results:
        assert isinstance(cell.name, str)
        assert isinstance(cell.id, str)
        assert isinstance(cell.cell_type, str)
        assert repr(cell).startswith("<Cell")
