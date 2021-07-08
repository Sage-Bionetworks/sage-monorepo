from sqlalchemy import orm
from api import db
from api.db_models import CohortToTag
from .database_helpers import build_general_query

accepted_cohort_to_tag_option_args = ['cohort', 'tag']

accepted_cohort_to_tag_query_args = [
    'cohort_id', 'tag_id']


def return_cohort_to_tag_query(*args):
    return build_general_query(
        CohortToTag, args=args,
        accepted_option_args=accepted_cohort_to_tag_option_args,
        accepted_query_args=accepted_cohort_to_tag_query_args)
