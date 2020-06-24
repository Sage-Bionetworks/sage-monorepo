from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import DatasetToSample
from .database_helpers import build_general_query

related_fields = ['datasets', 'samples']

core_fields = ['dataset_id', 'sample_id']


def return_dataset_to_sample_query(*args):
    return build_general_query(
        DatasetToSample, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
