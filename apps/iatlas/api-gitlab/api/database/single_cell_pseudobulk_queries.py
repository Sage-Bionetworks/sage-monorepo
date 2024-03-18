from sqlalchemy import orm
from api import db
from api.db_models import SingleCellPseudobulk
from .database_helpers import build_general_query

related_fields = ['gene', 'sample']

core_fields = ['id', 'gene_id', 'sample_id', 'single_cell_seq_sum', 'cell_type']


def return_single_cell_pseudobulk_query(*args):
    return build_general_query(
        SingleCellPseudobulk, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)