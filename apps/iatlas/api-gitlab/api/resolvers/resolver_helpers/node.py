from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToTag, Feature, Gene, Node, Tag
from .general_resolvers import build_join_condition, build_option_args, get_selection_set, get_value


def build_node_graphql_response(node):
    return {
        'direction': get_value(node, 'direction'),
        'meanNormal': get_value(node, 'mean_normal'),
        'meanCnv': get_value(node, 'mean_cnv'),
        'pValue': get_value(node, 'p_value'),
        'log10PValue': get_value(node, 'log10_p_value'),
        'tStat': get_value(node, 't_stat'),
        'dataSet': {
            'display': get_value(node, 'data_set_display'),
            'name': get_value(node, 'data_set_name'),
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
            'friendlyName': get_value(node, 'friendlyName'),
            'ioLandscapeName': get_value(node, 'ioLandscapeName')
        },
        'tag': {
            'characteristics': get_value(node, 'characteristics'),
            'color': get_value(node, 'color'),
            'display': get_value(node, 'tag_display'),
            'name': get_value(node, 'tag_name'),
        }
    }


def build_node_request(_obj, info, data_set=None, related=None, network=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)

    node_1 = aliased(Node, name='n')
    data_set_1 = aliased(Dataset, name='d')
    data_set_to_tag_1 = aliased(DatasetToTag, name='dt')
    network_1 = aliased(Tag, name='nt')
    related_1 = aliased(Tag, name='r')
    tag_to_tag_1 = aliased(TagToTag, name='tt')

    core_field_mapping = {'label': node_1.label.label('label'),
                          'name': node_1.name.label('name'),
                          'score': node_1.score.label('score'),
                          'x': node_1.x.label('x'),
                          'y': node_1.y.label('y')}

    related_field_mapping = {'dataSet': 'data_set',
                             'feature': 'feature',
                             'gene': 'gene',
                             'tags': 'tags'}

    core = build_option_args(selection_set, core_field_mapping)
    relations = build_option_args(selection_set, related_field_mapping)

    if 'data_set' in relations:
        data_set_selection_set = get_selection_set(
            selection_set, child_node='dataSet')
        data_set_core_field_mapping = {'display': data_set_1.display.label('data_set_display'),
                                       'name': data_set_1.name.label('data_set_name')}
        core = core + build_option_args(
            data_set_selection_set, data_set_core_field_mapping)

    if 'feature' in relations:
        feature_selection_set = get_selection_set(
            selection_set, child_node='feature')
        feature_core_field_mapping = {'display': feature_1.display.label('feature_display'),
                                      'name': feature_1.name.label('feature_name'),
                                      'order': feature_1.order.label('order'),
                                      'unit': feature_1.unit.label('unit')}
        core = core + build_option_args(
            feature_selection_set, feature_core_field_mapping)

    if 'gene' in relations:
        gene_selection_set = get_selection_set(
            selection_set, child_node='gene')
        gene_core_field_mapping = {'entrez': gene_1.entrez.label('entrez'),
                                   'hgnc': gene_1.hgnc.label('hgnc'),
                                   'description': gene_1.description.label('description'),
                                   'friendlyName': gene_1.friendly_name.label('friendly_name'),
                                   'ioLandscapeName': gene_1.io_landscape_name.label('io_landscape_name')}
        core = core + build_option_args(
            gene_selection_set, gene_core_field_mapping)

    if 'tag' in relations:
        tag_selection_set = get_selection_set(
            selection_set, child_node='tag')
        tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                                  'color': tag_1.color.label('color'),
                                  'display': tag_1.display.label('tag_display'),
                                  'name': tag_1.name.label('tag_name')}
        core = core + build_option_args(
            tag_selection_set, tag_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(node_1)

    if direction:
        query = query.filter(node_1.direction == direction)

    if max_p_value or max_p_value == 0:
        query = query.filter(node_1.p_value <= max_p_value)

    if (max_log10_p_value or max_log10_p_value == 0) and (not max_p_value and max_p_value != 0):
        query = query.filter(
            node_1.log10_p_value <= max_log10_p_value)

    if (min_log10_p_value or min_log10_p_value == 0) and (not min_p_value and min_p_value != 0):
        query = query.filter(
            node_1.log10_p_value >= min_log10_p_value)

    if min_mean_cnv or min_mean_cnv == 0:
        query = query.filter(node_1.mean_cnv >= min_mean_cnv)

    if min_mean_normal or min_mean_normal == 0:
        query = query.filter(
            node_1.mean_normal >= min_mean_normal)

    if min_p_value or min_p_value == 0:
        query = query.filter(node_1.p_value >= min_p_value)

    if min_t_stat or min_t_stat == 0:
        query = query.filter(node_1.t_stat >= min_t_stat)

    if 'data_set' in relations or data_set:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, node_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'gene' in relations or entrez:
        is_outer = not bool(entrez)
        data_set_join_condition = build_join_condition(
            gene_1.id, node_1.gene_id, filter_column=gene_1.entrez, filter_list=entrez)
        query = query.join(gene_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'feature' in relations or feature:
        is_outer = not bool(feature)
        data_set_join_condition = build_join_condition(
            feature_1.id, node_1.feature_id, filter_column=feature_1.name, filter_list=feature)
        query = query.join(feature_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'tag' in relations or tag:
        is_outer = not bool(tag)
        data_set_join_condition = build_join_condition(
            tag_1.id, node_1.tag_id, filter_column=tag_1.name, filter_list=tag)
        query = query.join(tag_1, and_(
            *data_set_join_condition), isouter=is_outer)

    return query


def get_data_set(info, data_set=None, node_ids=set()):
    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)
    requested = build_option_args(selection_set, {'dataSet': 'data_set'})

    if node_ids and 'data_set' in requested:
        sess = db.session

        data_set_1 = aliased(Dataset, name='d')
        node_1 = aliased(Node, name='n')

        data_set_selection_set = get_selection_set(
            selection_set, True, child_node='dataSet')
        data_set_core_field_mapping = {'display': data_set_1.display.label('display'),
                                       'name': data_set_1.name.label('name')}

        data_set_core = build_option_args(
            data_set_selection_set, data_set_core_field_mapping)
        # Always select the data_set id and the node id.
        data_set_core = data_set_core + \
            [data_set_1.id.label('id'), node_1.id.label('node_id')]

        requested = requested + build_option_args(
            data_set_selection_set, {'display': 'display', 'name': 'name'})

        data_set_query = sess.query(*data_set_core)
        data_set_query = data_set_query.select_from(data_set_1)

        if data_set:
            data_set_query = data_set_query.filter(
                data_set_1.name.in_(data_set))

        node_join_condition = build_join_condition(
            node_1.dataset_id, data_set_1.id, node_1.id, node_ids)

        data_set_query = data_set_query.join(
            node_1, and_(*node_join_condition))

        return data_set_query.distinct().all()
    return []


def get_feature(info, data_set=None, related=None, network=None, node_ids=set()):
    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)
    requested = build_option_args(selection_set, {'feature': 'feature'})

    if node_ids and 'feature' in requested:
        sess = db.session

        data_set_to_tag_1 = aliased(DatasetToTag, name='dt')
        feature_1 = aliased(Feature, name='f')
        network_1 = aliased(Tag, name='nt')
        node_1 = aliased(Node, name='n')
        related_1 = aliased(Tag, name='r')
        tag_to_tag_1 = aliased(TagToTag, name='tt')

        feature_selection_set = get_selection_set(
            selection_set, True, child_node='feature')
        feature_core_field_mapping = {'display': feature_1.display.label('display'),
                                      'name': feature_1.name.label('name'),
                                      'order': feature_1.order.label('order'),
                                      'unit': feature_1.unit.label('unit')}

        feature_core = build_option_args(
            feature_selection_set, feature_core_field_mapping)
        # Always select the feature id and the node id.
        feature_core = feature_core + \
            [feature_1.id.label('id'), node_1.id.label('node_id')]

        requested = requested + build_option_args(
            feature_selection_set, {'display': 'display', 'name': 'name', 'order': 'order', 'unit': 'unit'})

        feature_query = sess.query(*feature_core)
        feature_query = feature_query.select_from(feature_1)

        if data_set or related or network:
            feature_to_sample_1 = aliased(FeatureToSample, name='fs1')

            feature_query = feature_query.join(
                feature_to_sample_1, feature_1.id == feature_to_sample_1.feature_id)

            sample_tag_join_condition = [
                sample_to_tag_1.sample_id == sample_1.id]

            if data_set or related:
                data_set_1 = aliased(Dataset, name='d')

                data_set_sub_query = sess.query(data_set_1.id).filter(
                    data_set_1.name.in_(data_set)) if data_set else data_set

                data_set_to_sample_join_condition = build_join_condition(
                    data_set_to_sample_1.sample_id, feature_to_sample_1.sample_id, data_set_to_sample_1.dataset_id, data_set_sub_query)
                feature_query = feature_query.join(
                    data_set_to_sample_1, and_(*data_set_to_sample_join_condition))

            if related:
                data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
                related_tag_1 = aliased(Tag, name='rt')
                tag_to_tag_1 = aliased(TagToTag, name='tt')

                related_tag_sub_query = sess.query(related_tag_1.id).filter(
                    related_tag_1.name.in_(related))

                data_set_tag_join_condition = build_join_condition(
                    data_set_to_tag_1.dataset_id, data_set_to_sample_1.dataset_id, data_set_to_tag_1.tag_id, related_tag_sub_query)
                feature_query = feature_query.join(
                    data_set_to_tag_1, and_(*data_set_tag_join_condition))

                tag_to_tag_subquery = sess.query(tag_to_tag_1.tag_id).filter(
                    tag_to_tag_1.related_tag_id == data_set_to_tag_1.tag_id)

                sample_tag_join_condition.append(
                    sample_to_tag_1.tag_id.in_(tag_to_tag_subquery))

            if network:
                tag_to_network_2 = aliased(TagToTag, name='tn')

                network_subquery = sess.query(network_1.id).filter(
                    network_1.name.in_(network))

                sample_tag_join_condition = sample_tag_join_condition + \
                    build_join_condition(
                        sample_to_tag_1.tag_id, tag_to_network_2.tag_id, tag_to_network_2.related_tag_id, network_subquery)

            query = query.join(sample_to_tag_1, and_(
                *sample_tag_join_condition))

            query = query.join(tag_1, and_(*tag_join_condition))

        node_join_condition = build_join_condition(
            node_1.feature_id, feature_1.id, node_1.id, node_ids)

        feature_query = feature_query.join(node_1, and_(*node_join_condition))

        return feature_query.distinct().all()
    return []


def request_nodes(_obj, info, data_set=None, related=None, network=None):
    query = build_node_request(
        _obj, info, data_set=data_set, related=related, network=network)
    return query.yield_per(1000).distinct().all()
