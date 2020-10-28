from threading import Thread
from itertools import groupby
from sqlalchemy import and_, or_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToSample, DatasetToTag, Feature, FeatureToSample, Gene, GeneToSample, Node, NodeToTag, SampleToTag, Tag, TagToTag
from .general_resolvers import build_join_condition, get_selected, get_value
from .data_set import build_data_set_graphql_response
from api.database.database_helpers import execute_sql
from .feature import build_feature_graphql_response
from .gene import build_gene_graphql_response
from .paging_utils import create_temp_table, get_cursor, get_pagination_queries, Paging
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
        has_feature = get_value(node, 'feature_name') or get_value(
            node, 'feature_display') or get_value(node, 'order') or get_value(node, 'unit')
        has_gene = get_value(node, 'entrez') or get_value(node, 'hgnc') or get_value(
            node, 'description') or get_value(node, 'friendly_name') or get_value(node, 'io_landscape_name')
        return {
            'id': node_id,
            'dataSet': build_data_set_graphql_response(node),
            'feature': build_feature_graphql_response()(node) if has_feature else None,
            'gene': build_gene_graphql_response()(node) if has_gene else None,
            'label': get_value(node, 'label'),
            'name': get_value(node, 'node_name'),
            'score': get_value(node, 'score'),
            'tags': map(build_tag_graphql_response(), tags),
            'x': get_value(node, 'x'),
            'y': get_value(node, 'y')
        }
    return f


def build_node_request(requested, data_set_requested, feature_requested, gene_requested, data_set=None, distinct=False, entrez=None, feature=None, max_score=None, min_score=None, network=None, related=None, paging=None, tag=None):
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
                              'name': data_set_1.name.label('data_set_name'),
                              'type': data_set_1.data_set_type.label('data_set_type')}

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

    if max_score:
        query = query.filter(node_1.score <= max_score)

    if min_score:
        query = query.filter(node_1.score >= min_score)

    if network:
        network_1 = aliased(Tag, name='nt')
        node_to_tag_1 = aliased(NodeToTag, name='ntt1')

        network_subquery = sess.query(network_1.id).filter(
            network_1.name.in_(network))

        node_tag_join_condition = build_join_condition(
            node_to_tag_1.node_id, node_1.id, node_to_tag_1.tag_id, network_subquery)

        query = query.join(node_to_tag_1, and_(*node_tag_join_condition))

    if tag:
        node_to_tag_2 = aliased(NodeToTag, name='ntt2')
        tag_1 = aliased(Tag, name="t")

        tag_subquery = sess.query(tag_1.id).filter(
            tag_1.name.in_(tag))

        node_tag_join_condition = build_join_condition(
            node_to_tag_2.node_id, node_1.id, node_to_tag_2.tag_id, tag_subquery)

        query = query.join(node_to_tag_2, and_(*node_tag_join_condition))

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

    if feature or 'feature' in requested:
        is_outer = not bool(feature)
        feature_join_condition = build_join_condition(
            feature_1.id, node_1.feature_id, feature_1.name, feature)
        query = query.join(feature_1, and_(
            *feature_join_condition), isouter=is_outer)

    if entrez or 'gene' in requested:
        is_outer = not bool(entrez)
        gene_join_condition = build_join_condition(
            gene_1.id, node_1.gene_id, gene_1.entrez, entrez)
        query = query.join(gene_1, and_(
            *gene_join_condition), isouter=is_outer)

    # order = []
    # append_to_order = order.append
    # if 'name' in requested:
    #     append_to_order(node_1.name)
    # if 'label' in requested:
    #     append_to_order(node_1.label)
    # if 'score' in requested:
    #     append_to_order(node_1.score)
    # if 'x' in requested:
    #     append_to_order(node_1.x)
    # if 'y' in requested:
    #     append_to_order(node_1.y)
    # if not order:
    #     append_to_order(node_1.id)
    # query = query.order_by(*order)

    # return query.distinct()
    return get_pagination_queries(query, paging, distinct, cursor_field=node_1.id)


def build_tags_request(requested, tag_requested, data_set=None, entrez=None, feature=None, max_score=None, min_score=None, network=None, related=None, tag=None):
    if 'tags' not in requested:
        return None
    sess = db.session

    data_set_1 = aliased(Dataset, name='d')
    network_tag_2 = aliased(Tag, name='nt2')
    node_1 = aliased(Node, name='n')
    node_to_tag_2 = aliased(NodeToTag, name='ntt2')
    tag_1 = aliased(Tag, name='t')
    tag_to_tag_1 = aliased(TagToTag, name='tt')

    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                                'color': tag_1.color.label('color'),
                                'longDisplay': tag_1.long_display.label('tag_long_display'),
                                'name': tag_1.name.label('name'),
                                'shortDisplay': tag_1.short_display.label('tag_short_display')}

    tag_core = get_selected(tag_requested, tag_core_field_mapping)

    # Always select the tag id and the node id.
    tag_core |= {tag_1.id.label('id'), node_1.id.label('node_id')}

    tag_query = sess.query(*tag_core)
    tag_query = tag_query.select_from(node_1)

    if max_score or max_score == 0:
        tag_query = tag_query.filter(node_1.score <= max_score)

    if min_score or min_score == 0:
        tag_query = tag_query.filter(node_1.score >= min_score)

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

    # Filter results down by the nodes' association with the passed network
    if network:
        network_tag_1 = aliased(Tag, name='nt1')
        node_to_tag_1 = aliased(NodeToTag, name='ntt1')
        network_subquery = sess.query(network_tag_1.id).filter(
            network_tag_1.name.in_(network))
        node_tag_join_condition = build_join_condition(
            node_to_tag_1.node_id, node_1.id, node_to_tag_1.tag_id, network_subquery)
        tag_query = tag_query.join(
            node_to_tag_1, and_(*node_tag_join_condition))

    if tag:
        tag_2 = aliased(Tag, name='t2')
        node_to_tag_3 = aliased(NodeToTag, name='ntt3')
        node_tag_subquery = sess.query(tag_2.id).filter(
            tag_2.name.in_(tag))
        node_tag_join_condition = build_join_condition(
            node_to_tag_3.node_id, node_1.id, node_to_tag_3.tag_id, node_tag_subquery)
        tag_query = tag_query.join(
            node_to_tag_3, and_(*node_tag_join_condition))

    if feature:
        feature_1 = aliased(Feature, name='f')
        feature_join_condition = build_join_condition(
            feature_1.id, node_1.feature_id, feature_1.name, feature)
        tag_query = tag_query.join(
            feature_1, and_(*feature_join_condition))

    if entrez:
        gene_1 = aliased(Gene, name='g')
        gene_join_condition = build_join_condition(
            gene_1.id, node_1.gene_id, gene_1.entrez, entrez)
        tag_query = tag_query.join(gene_1, and_(*gene_join_condition))

    tag_query = tag_query.join(
        node_to_tag_2, node_to_tag_2.node_id == node_1.id)

    network_tag_subquery = sess.query(
        network_tag_2.id).filter(network_tag_2.name == 'network')

    tag_to_tag_join_condition = [
        tag_to_tag_1.tag_id == node_to_tag_2.tag_id, tag_to_tag_1.related_tag_id.notin_(network_tag_subquery)]

    tag_query = tag_query.join(
        tag_to_tag_1, and_(*tag_to_tag_join_condition))

    tag_query = tag_query.join(tag_1, tag_to_tag_1.tag_id == tag_1.id)

    return tag_query.distinct()

def fetch_nodes_with_tags(query, paging, distinct, tag_requested, network):
    items, table_name, conn = create_temp_table(query, paging, distinct)
    return items, return_associated_tags(table_name, conn, tag_requested, network)

def return_associated_tags(table_name, conn, tag_requested, network):
    tag_1 = aliased(Tag, name='t')
    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                                'color': tag_1.color.label('color'),
                                'longDisplay': tag_1.long_display.label('tag_long_display'),
                                'name': tag_1.name.label('name'),
                                'shortDisplay': tag_1.short_display.label('tag_short_display')}

    tag_core = get_selected(tag_requested, tag_core_field_mapping)
    tag_fields = [str(tag_field) for tag_field in tag_core]
    sep = ', '
    tag_fields = sep.join(tag_fields)

    query = f'SELECT DISTINCT {tag_fields}, n.id as node_id FROM tags as t, {table_name} as n, nodes_to_tags WHERE n.id = nodes_to_tags.node_id AND t.id = nodes_to_tags.tag_id'
    if network is not None:
        network_str = ''
        for net in network:
            network_str += f"'{net}', "
        network_str = network_str[0:-2]
        query += f' AND t.name NOT IN ({network_str})'
    tag_results = execute_sql(query, conn=conn)
    tag_dict = dict()
    if tag_results:
        for key, collection in groupby(tag_results, key=lambda t: t.node_id):
            tag_dict[key] = tag_dict.get(key, []) + list(collection)
    return tag_dict

def return_node_derived_fields(requested, tag_requested, data_set=None, entrez=None, feature=None, max_score=None, min_score=None, network=None, related=None, tag=None):
    tag_results = build_tags_request(
        requested, tag_requested, data_set=data_set, entrez=entrez, feature=feature, max_score=max_score, min_score=min_score, network=network, related=related, tag=tag)

    tag_dict = dict()
    if tag_results:
        for key, collection in groupby(tag_results.yield_per(10000).all(), key=lambda t: t.node_id):
            tag_dict[key] = tag_dict.get(key, []) + list(collection)

    return tag_dict
