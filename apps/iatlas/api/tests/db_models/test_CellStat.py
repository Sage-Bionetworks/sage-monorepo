import pytest
from tests import NoneType
from api.database import return_cell_stat_query


def test_cell_stat_query_no_relations():
    query = return_cell_stat_query()
    results = query.limit(3).all()
    assert isinstance(results, list)
    for cell in results:
        assert isinstance(cell.id, str)
        assert isinstance(cell.cell_type, str)
        assert cell.cell_count is None or isinstance(cell.cell_count, int)
        assert type(cell.avg_expr) is float or NoneType
        assert type(cell.perc_expr) is float or NoneType
        assert repr(cell).startswith("<CellStat")
        assert cell.gene is None
        assert cell.data_set is None


def test_cell_stat_query_with_relations():
    query = return_cell_stat_query("gene", "data_set")
    results = query.limit(3).all()
    for cell in results:
        assert cell.gene.entrez_id
        assert cell.data_set.name
