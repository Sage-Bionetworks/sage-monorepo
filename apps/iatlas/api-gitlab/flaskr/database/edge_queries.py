from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Edge
from .database_helpers import build_option_args, build_query_args

accepted_option_args = ['node_1', 'node_2']

accepted_query_args = ['id',
                       'node_1_id',
                       'node_2_id',
                       'label',
                       'score']


def return_edge_query(*args):
    option_args = build_option_args(*args, accepted_args=accepted_option_args)
    query_args = build_query_args(
        Edge, * args, accepted_args=accepted_query_args)
    query = db.session.query(*query_args)
    if option_args:
        query = db.session.query(Edge).options(*option_args)
    return query
