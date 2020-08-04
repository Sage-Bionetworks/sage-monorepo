from threading import Thread
from itertools import groupby
from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToSample, DatasetToTag, Feature, FeatureToSample, Gene, GeneToSample, Node, NodeToTag, SampleToTag, Tag, TagToTag
from .data_set import build_data_set_graphql_response
from .feature import build_feature_graphql_response
from .gene import build_gene_graphql_response
from .general_resolvers import build_join_condition, build_option_args, get_selected, get_selection_set, get_value
from .tag import build_tag_graphql_response

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
            'dataSet': {
                'display': get_value(node, 'data_set_display'),
                'name': get_value(node, 'data_set_name')
            },
            'feature': {
                'display': get_value(node, 'feature_display'),
                'name': get_value(node, 'feature_name'),
                'order': get_value(node, 'order'),
                'unit': get_value(node, 'unit')
            },
            'gene': {
                'entrez': get_value(node, 'entrez'),
                'hgnc': get_value(node, 'hgnc'),
                'description': get_value(node, 'description'),
                'friendlyName': get_value(node, 'friendly_name'),
                'ioLandscapeName': get_value(node, 'io_landscape_name')
            },
            'label': get_value(node, 'label'),
            'name': get_value(node, 'name'),
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

    core_field_mapping = {'dataSetId': node_1.dataset_id.label('dataset_id'),
                          'featureId': node_1.feature_id.label('feature_id'),
                          'geneId': node_1.gene_id.label('gene_id'),
                          'label': node_1.label.label('label'),
                          'name': node_1.name.label('name'),
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
    core.add(node_1.id.label('id'))

    if 'dataSet' in requested:
        core |= get_selected(data_set_requested, data_set_field_mapping)

    if 'feature' in requested:
        core |= get_selected(feature_requested, feature_field_mapping)

    if 'gene' in requested:
        core |= get_selected(gene_requested, gene_field_mapping)

    query = sess.query(*core)
    query = query.select_from(node_1)

    if 'feature' in requested:
        query = query.outerjoin(feature_1, feature_1.id == node_1.feature_id)

    if 'gene' in requested:
        query = query.outerjoin(gene_1, gene_1.id == node_1.gene_id)

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
    if 'data_set_id' in requested:
        append_to_order(node_1.y)
    if not order:
        append_to_order(node_1.id)
    query = query.order_by(*order)

    return query.distinct()


def build_tags_request(tag_requested, data_set=None, related=None, network=None):
    sess = db.session

    network_tag_1 = aliased(Tag, name='nt')
    node_to_tag_1 = aliased(NodeToTag, name='ntt')
    tag_1 = aliased(Tag, name='t')
    tag_to_tag_1 = aliased(TagToTag, name='tt')

    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                              'color': tag_1.color.label('color'),
                              'display': tag_1.display.label('tag_display'),
                              'name': tag_1.name.label('name')}

    tag_core = get_selected(tag_requested, tag_core_field_mapping)

    # Always select the tag id.
    tag_core.add(tag_1.id.label('id'))

    tag_query = sess.query(*tag_core)
    tag_query = tag_query.select_from(tag_1)

    network_tag_subquery = sess.query(
        network_tag_1.id).filter(network_tag_1.name == 'network')

    tag_to_tag_join_condition = [
        tag_to_tag_1.tag_id == tag_1.id, tag_to_tag_1.related_tag_id.notin_(network_tag_subquery)]

    if data_set or related:
        data_set_to_sample_1 = aliased(DatasetToSample, name='ds')
        sample_to_tag_1 = aliased(SampleToTag, name='st')

        tag_query = tag_query.join(
            sample_to_tag_1, sample_to_tag_1.tag_id == tag_1.id)

        if data_set or related:
            data_set_1 = aliased(Dataset, name='d')

            data_set_sub_query = sess.query(data_set_1.id).filter(
                data_set_1.name.in_(data_set)) if data_set else data_set

            data_set_to_sample_join_condition = build_join_condition(
                data_set_to_sample_1.sample_id, sample_to_tag_1.sample_id, data_set_to_sample_1.dataset_id, data_set_sub_query)
            tag_query = tag_query.join(
                data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

        if related:
            data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
            related_tag_1 = aliased(Tag, name='rt')

            related_tag_sub_query = sess.query(related_tag_1.id).filter(
                related_tag_1.name.in_(related))

            data_set_tag_join_condition = build_join_condition(
                data_set_to_tag_1.dataset_id, data_set_to_sample_1.dataset_id, data_set_to_tag_1.tag_id, related_tag_sub_query)
            tag_query = tag_query.join(
                data_set_to_tag_1, and_(*data_set_tag_join_condition))

            tag_to_tag_join_condition.append(
                tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id)

    tag_query = tag_query.join(tag_to_tag_1, and_(
        *tag_to_tag_join_condition))

    tag_query = tag_query.distinct().subquery()

    node_to_tag_selection = {node_to_tag_1.node_id.label('node_id')}
    add_to_selection = node_to_tag_selection.add
    if 'characteristics' in tag_requested:
        add_to_selection(tag_query.c.characteristics.label('characteristics'))
    if 'color' in tag_requested:
        add_to_selection(tag_query.c.color.label('color'))
    if 'display' in tag_requested:
        add_to_selection(tag_query.c.tag_display.label('tag_display'))
    if 'name' in tag_requested:
        add_to_selection(tag_query.c.name.label('name'))

    node_to_tag_query = sess.query(*node_to_tag_selection)
    node_to_tag_query = node_to_tag_query.select_from(node_to_tag_1)

    node_to_tag_query = node_to_tag_query.join(
        tag_query, node_to_tag_1.tag_id == tag_query.c.id)

    order = [node_to_tag_1.node_id]
    append_to_order = order.append
    if 'name' in tag_requested:
        append_to_order(tag_query.c.name)
    if 'display' in tag_requested:
        append_to_order(tag_query.c.tag_display)
    if 'color' in tag_requested:
        append_to_order(tag_query.c.color)
    if 'characteristics' in tag_requested:
        append_to_order(tag_query.c.characteristics)
    node_to_tag_query = node_to_tag_query.order_by(*order)

    return node_to_tag_query.distinct()


def return_node_derived_fields(tag_requested, data_set=None, network=None, related=None):
    tag_dict = dict()
    for key, collection in groupby(build_tags_request(tag_requested, data_set=data_set, related=related, network=network).yield_per(1000).all(), key=lambda t: t.node_id):
        tag_dict[key] = tag_dict.get(key, []) + list(collection)

    return tag_dict
