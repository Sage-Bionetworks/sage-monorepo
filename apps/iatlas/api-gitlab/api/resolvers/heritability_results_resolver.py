from .resolver_helpers import build_hr_graphql_response, build_heritability_result_request, heritability_result_request_fields, get_requested, get_selection_set, simple_data_set_request_fields, simple_feature_request_fields, simple_gene_request_fields, simple_tag_request_fields

from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_heritability_results(
        _obj, info, dataSet=None, distinct=False, feature=None, maxPValue=None, minFoldChange=None, minPValue=None, paging=None, module=None, cluster=None):
    # The selection is nested under the 'items' node.
    selection_set = get_selection_set(info=info, child_node='items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=heritability_result_request_fields)

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_data_set_request_fields, child_node='dataSet')

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_feature_request_fields, child_node='feature')

    if distinct == False:
        # Add the id as a cursor if not selecting distinct
        requested.add('id')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_heritability_result_request(
        requested, data_set_requested, feature_requested, data_set=dataSet, distinct=distinct, feature=feature, max_p_value=maxPValue, min_p_value=minPValue, module=module, cluster=cluster, paging=paging)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    return paginate(query, count_query, paging, distinct, build_hr_graphql_response, pagination_requested)
