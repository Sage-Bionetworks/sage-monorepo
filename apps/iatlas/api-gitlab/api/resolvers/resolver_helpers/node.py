from threading import Thread
from itertools import groupby
from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToSample, DatasetToTag, Feature, FeatureToSample, Gene, GeneToSample, Node, NodeToTag, SampleToTag, Tag, TagToTag
from .general_resolvers import build_join_condition, get_selected, get_value
from .data_set import build_data_set_graphql_response
from .feature import build_feature_graphql_response
from .gene import build_gene_graphql_response
from .tag import build_tag_graphql_response
import logging

log = logging.getLogger('node resolver helper')
log.setLevel(logging.DEBUG)

node_request_fields = {'dataSet',
                       'feature',
                       'gene',
                       'label',
                       'name',
                       'score',
                       'tags',
                       'x',
                       'y'}


def build_node_graphql_response(tag_dict):
    def f(node):
        node_id = get_value(node, 'id')
        tags = tag_dict.get(node_id, []) if tag_dict else []
        return {
            'dataSet': build_data_set_graphql_response(node),
            'feature': build_feature_graphql_response()(node),
            'gene': build_gene_graphql_response()(node),
            'label': get_value(node, 'label'),
            'name': get_value(node, 'node_name'),
            'score': get_value(node, 'score'),
            'tags': map(build_tag_graphql_response(), tags),
            'x': get_value(node, 'x'),
            'y': get_value(node, 'y')
        }
    return f


def build_node_request(requested, data_set_requested, feature_requested, gene_requested, data_set=None, related=None, network=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    data_set_1 = aliased(Dataset, name='d')
    feature_1 = aliased(Feature, name='f')
    gene_1 = aliased(Gene, name='g')
    node_1 = aliased(Node, name='n')

    core_field_mapping = {'label': node_1.label.label('label'),
                          'name': node_1.name.label('node_name'),
                          'score': node_1.score.label('score'),
                          'x': node_1.x.label('x'),
                          'y': node_1.y.label('y')}

    data_set_field_mapping = {'display': data_set_1.display.label('data_set_display'),
                              'name': data_set_1.name.label('data_set_name')}

    feature_field_mapping = {'display': feature_1.display.label('feature_display'),
                             'name': feature_1.name.label('feature_name'),
                             'order': feature_1.order.label('order'),
                             'unit': feature_1.unit.label('unit')}

    gene_field_mapping = {'entrez': gene_1.entrez.label('entrez'),
                          'hgnc': gene_1.hgnc.label('hgnc'),
                          'description': gene_1.description.label('description'),
                          'friendlyName': gene_1.friendly_name.label('friendly_name'),
                          'ioLandscapeName': gene_1.io_landscape_name.label('io_landscape_name')}

    core = get_selected(requested, core_field_mapping)
    data_set_core = get_selected(data_set_requested, data_set_field_mapping)
    feature_core = get_selected(feature_requested, feature_field_mapping)
    gene_core = get_selected(gene_requested, gene_field_mapping)

    # Always get the node id
    core |= {node_1.id.label('id')}

    query = sess.query(*[*core, *data_set_core, *feature_core, *gene_core])
    query = query.select_from(node_1)

    if network:
        network_1 = aliased(Tag, name='nt')
        node_to_tag_1 = aliased(NodeToTag, name='ntt')

        network_subquery = sess.query(network_1.id).filter(
            network_1.name.in_(network))

        node_tag_join_condition = build_join_condition(
            node_to_tag_1.node_id, node_1.id, node_to_tag_1.tag_id, network_subquery)
        query = query.join(node_to_tag_1, and_(*node_tag_join_condition))

    if data_set or related or 'dataSet' in requested:
        data_set_join_condition = build_join_condition(
            data_set_1.id, node_1.dataset_id, data_set_1.name, data_set)
        query = query.join(data_set_1, and_(*data_set_join_condition))

    if related:
        data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
        related_tag_1 = aliased(Tag, name='rt')

        related_tag_sub_query = sess.query(related_tag_1.id).filter(
            related_tag_1.name.in_(related))

        data_set_tag_join_condition = build_join_condition(
            data_set_to_tag_1.dataset_id, data_set_1.id, data_set_to_tag_1.tag_id, related_tag_sub_query)
        query = query.join(
            data_set_to_tag_1, and_(*data_set_tag_join_condition))

    if 'feature' in requested:
        query = query.outerjoin(feature_1, feature_1.id == node_1.feature_id)

    if 'gene' in requested:
        query = query.outerjoin(gene_1, gene_1.id == node_1.gene_id)

    order = []
    append_to_order = order.append
    if 'name' in requested:
        append_to_order(node_1.name)
    if 'label' in requested:
        append_to_order(node_1.label)
    if 'score' in requested:
        append_to_order(node_1.score)
    if 'x' in requested:
        append_to_order(node_1.x)
    if 'y' in requested:
        append_to_order(node_1.y)
    if not order:
        append_to_order(node_1.id)
    query = query.order_by(*order)

    return query.distinct()


def build_tags_request(requested, tag_requested, data_set=None, related=None, network=None):
    if 'tags' in requested:
        sess = db.session

        data_set_1 = aliased(Dataset, name='d')
        network_tag_1 = aliased(Tag, name='nt')
        node_1 = aliased(Node, name='n')
        node_to_tag_1 = aliased(NodeToTag, name='ntt1')
        node_to_tag_2 = aliased(NodeToTag, name='ntt2')
        tag_1 = aliased(Tag, name='t')
        tag_to_tag_1 = aliased(TagToTag, name='tt')

        tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                                  'color': tag_1.color.label('color'),
                                  'display': tag_1.display.label('tag_display'),
                                  'name': tag_1.name.label('name')}

        tag_core = get_selected(tag_requested, tag_core_field_mapping)

        # Always select the tag id and the node id.
        tag_core |= {tag_1.id.label('id'), node_1.id.label('node_id')}

        tag_query = sess.query(*tag_core)
        tag_query = tag_query.select_from(node_1)

        if data_set or related or 'dataSet' in requested:
            data_set_join_condition = build_join_condition(
                data_set_1.id, node_1.dataset_id, data_set_1.name, data_set)
            tag_query = tag_query.join(
                data_set_1, and_(*data_set_join_condition))

        if related:
            data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
            related_tag_1 = aliased(Tag, name='rt')

            related_tag_sub_query = sess.query(related_tag_1.id).filter(
                related_tag_1.name.in_(related))

            data_set_tag_join_condition = build_join_condition(
                data_set_to_tag_1.dataset_id, data_set_1.id, data_set_to_tag_1.tag_id, related_tag_sub_query)
            tag_query = tag_query.join(
                data_set_to_tag_1, and_(*data_set_tag_join_condition))

        network_subquery = sess.query(network_tag_1.id).filter(
            network_tag_1.name.in_(network)) if network else None

        node_tag_join_condition = build_join_condition(
            node_to_tag_1.node_id, node_1.id, node_to_tag_1.tag_id, network_subquery)
        tag_query = tag_query.join(
            node_to_tag_1, and_(*node_tag_join_condition))

        node_tag_join_condition = [
            node_to_tag_2.node_id == node_1.id, node_to_tag_2.tag_id.notin_(network_subquery)]
        tag_query = tag_query.join(
            node_to_tag_2, and_(*node_tag_join_condition))

        network_tag_subquery = sess.query(
            network_tag_1.id).filter(network_tag_1.name == 'network')

        tag_to_tag_join_condition = [
            tag_to_tag_1.tag_id == node_to_tag_2.tag_id, tag_to_tag_1.related_tag_id.notin_(network_tag_subquery)]

        tag_query = tag_query.join(tag_to_tag_1, and_(
            *tag_to_tag_join_condition))

        tag_query = tag_query.join(tag_1, tag_to_tag_1.tag_id == tag_1.id)

        order = [node_1.id]
        append_to_order = order.append
        if 'name' in tag_requested:
            append_to_order(tag_1.name)
        if 'display' in tag_requested:
            append_to_order(tag_1.display)
        if 'color' in tag_requested:
            append_to_order(tag_1.color)
        if 'characteristics' in tag_requested:
            append_to_order(tag_1.characteristics)
        tag_query = tag_query.order_by(*order)

        return tag_query.distinct().yield_per(1000).all()
    return []


def return_node_derived_fields(requested, tag_requested, data_set=None, network=None, related=None):
    tag_results = build_tags_request(
        requested, tag_requested, data_set=data_set, related=related, network=network)
    # tag_results = []
    tag_dict = dict()
    for key, collection in groupby(tag_results, key=lambda t: t.node_id):
        tag_dict[key] = tag_dict.get(key, []) + list(collection)

    return tag_dict
