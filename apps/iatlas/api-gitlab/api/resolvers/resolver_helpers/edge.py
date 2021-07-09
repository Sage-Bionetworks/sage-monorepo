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
    return {
        'id': get_value(edge, 'id'),
        'label': get_value(edge, 'label'),
        'name': get_value(edge, 'name'),
        'node1': {
            'label': get_value(edge, 'n1_label'),
            'name': get_value(edge, 'n1_name'),
            'score': get_value(edge, 'n1_score'),
            'x': get_value(edge, 'n1_x'),
            'y': get_value(edge, 'n1_y')
        },
        'node2': {
            'label': get_value(edge, 'n2_label'),
            'name': get_value(edge, 'n2_name'),
            'score': get_value(edge, 'n2_score'),
            'x': get_value(edge, 'n2_x'),
            'y': get_value(edge, 'n2_y')
        },
        'score': get_value(edge, 'score')
    }


def build_edge_request(requested, node_1_requested, node_2_requested, distinct=False, max_score=None, min_score=None, node_start=None, node_end=None, paging=None):
    '''
    Builds a SQL request.

    All keyword arguments are optional. Keyword arguments are:
        `max_score` - a float, a maximum score value
        `min_score` - a float, a minimum score value
        `node_start` - a list of strings, starting node names
        `node_end` - a list of strings, ending node names
    '''
    sess = db.session

    edge_1 = aliased(Edge, name='e')
    node_1 = aliased(Node, name='n1')
    node_2 = aliased(Node, name='n2')

    core_field_mapping = {
        'id': edge_1.id.label('id'),
        'label': edge_1.label.label('label'),
        'name': edge_1.name.label('name'),
        'score': edge_1.score.label('score')}
    node_1_core_field_mapping = {'label': node_1.label.label('n1_label'),
                                 'name': node_1.name.label('n1_name'),
                                 'score': node_1.score.label('n1_score'),
                                 'x': node_1.x.label('n1_x'),
                                 'y': node_1.y.label('n1_y')}
    node_2_core_field_mapping = {'label': node_2.label.label('n2_label'),
                                 'name': node_2.name.label('n2_name'),
                                 'score': node_2.score.label('n2_score'),
                                 'x': node_2.x.label('n2_x'),
                                 'y': node_2.y.label('n2_y')}

    core = get_selected(requested, core_field_mapping)
    node_1_core = get_selected(node_1_requested, node_1_core_field_mapping)
    node_2_core = get_selected(node_2_requested, node_2_core_field_mapping)

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
