import pytest
from api.database import return_cell_to_gene_query


def test_query_with_no_relations():
    query = return_cell_to_gene_query()
    results = query.limit(3).all()
    assert isinstance(results, list)
    for result in results:
        assert isinstance(result.id, str)
        assert isinstance(result.cell_id, str)
        assert isinstance(result.gene_id, str)
        assert result.single_cell_seq
        assert repr(result).startswith("<CellToGene")
        assert result.gene is None
        assert result.cell is None


def test_query_with_relations():
    query = return_cell_to_gene_query("gene", "cell")
    results = query.limit(3).all()
    for result in results:
        assert result.gene.entrez_id
        assert result.cell.name
