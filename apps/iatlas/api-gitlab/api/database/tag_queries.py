from sqlalchemy import orm
from api import db
from api.db_models import Tag
from .database_helpers import build_general_query

related_fields = ['copy_number_results',
                  'driver_results',
                  'node_tag_assoc',
                  'related_tags',
                  'sample_tag_assoc',
                  'samples',
                  'tags']

core_fields = ['id', 'name', 'characteristics', 'display', 'color']


def return_tag_query(*args, model=Tag):
    return build_general_query(
        model, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
