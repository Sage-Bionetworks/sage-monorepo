from sqlalchemy import and_, orm
from api import db
from api.db_models import CopyNumberResult, Dataset, Feature, Gene, Tag
from .general_resolvers import build_join_condition, build_option_args, get_selection_set


def build_copy_number_result_request(_obj, info, data_set=None, direction=None, entrez=None,
                                     feature=None, max_p_value=None, max_log10_p_value=None,
                                     min_log10_p_value=None, min_mean_cnv=None,
                                     min_mean_normal=None, min_p_value=None, min_t_stat=None,
                                     tag=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    selection_set = get_selection_set(info.field_nodes[0].selection_set, False)

    copy_number_result_1 = orm.aliased(CopyNumberResult, name='dcnr')
    data_set_1 = orm.aliased(Dataset, name='ds')
    feature_1 = orm.aliased(Feature, name='f')
    gene_1 = orm.aliased(Gene, name='g')
    tag_1 = orm.aliased(Tag, name='t')

    core_field_mapping = {'direction': copy_number_result_1.direction.label('direction'),
                          'meanNormal': copy_number_result_1.mean_normal.label('mean_normal'),
                          'meanCnv': copy_number_result_1.mean_cnv.label('mean_cnv'),
                          'pValue': copy_number_result_1.p_value.label('p_value'),
                          'log10PValue': copy_number_result_1.log10_p_value.label('log10_p_value'),
                          'tStat': copy_number_result_1.t_stat.label('t_stat')}

    related_field_mapping = {'dataSet': 'data_set',
                             'feature': 'feature',
                             'gene': 'gene',
                             'tag': 'tag'}

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
    query = query.select_from(copy_number_result_1)

    if direction:
        query = query.filter(copy_number_result_1.direction == direction)

    if max_p_value or max_p_value == 0:
        query = query.filter(copy_number_result_1.p_value <= max_p_value)

    if (max_log10_p_value or max_log10_p_value == 0) and (not max_p_value and max_p_value != 0):
        query = query.filter(
            copy_number_result_1.log10_p_value <= max_log10_p_value)

    if (min_log10_p_value or min_log10_p_value == 0) and (not min_p_value and min_p_value != 0):
        query = query.filter(
            copy_number_result_1.log10_p_value >= min_log10_p_value)

    if min_mean_cnv or min_mean_cnv == 0:
        query = query.filter(copy_number_result_1.mean_cnv >= min_mean_cnv)

    if min_mean_normal or min_mean_normal == 0:
        query = query.filter(
            copy_number_result_1.mean_normal >= min_mean_normal)

    if min_p_value or min_p_value == 0:
        query = query.filter(copy_number_result_1.p_value >= min_p_value)

    if min_t_stat or min_t_stat == 0:
        query = query.filter(copy_number_result_1.t_stat >= min_t_stat)

    if 'data_set' in relations or data_set:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, copy_number_result_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'gene' in relations or entrez:
        is_outer = not bool(entrez)
        data_set_join_condition = build_join_condition(
            gene_1.id, copy_number_result_1.gene_id, filter_column=gene_1.entrez, filter_list=entrez)
        query = query.join(gene_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'feature' in relations or feature:
        is_outer = not bool(feature)
        data_set_join_condition = build_join_condition(
            feature_1.id, copy_number_result_1.feature_id, filter_column=feature_1.name, filter_list=feature)
        query = query.join(feature_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'tag' in relations or tag:
        is_outer = not bool(tag)
        data_set_join_condition = build_join_condition(
            tag_1.id, copy_number_result_1.tag_id, filter_column=tag_1.name, filter_list=tag)
        query = query.join(tag_1, and_(
            *data_set_join_condition), isouter=is_outer)

    return query


def request_copy_number_results(_obj, info, data_set=None, direction=None, entrez=None,
                                feature=None, max_p_value=None, max_log10_p_value=None,
                                min_log10_p_value=None, min_mean_cnv=None,
                                min_mean_normal=None, min_p_value=None, min_t_stat=None,
                                tag=None):
    query = build_copy_number_result_request(_obj, info, data_set=data_set, direction=direction, entrez=entrez,
                                             feature=feature, max_p_value=max_p_value, max_log10_p_value=max_log10_p_value,
                                             min_log10_p_value=min_log10_p_value, min_mean_cnv=min_mean_cnv,
                                             min_mean_normal=min_mean_normal, min_p_value=min_p_value, min_t_stat=min_t_stat,
                                             tag=tag)
    return query.distinct().all()
