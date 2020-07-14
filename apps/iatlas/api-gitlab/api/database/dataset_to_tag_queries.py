from sqlalchemy import orm
from api import db
from api.db_models import DatasetToTag
from .database_helpers import build_general_query

related_fields = ['data_sets', 'tags']

core_fields = ['dataset_id', 'tag_id']


def return_dataset_to_tag_query(*args):
    return build_general_query(
        DatasetToTag, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
