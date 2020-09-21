from itertools import groupby
from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToTag, Edge, Feature, Node, NodeToTag, Tag
from .general_resolvers import build_join_condition, get_selected, get_value

edge_request_fields = {'label',
                       'name',
                       'node1',
                       'node2',
                       'score'}


def build_edge_graphql_response(edge):
    return {
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


def build_edge_request(requested, node_1_requested, node_2_requested, data_set=None, related=None, network=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    data_set_1 = aliased(Dataset, name='d')
    edge_1 = aliased(Edge, name='e')
    node_1 = aliased(Node, name='n1')
    node_2 = aliased(Node, name='n2')

    core_field_mapping = {'label': edge_1.label.label('label'),
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

    if network:
        network_1 = aliased(Tag, name='nt')
        node_to_tag_1 = aliased(NodeToTag, name='ntt')

        network_subquery = sess.query(network_1.id).filter(
            network_1.name.in_(network))

        edge_tag_join_condition = build_join_condition(
            node_to_tag_1.node_id, edge_1.node_1_id, node_to_tag_1.tag_id, network_subquery)
        query = query.join(node_to_tag_1, and_(*edge_tag_join_condition))

    if 'node1' in requested:
        query = query.join(node_1, edge_1.node_1_id == node_1.id)

    if data_set or related or 'dataSet' in requested:
        if 'node1' in requested:
            data_set_join_condition = [data_set_1.id == node_1.dataset_id]
        else:
            node_1_subquery = sess.query(node_1.dataset_id).filter(
                edge_1.node_1_id == node_1.id)
            data_set_join_condition = [data_set_1.id.in_(node_1_subquery)]

        if data_set:
            data_set_join_condition.append(data_set_1.name.in_(data_set))

        is_outer = not bool(data_set)

        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if related:
        data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
        related_tag_1 = aliased(Tag, name='rt')

        related_tag_sub_query = sess.query(related_tag_1.id).filter(
            related_tag_1.name.in_(related))

        data_set_tag_join_condition = build_join_condition(
            data_set_to_tag_1.dataset_id, data_set_1.id, data_set_to_tag_1.tag_id, related_tag_sub_query)
        query = query.join(
            data_set_to_tag_1, and_(*data_set_tag_join_condition))

    if 'node2' in requested:
        query = query.join(node_2, edge_1.node_2_id == node_2.id)

    order = []
    append_to_order = order.append
    if 'name' in requested:
        append_to_order(edge_1.name)
    if 'label' in requested:
        append_to_order(edge_1.label)
    if 'score' in requested:
        append_to_order(edge_1.score)
    if not order:
        append_to_order(edge_1.id)
    query = query.order_by(*order)

    return query.distinct()
