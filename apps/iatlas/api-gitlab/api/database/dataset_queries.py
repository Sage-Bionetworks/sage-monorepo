from sqlalchemy import orm
from api import db
from api.db_models import Dataset
from .database_helpers import general_core_fields, build_general_query

dataset_related_fields = [
    'dataset_sample_assoc', 'dataset_tag_assoc', 'samples', 'tags']

dataset_core_fields = ['id', 'name', 'display']


def return_dataset_query(*args, model=Dataset):
    return build_general_query(
        model, args=args,
        accepted_option_args=dataset_related_fields,
        accepted_query_args=dataset_core_fields)
