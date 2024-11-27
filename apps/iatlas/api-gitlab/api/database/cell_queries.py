from sqlalchemy import orm
from api import db
from api.db_models import Cell
from .database_helpers import general_core_fields, build_general_query

cell_core_fields = [
    'id', 'name', 'cell_type']

def return_cell_query(*args):
    return build_general_query(
        Cell, args=args,
        accepted_query_args=cell_core_fields)