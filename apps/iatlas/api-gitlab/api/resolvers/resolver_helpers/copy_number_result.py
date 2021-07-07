from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import CopyNumberResult, Dataset, DatasetToTag, Feature, Gene, Tag
from .general_resolvers import build_join_condition, get_selected, get_value
from .data_set import build_data_set_graphql_response
from .feature import build_feature_graphql_response
from .gene import build_gene_graphql_response
from .tag import build_simple_tag_graphql_response
from .paging_utils import get_pagination_queries

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
    result = {
        'id': get_value(copy_number_result, 'id'),
        'direction': get_value(copy_number_result, 'direction'),
        'meanNormal': get_value(copy_number_result, 'mean_normal'),
        'meanCnv': get_value(copy_number_result, 'mean_cnv'),
        'pValue': get_value(copy_number_result, 'p_value'),
        'log10PValue': get_value(copy_number_result, 'log10_p_value'),
        'tStat': get_value(copy_number_result, 't_stat'),
        'dataSet': build_data_set_graphql_response(copy_number_result),
        'feature': build_feature_graphql_response()(copy_number_result),
        'gene': build_gene_graphql_response()(copy_number_result),
        'tag': build_simple_tag_graphql_response(copy_number_result)
    }
    return(result)


def build_copy_number_result_request(
        requested, data_set_requested, feature_requested, gene_requested, tag_requested, data_set=None, direction=None, distinct=False, entrez=None, feature=None, max_p_value=None, max_log10_p_value=None, min_log10_p_value=None, min_mean_cnv=None, min_mean_normal=None, min_p_value=None, min_t_stat=None, paging=None, related=None, tag=None):
    """
    Builds a SQL request.

    All positional arguments are required. Positional arguments are:
        1st position - a set of the requested fields at the root of the graphql request
        2nd position - a set of the requested fields in the 'dataSet' node of the graphql request. If 'dataSet' is not requested, this will be an empty set.
        3rd position - a set of the requested fields in the 'feature' node of the graphql request. If 'feature' is not requested, this will be an empty set.
        4th position - a set of the requested fields in the 'gene' node of the graphql request. If 'gene' is not requested, this will be an empty set.
        5th position - a set of the requested fields in the 'tag' node of the graphql request. If 'tag' is not requested, this will be an empty set.

    All keyword arguments are optional. Keyword arguments are:
        `data_set` - a list of strings, data set names
        `direction` - a value from the DirectionEnum. (either 'Amp' or 'Del')
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `entrez` - a list of integers, gene entrez ids
        `feature` - a list of strings, feature names
        `max_p_value` - a float, a maximum P value
        `max_log10_p_value` - a float, a minimum calculated log10 P value
        `min_log10_p_value` - a float, a minimum calculated log 10 P value
        `min_mean_cnv` - a float, a minimum mean cnv value
        `min_mean_normal` - a float, a minimum mean normal value
        `min_p_value` - a float, a minimum P value
        `min_t_stat` - a float, a minimum t stat value
        `paging` - a dict containing pagination metadata
        `related` - a list of strings, tags related to the dataset that is associated with the result.
        `tag` - a list of strings, tag names
    """
    sess = db.session

    copy_number_result_1 = aliased(CopyNumberResult, name='dcnr')
    data_set_1 = aliased(Dataset, name='ds')
    feature_1 = aliased(Feature, name='f')
    gene_1 = aliased(Gene, name='g')
    tag_1 = aliased(Tag, name='t')

    core_field_mapping = {
        'direction': copy_number_result_1.direction.label('direction'),
        'meanNormal': copy_number_result_1.mean_normal.label('mean_normal'),
        'meanCnv': copy_number_result_1.mean_cnv.label('mean_cnv'),
        'pValue': copy_number_result_1.p_value.label('p_value'),
        'log10PValue': copy_number_result_1.log10_p_value.label('log10_p_value'),
        'tStat': copy_number_result_1.t_stat.label('t_stat')}

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

    tag_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                         'color': tag_1.color.label('color'),
                         'longDisplay': tag_1.long_display.label('tag_long_display'),
                         'name': tag_1.name.label('tag_name'),
                         'shortDisplay': tag_1.short_display.label('tag_short_display')}

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(data_set_requested, data_set_field_mapping)
    core |= get_selected(feature_requested, feature_field_mapping)
    core |= get_selected(gene_requested, gene_field_mapping)
    core |= get_selected(tag_requested, tag_field_mapping)

    if not distinct:
        # Add the id as a cursor if not selecting distinct
        core.add(copy_number_result_1.id.label('id'))

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

    if data_set or 'dataSet' in requested or related:
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

    if related:
        data_set_to_tag_1 = aliased(DatasetToTag, name='dtt')
        related_tag_1 = aliased(Tag, name='rt')

        related_tag_sub_query = sess.query(related_tag_1.id).filter(
            related_tag_1.name.in_(related))

        data_set_tag_join_condition = build_join_condition(
            data_set_to_tag_1.dataset_id, data_set_1.id, data_set_to_tag_1.tag_id, related_tag_sub_query)
        query = query.join(
            data_set_to_tag_1, and_(*data_set_tag_join_condition))

    return get_pagination_queries(query, paging, distinct, cursor_field=copy_number_result_1.id)
