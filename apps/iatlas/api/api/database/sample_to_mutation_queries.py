from sqlalchemy import orm
from api import db
from api.db_models import SampleToMutation
from .database_helpers import build_general_query

related_fields = ['mutations', 'samples']

core_fields = ['mutation_id', 'sample_id']


def return_sample_to_mutation_query(*args):
    return build_general_query(
        SampleToMutation, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
