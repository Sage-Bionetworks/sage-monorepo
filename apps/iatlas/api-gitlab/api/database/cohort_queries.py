from sqlalchemy import orm
from api import db
from .database_helpers import build_general_query
from api.db_models import Cohort

accepted_cohort_option_args = ['name', 'data_set', 'tag', 'clinical']

accepted_cohort_query_args = ['name', 'data_set_id', 'tag_id', 'clinical']


def return_cohort_query(*args):
    return build_general_query(
        Cohort, args=args,
        accepted_option_args=accepted_cohort_option_args,
        accepted_query_args=accepted_cohort_query_args)
