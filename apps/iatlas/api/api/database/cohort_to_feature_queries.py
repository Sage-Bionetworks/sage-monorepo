from sqlalchemy import orm
from api import db
from api.db_models import CohortToFeature
from .database_helpers import build_general_query

accepted_cohort_to_feature_option_args = ['cohort', 'feature']

accepted_cohort_to_feature_query_args = [
    'cohort_id', 'feature_id']


def return_cohort_to_feature_query(*args):
    return build_general_query(
        CohortToFeature, args=args,
        accepted_option_args=accepted_cohort_to_feature_option_args,
        accepted_query_args=accepted_cohort_to_feature_query_args)
