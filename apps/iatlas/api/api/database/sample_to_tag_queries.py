from sqlalchemy import orm
from api import db
from api.db_models import SampleToTag
from .database_helpers import build_general_query

related_fields = ['samples', 'tags']

core_fields = ['sample_id', 'tag_id']


def return_sample_to_tag_query(*args):
    return build_general_query(
        SampleToTag, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
