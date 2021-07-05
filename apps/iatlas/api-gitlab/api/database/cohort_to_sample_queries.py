from sqlalchemy import orm
from api import db
from api.db_models import CohortToSample
from .database_helpers import build_general_query

accepted_cohort_to_sample_option_args = ['cohort', 'sample']

accepted_cohort_to_sample_query_args = [
    'cohort_id', 'sample_id' 'tag_id']


def return_cohort_to_sample_query(*args):
    return build_general_query(
        CohortToSample, args=args,
        accepted_option_args=accepted_cohort_to_sample_option_args,
        accepted_query_args=accepted_cohort_to_sample_query_args)
