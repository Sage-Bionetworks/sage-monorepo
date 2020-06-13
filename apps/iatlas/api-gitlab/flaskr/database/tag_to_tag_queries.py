from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import TagToTag
from .database_helpers import build_general_query

related_fields = ['related_tags', 'tags']

core_fields = ['related_tag_id', 'tag_id']


def return_tag_to_tag_query(*args):
    return build_general_query(
        TagToTag, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
