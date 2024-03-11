from sqlalchemy import orm
from api import db
from api.db_models import FeatureToSample
from .database_helpers import build_general_query

related_fields = ['features', 'samples']

core_fields = ['feature_id', 'sample_id', 'value']


def return_feature_to_sample_query(*args):
    return build_general_query(
        FeatureToSample, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
