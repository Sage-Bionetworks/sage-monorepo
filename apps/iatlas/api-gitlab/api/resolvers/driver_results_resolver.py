from .resolver_helpers import build_dr_graphql_response, build_driver_result_request, driver_result_request_fields, get_requested, get_selection_set, simple_data_set_request_fields, simple_feature_request_fields, mutation_request_fields, simple_gene_request_fields, simple_tag_request_fields, mutation_type_request_fields

from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_driver_results(_obj, info, paging=None, distinct=False, dataSet=None, entrez=None, feature=None, mutation=None, mutationCode=None, related=None, tag=None, maxPValue=None, maxLog10PValue=None, minFoldChange=None, minLog10FoldChange=None, minLog10PValue=None, minPValue=None, minNumMutants=None, minNumWildTypes=None):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=driver_result_request_fields)

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_data_set_request_fields, child_node='dataSet')

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_feature_request_fields, child_node='feature')

    mutation_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=mutation_request_fields, child_node='mutation')

    mutation_selection_set = get_selection_set(
        selection_set=selection_set, child_node='mutation')

    mutation_gene_selection_set = get_selection_set(
        selection_set=mutation_selection_set, child_node='gene')

    mutation_gene_requested = get_requested(
        selection_set=mutation_gene_selection_set, requested_field_mapping=simple_gene_request_fields)

    mutation_type_requested = get_requested(
        selection_set=mutation_selection_set, requested_field_mapping=mutation_type_request_fields, child_node='mutationType')

    tag_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='tag')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_driver_result_request(
        requested, data_set_requested, feature_requested, mutation_requested, mutation_gene_requested, mutation_type_requested, tag_requested, data_set=dataSet, distinct=distinct, entrez=entrez, feature=feature, max_p_value=maxPValue, max_log10_p_value=maxLog10PValue, min_fold_change=minFoldChange, min_log10_fold_change=minLog10FoldChange, min_log10_p_value=minLog10PValue, min_p_value=minPValue, min_n_mut=minNumMutants, min_n_wt=minNumWildTypes, mutation=mutation, mutation_code=mutationCode, paging=paging, related=related, tag=tag
    )
    pagination_requested = get_requested(info, paging_fields, 'paging')
    return paginate(query, count_query, paging, distinct, build_dr_graphql_response, pagination_requested)
