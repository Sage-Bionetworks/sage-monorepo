from sqlalchemy import orm
from api import db
from api.db_models import Edge
from .database_helpers import build_general_query

accepted_option_args = ['node_1', 'node_2']

accepted_query_args = ['id', 'node_1_id',
                       'node_2_id', 'name', 'label', 'score']


def return_edge_query(*args):
    return build_general_query(
        Edge, args=args, accepted_option_args=accepted_option_args,
        accepted_query_args=accepted_query_args)
