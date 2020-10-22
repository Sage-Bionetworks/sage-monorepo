from .resolver_helpers import build_dr_graphql_response, build_driver_result_request, driver_result_request_fields, get_requested, get_selection_set, request_driver_results, simple_data_set_request_fields, simple_feature_request_fields, simple_gene_request_fields, simple_tag_request_fields

from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_driver_results(_obj, info, dataSet=None, distinct=False, entrez=None,
                           feature=None, maxPValue=None,
                           maxLog10PValue=None, minFoldChange=None, minLog10FoldChange=None,
                           minLog10PValue=None, minPValue=None, minNumMutants=None, minNumWildTypes=None, mutationCode=None, paging=None, tag=None):
    # The selection is nested under the 'items' node.
    selection_set = get_selection_set(info=info, child_node='items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=driver_result_request_fields)

    if distinct == False:
        requested.add('id')  # Add the id as a cursor if not selecting distinct

    pagination_set = get_selection_set(info=info, child_node='paging')
    pagination_requested = get_requested(selection_set=pagination_set, requested_field_mapping=paging_fields)
    paging = paging if paging else {'type': Paging.CURSOR, 'first': Paging.MAX_LIMIT}

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_data_set_request_fields, child_node='dataSet')

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_feature_request_fields, child_node='feature')

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_gene_request_fields, child_node='gene')

    tag_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='tag')

    query, count_query = build_driver_result_request(
        requested, data_set_requested, feature_requested, gene_requested, tag_requested, data_set=dataSet, distinct=distinct, entrez=entrez, feature=feature, max_p_value=maxPValue, max_log10_p_value=maxLog10PValue, min_fold_change=minFoldChange, min_log10_fold_change=minLog10FoldChange, min_log10_p_value=minLog10PValue, min_p_value=minPValue, min_n_mut=minNumMutants, min_n_wt=minNumWildTypes, mutation_code=mutationCode, paging=paging, tag=tag)

    return paginate(query, count_query, paging, distinct, build_dr_graphql_response)
