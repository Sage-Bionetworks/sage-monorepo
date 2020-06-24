from sqlalchemy import orm
from api import db
from api.db_models import NodeToTag
from .database_helpers import build_general_query

related_fields = ['nodes', 'tags']

core_fields = ['node_id', 'tag_id']


def return_node_to_tag_query(*args):
    return build_general_query(
        NodeToTag, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
