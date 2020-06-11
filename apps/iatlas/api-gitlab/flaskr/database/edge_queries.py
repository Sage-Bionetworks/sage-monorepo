from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Edge
from .database_helpers import build_option_args


def return_edge_query(*args):
    option_args = build_option_args(*args, accepted_args=['node_1', 'node_2'])
    return db.session.query(Edge).options(*option_args)
