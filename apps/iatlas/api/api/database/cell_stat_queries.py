from api import db
from api.db_models import CellStat
from .database_helpers import build_general_query

related_fields = ["data_set", "gene"]

core_fields = ["id", "cell_type", "cell_count", "avg_expr", "perc_expr"]


def return_cell_stat_query(*args):
    return build_general_query(
        CellStat,
        args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields,
    )
