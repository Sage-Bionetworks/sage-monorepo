from sqlalchemy import orm
from flaskr import db
from flaskr.db_models import Node
from .database_helpers import build_general_query

related_fields = ['gene', 'feature', 'edges_primary', 'edges_secondary']

core_fields = ['id', 'gene_id', 'label', 'score', 'x', 'y']


def return_node_query(*args):
    return build_general_query(
        Node, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
