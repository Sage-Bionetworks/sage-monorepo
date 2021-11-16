from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Edge, Node
from .paging_utils import get_pagination_queries
from .general_resolvers import build_join_condition, get_selected, get_value

edge_request_fields = {'label',
                       'name',
                       'node1',
                       'node2',
                       'score'}


def build_edge_graphql_response(edge):
    from .node import build_node_graphql_response
    import logging
    logger = logging.getLogger('node response')
    logger.info(edge)
    dict = {
        'id': get_value(edge, 'id'),
        'label': get_value(edge, 'label'),
        'name': get_value(edge, 'name'),
        'score': get_value(edge, 'score'),
        'node1': build_node_graphql_response(prefix='node1_')(edge),
        'node2': build_node_graphql_response(prefix='node2_')(edge)
    }
    logger.info(dict)
    return(dict)


def build_edge_request(requested, node_1_requested, node_2_requested, distinct=False, max_score=None, min_score=None, node_start=None, node_end=None, paging=None):
    '''
    Builds a SQL request.

    All keyword arguments are optional. Keyword arguments are:
        `max_score` - a float, a maximum score value
        `min_score` - a float, a minimum score value
        `node_start` - a list of strings, starting node names
        `node_end` - a list of strings, ending node names
    '''
    from .node import get_node_column_labels
    sess = db.session

    edge_1 = aliased(Edge, name='e')
    node_1 = aliased(Node, name='n1')
    node_2 = aliased(Node, name='n2')

    core_field_mapping = {
        'id': edge_1.id.label('id'),
        'label': edge_1.label.label('label'),
        'name': edge_1.name.label('name'),
        'score': edge_1.score.label('score')}

    core = get_selected(requested, core_field_mapping)
    node_1_core = get_node_column_labels(
        node_1_requested, node_1, prefix='node1_')
    node_2_core = get_node_column_labels(
        node_2_requested, node_2, prefix='node2_')

    query = sess.query(*[*core, *node_1_core, *node_2_core])
    query = query.select_from(edge_1)

    if max_score != None:
        query = query.filter(edge_1.score <= max_score)

    if min_score != None:
        query = query.filter(edge_1.score >= min_score)

    if 'node1' in requested or node_start:
        node_start_join_condition = build_join_condition(
            node_1.id, edge_1.node_1_id, node_1.name, node_start)
        query = query.join(node_1, and_(*node_start_join_condition))

    if 'node2' in requested or node_end:
        node_start_join_condition = build_join_condition(
            node_2.id, edge_1.node_2_id, node_2.name, node_end)
        query = query.join(node_2, and_(*node_start_join_condition))

    return get_pagination_queries(query, paging, distinct, cursor_field=edge_1.id)
