from sqlalchemy import orm
from api import db
from api.db_models import CellToGene
from .database_helpers import build_general_query

related_fields = ['gene', 'cell']

core_fields = ['gene_id', 'cell_id', 'single_cell_seq']


def return_cell_to_gene_query(*args):
    return build_general_query(
        CellToGene, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)