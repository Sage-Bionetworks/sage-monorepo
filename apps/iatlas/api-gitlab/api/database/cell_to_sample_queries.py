from sqlalchemy import orm
from api import db
from api.db_models import CellToSample
from .database_helpers import build_general_query

related_fields = ['sample', 'cell']

core_fields = ['sample_id', 'cell_id']


def return_cell_to_sample_query(*args):
    return build_general_query(
        CellToSample, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)