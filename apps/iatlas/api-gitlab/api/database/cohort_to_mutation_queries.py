from sqlalchemy import orm
from api import db
from api.db_models import CohortToMutation
from .database_helpers import build_general_query

accepted_cohort_to_mutation_option_args = ['cohort', 'mutation']

accepted_cohort_to_mutation_query_args = [
    'cohort_id', 'mutation_id']


def return_cohort_to_mutation_query(*args):
    return build_general_query(
        CohortToMutation, args=args,
        accepted_option_args=accepted_cohort_to_mutation_option_args,
        accepted_query_args=accepted_cohort_to_mutation_query_args)
