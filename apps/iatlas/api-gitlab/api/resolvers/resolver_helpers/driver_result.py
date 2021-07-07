from sqlalchemy import and_
from sqlalchemy.orm import aliased
from api import db
from api.db_models import Dataset, DatasetToTag, DriverResult, Feature, Gene, Mutation, MutationCode, Tag
from .general_resolvers import build_join_condition, get_selected, get_value
from .data_set import build_data_set_graphql_response
from .feature import build_feature_graphql_response
from .gene import build_gene_graphql_response
from .paging_utils import get_pagination_queries, Paging
from .tag import build_simple_tag_graphql_response

driver_result_request_fields = {'dataSet',
                                'feature',
                                'gene',
                                'mutationCode',
                                'mutationId',
                                'tag',
                                'pValue',
                                'foldChange',
                                'log10PValue',
                                'log10FoldChange',
                                'numWildTypes',
                                'numMutants'}


def build_dr_graphql_response(driver_result):
    result = {
        'id': get_value(driver_result, 'id'),
        'pValue': get_value(driver_result, 'p_value'),
        'foldChange': get_value(driver_result, 'fold_change'),
        'log10PValue': get_value(driver_result, 'log10_p_value'),
        'log10FoldChange': get_value(driver_result, 'log10_fold_change'),
        'numWildTypes': get_value(driver_result, 'n_wt'),
        'numMutants': get_value(driver_result, 'n_mut'),
        'dataSet': build_data_set_graphql_response(driver_result),
        'feature': build_feature_graphql_response()(driver_result),
        'gene': build_gene_graphql_response()(driver_result),
        'mutationCode': get_value(driver_result, 'code'),
        'mutationId': get_value(driver_result, 'mutation_id'),
        'tag': build_simple_tag_graphql_response(driver_result)
    }
    return(result)


def build_driver_result_request(
        requested, data_set_requested, feature_requested, gene_requested, tag_requested, data_set=None, distinct=False, entrez=None, feature=None, max_p_value=None, max_log10_p_value=None, min_fold_change=None, min_log10_fold_change=None, min_log10_p_value=None, min_p_value=None, min_n_mut=None, min_n_wt=None, mutation_code=None, paging=None, related=None, tag=None):
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
        `distinct` - a boolean, indicates whether duplicate records should be filtered out
        `entrez` - a list of integers, gene entrez ids
        `feature` - a list of strings, feature names
        `max_p_value` - a float, a maximum P value
        `max_log10_p_value` - a float, a minimum calculated log10 P value
        `min_fold_change` - a float, a minimum fold change value
        `min_log10_fold_change` - a float, a minimum calculated log 10 fold change value
        `min_log10_p_value` - a float, a minimum calculated log 10 P value
        `min_p_value` - a float, a minimum P value
        `min_n_mut` - a float, a minimum number of mutants
        `min_n_wt` - a float, a minimum number of wild types
        `mutation_code` - a list of strings, mutation codes
        `paging` - a dict containing pagination metadata
        `related` - a list of strings, tags related to the dataset that is associated with the result.
        `tag` - a list of strings, tag names
    """
    sess = db.session

    driver_result_1 = aliased(DriverResult, name='dr')
    gene_1 = aliased(Gene, name='g')
    mutation_1 = aliased(Mutation, name='m')
    mutation_code_1 = aliased(MutationCode, name='mc')
    tag_1 = aliased(Tag, name='t')
    feature_1 = aliased(Feature, name='f')
    data_set_1 = aliased(Dataset, name='ds')

    core_field_mapping = {
        'id': driver_result_1.id.label('id'),
        'pValue': driver_result_1.p_value.label('p_value'),
        'foldChange': driver_result_1.fold_change.label('fold_change'),
        'log10PValue': driver_result_1.log10_p_value.label('log10_p_value'),
        'log10FoldChange': driver_result_1.log10_fold_change.label('log10_fold_change'),
        'mutationCode': mutation_code_1.code.label('code'),
        'mutationId': mutation_1.id.label('mutation_id'),
        'numWildTypes': driver_result_1.n_wt.label('n_wt'),
        'numMutants': driver_result_1.n_mut.label('n_mut')}
    data_set_core_field_mapping = {'display': data_set_1.display.label('data_set_display'),
                                   'name': data_set_1.name.label('data_set_name'),
                                   'type': data_set_1.data_set_type.label('data_set_type')}
    feature_core_field_mapping = {'display': feature_1.display.label('feature_display'),
                                  'name': feature_1.name.label('feature_name'),
                                  'order': feature_1.order.label('order'),
                                  'unit': feature_1.unit.label('unit')}
    gene_core_field_mapping = {'entrez': gene_1.entrez.label('entrez'),
                               'hgnc': gene_1.hgnc.label('hgnc'),
                               'description': gene_1.description.label('description'),
                               'friendlyName': gene_1.friendly_name.label('friendly_name'),
                               'ioLandscapeName': gene_1.io_landscape_name.label('io_landscape_name')}
    tag_core_field_mapping = {'characteristics': tag_1.characteristics.label('characteristics'),
                              'color': tag_1.color.label('color'),
                              'longDisplay': tag_1.long_display.label('tag_long_display'),
                              'name': tag_1.name.label('tag_name'),
                              'shortDisplay': tag_1.short_display.label('tag_short_display')}

    core = get_selected(requested, core_field_mapping)
    core |= get_selected(data_set_requested, data_set_core_field_mapping)
    core |= get_selected(feature_requested, feature_core_field_mapping)
    core |= get_selected(gene_requested, gene_core_field_mapping)
    core |= get_selected(tag_requested, tag_core_field_mapping)

    query = sess.query(*core)
    query = query.select_from(driver_result_1)

    if max_p_value or max_p_value == 0:
        query = query.filter(driver_result_1.p_value <= max_p_value)

    if (max_log10_p_value or max_log10_p_value == 0) and (not max_p_value and max_p_value != 0):
        query = query.filter(
            driver_result_1.log10_p_value <= max_log10_p_value)

    if min_fold_change or min_fold_change == 0:
        query = query.filter(
            driver_result_1.fold_change >= min_fold_change)

    if (min_log10_fold_change or min_log10_fold_change == 0) and (not min_fold_change and min_fold_change != 0):
        query = query.filter(
            driver_result_1.log10_fold_change >= min_log10_fold_change)

    if (min_log10_p_value or min_log10_p_value == 0) and (not min_p_value and min_p_value != 0):
        query = query.filter(
            driver_result_1.log10_p_value >= min_log10_p_value)

    if min_p_value or min_p_value == 0:
        query = query.filter(driver_result_1.p_value >= min_p_value)

    if min_n_mut or min_n_mut == 0:
        query = query.filter(driver_result_1.n_mut >= min_n_mut)

    if min_n_wt or min_n_wt == 0:
        query = query.filter(driver_result_1.n_wt >= min_n_wt)

    if 'dataSet' in requested or data_set or related:
        is_outer = not bool(data_set)
        data_set_join_condition = build_join_condition(
            data_set_1.id, driver_result_1.dataset_id, filter_column=data_set_1.name, filter_list=data_set)
        query = query.join(data_set_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'gene' in requested or entrez:
        is_outer = not bool(entrez)
        data_set_join_condition = build_join_condition(
            gene_1.id, driver_result_1.gene_id, filter_column=gene_1.entrez, filter_list=entrez)
        query = query.join(gene_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'feature' in requested or feature:
        is_outer = not bool(feature)
        data_set_join_condition = build_join_condition(
            feature_1.id, driver_result_1.feature_id, filter_column=feature_1.name, filter_list=feature)
        query = query.join(feature_1, and_(
            *data_set_join_condition), isouter=is_outer)

    if 'mutationCode' in requested or mutation_code:
        is_outer = not bool(mutation_code)
        mutation_code_join_condition = build_join_condition(
            mutation_code_1.id, driver_result_1.mutation_code_id, filter_column=mutation_code_1.code, filter_list=mutation_code)
        query = query.join(mutation_code_1, and_(
            *mutation_code_join_condition), isouter=is_outer)

    if 'mutationId' in requested:
        query = query.join(mutation_1, and_(
            mutation_1.gene_id == driver_result_1.gene_id, mutation_1.mutation_code_id == driver_result_1.mutation_code_id))

    if 'tag' in requested or tag:
        is_outer = not bool(tag)
        data_set_join_condition = build_join_condition(
            tag_1.id, driver_result_1.tag_id, filter_column=tag_1.name, filter_list=tag)
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

    return get_pagination_queries(query, paging, distinct, cursor_field=driver_result_1.id)
