from api import db
from api.db_models import Node
from .database_helpers import build_general_query

related_fields = [
    'data_sets', 'edges_primary', 'edges_secondary',
    'feature', 'gene', 'node_tag_assoc', 'tags']

core_fields = ['id', 'dataset_id', 'feature_id',
               'gene_id', 'name', 'network', 'label', 'score', 'x', 'y']


def return_node_query(*args):
    return build_general_query(
        Node, args=args,
        accepted_option_args=related_fields,
        accepted_query_args=core_fields)
