from sqlalchemy import orm
from api import db
from api.db_models import TagToPublication
from .database_helpers import build_general_query

related_fields = ['publications', 'tags']

core_fields = ['publication_id', 'tag_id']


def return_tag_to_publication_query(*args):
    return build_general_query(
        TagToPublication, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
