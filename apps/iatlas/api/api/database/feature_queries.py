from sqlalchemy import orm
from api import db
from api.db_models import Feature
from .database_helpers import general_core_fields, build_general_query

feature_related_fields = [
    "copy_number_results",
    "driver_results",
    "feature_sample_assoc",
    "samples",
]

feature_core_fields = [
    "id",
    "name",
    "display",
    "order",
    "unit",
    "feature_class",
    "method_tag",
    "germline_category",
    "germline_module",
]


def return_feature_query(*args):
    return build_general_query(
        Feature,
        args=args,
        accepted_option_args=feature_related_fields,
        accepted_query_args=feature_core_fields,
    )
