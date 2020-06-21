from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Dataset
from .database_helpers import general_core_fields, build_general_query

dataset_related_fields = ['dataset_sample_assoc', 'samples']

dataset_core_fields = ['id', 'name', 'display']


def return_dataset_query(*args):
    return build_general_query(
        Dataset, args=args,
        accepted_option_args=dataset_related_fields,
        accepted_query_args=dataset_core_fields)
