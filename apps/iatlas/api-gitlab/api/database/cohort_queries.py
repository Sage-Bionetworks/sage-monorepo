from sqlalchemy import orm
from api import db
from .database_helpers import build_general_query
from api.db_models import Cohort

cohort_related_fields = ['data_set', 'tag',
                         'samples', 'features', 'mutations', 'genes']

cohort_core_fields = ['id', 'name', 'dataset_id', 'tag_id']


def return_cohort_query(*args):
    return build_general_query(
        Cohort, args=args,
        accepted_option_args=cohort_related_fields,
        accepted_query_args=cohort_core_fields)
