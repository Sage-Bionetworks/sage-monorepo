from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import FeatureToSample
from .database_helpers import build_general_query

related_fields = ['features', 'samples']

core_fields = ['feature_id', 'sample_id', 'value', 'inf_value']


def return_feature_to_sample_query(*args):
    return build_general_query(
        FeatureToSample, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
