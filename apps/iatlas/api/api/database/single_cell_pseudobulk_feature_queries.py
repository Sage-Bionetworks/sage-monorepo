from sqlalchemy import orm
from api import db
from api.db_models import SingleCellPseudobulkFeature
from .database_helpers import build_general_query

related_fields = ["feature", "sample"]

core_fields = ["id", "feature_id", "sample_id", "value", "cell_type"]


def return_single_cell_pseudobulk_feature_query(*args):
    return build_general_query(
        SingleCellPseudobulkFeature,
        args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields,
    )
