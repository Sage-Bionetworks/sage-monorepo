from sqlalchemy import and_, orm
from api import db
from api.db_models import CopyNumberResult, Dataset, Feature, Gene, Tag
from .general_resolvers import build_join_condition, build_option_args, get_selected, get_selection_set, get_value
from .cursor_utils import get_cursor, to_cursor_hash

cnr_request_fields = {'dataSet',
                      'direction',
                      'feature',
                      'gene',
                      'meanNormal',
                      'meanCnv',
                      'pValue',
                      'log10PValue',
                      'tag',
                      'tStat'}


def build_cnr_graphql_response(copy_number_result):
    return {
        'cursor': to_cursor_hash(get_value(copy_number_result, 'id')),
        'node': {
            'id': get_value(copy_number_result, 'id'),
            'direction': get_value(copy_number_result, 'direction'),
            'meanNormal': get_value(copy_number_result, 'mean_normal'),
            'meanCnv': get_value(copy_number_result, 'mean_cnv'),
            'pValue': get_value(copy_number_result, 'p_value'),
            'log10PValue': get_value(copy_number_result, 'log10_p_value'),
            'tStat': get_value(copy_number_result, 't_stat'),
            'dataSet': {
                'display': get_value(copy_number_result, 'data_set_display'),
                'name': get_value(copy_number_result, 'data_set_name'),
            },
            'feature': {
                'display': get_value(copy_number_result, 'feature_display'),
                'name': get_value(copy_number_result, 'feature_name'),
                'order': get_value(copy_number_result, 'order'),
                'unit': get_value(copy_number_result, 'unit')
            },
            'gene': {
                'entrez': get_value(copy_number_result, 'entrez'),
                'hgnc': get_value(copy_number_result, 'hgnc'),
                'description': get_value(copy_number_result, 'description'),
                'friendlyName': get_value(copy_number_result, 'friendlyName'),
                'ioLandscapeName': get_value(copy_number_result, 'ioLandscapeName')
            },
            'tag': {
                'characteristics': get_value(copy_number_result, 'characteristics'),
                'color': get_value(copy_number_result, 'color'),
                'display': get_value(copy_number_result, 'tag_display'),
                'name': get_value(copy_number_result, 'tag_name'),
            }
        }

    }


def build_copy_number_result_request(requested, data_set_requested, feature_requested, gene_requested, tag_requested, first=None, after=None, last=None, before=None, data_set=None, direction=None, distinct=False, entrez=None,
                                     feature=None, max_p_value=None, max_log10_p_value=None,
                                     min_log10_p_value=None, min_mean_cnv=None,
                                     min_mean_normal=None, min_p_value=None, min_t_stat=None,
                                     tag=None):
    """
    Builds a SQL request.
    """
    sess = db.session

    copy_number_result_1 = orm.aliased(CopyNumberResult, name='dcnr')
    data_set_1 = orm.aliased(Dataset, name='ds')
    feature_1 = orm.aliased(Feature, name='f')
    gene_1 = orm.aliased(Gene, name='g')
    tag_1 = orm.aliased(Tag, name='t')

    core_field_mapping = {
                            'id': copy_number_result_1.id.label('id'),
                            'direction': copy_number_result_1.direction.label('direction'),
                          'meanNormal': copy_number_result_1.mean_normal.label('mean_normal'),
                          'meanCnv': copy_number_result_1.mean_cnv.label('mean_cnv'),
                          'pValue': copy_number_result_1.p_value.label('p_value'),
                          'log10PValue': copy_number_result_1.log10_p_value.label('log10_p_value'),
                          'tStat': copy_number_result_1.t_stat.label('t_stat')}

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

    tag_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                         'color': tag_1.color.label('color'),
                         'display': tag_1.display.label('tag_display'),
                         'name': tag_1.name.label('tag_name')}

    core = get_selected(requested, core_field_mapping)

    if 'dataSet' in requested:
        core |= get_selected(data_set_requested, data_set_field_mapping)

    if 'feature' in requested:
        core |= get_selected(feature_requested, feature_field_mapping)

    if 'gene' in requested:
        core |= get_selected(gene_requested, gene_field_mapping)

    if 'tag' in requested:
        core |= get_selected(tag_requested, tag_field_mapping)

    query = sess.query(*core)
    query = query.select_from(copy_number_result_1)

    # Handle cursor and sort order
    cursor, sort_order = get_cursor(before, after)
    order_by = copy_number_result_1.id
    if sort_order == 'ASC':
        query = query.order_by(order_by)
    else:
        query = query.order_by(order_by.desc())

    if cursor:
        if sort_order == 'ASC':
            query = query.filter(copy_number_result_1.id > cursor)
        else:
            query = query.filter(copy_number_result_1.id < cursor)
    # end handle cursor

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

    if data_set or 'data_set' in requested:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, copy_number_result_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if entrez or 'gene' in requested:
        is_outer = not bool(entrez)
        data_set_join_condition = build_join_condition(
            gene_1.id, copy_number_result_1.gene_id, filter_column=gene_1.entrez, filter_list=entrez)
        query = query.join(gene_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if feature or 'feature' in requested:
        is_outer = not bool(feature)
        data_set_join_condition = build_join_condition(
            feature_1.id, copy_number_result_1.feature_id, filter_column=feature_1.name, filter_list=feature)
        query = query.join(feature_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if tag or 'tag' in requested:
        is_outer = not bool(tag)
        data_set_join_condition = build_join_condition(
            tag_1.id, copy_number_result_1.tag_id, filter_column=tag_1.name, filter_list=tag)
        query = query.join(tag_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if distinct == True:
        return query.distinct()
    return query
