from sqlalchemy import orm
from api import db
from api.db_models import CellToFeature
from .database_helpers import build_general_query

related_fields = ["feature", "cell"]

core_fields = ["feature_id", "cell_id", "feature_value"]


def return_cell_to_feature_query(*args):
    return build_general_query(
        CellToFeature,
        args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields,
    )
