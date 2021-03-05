from .resolver_helpers import build_rvpa_graphql_response, build_rare_variant_pathway_association_request, rare_variant_pathway_association_request_fields, get_requested, get_selection_set, simple_data_set_request_fields, simple_feature_request_fields

from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_rare_variant_pathway_associations(
        _obj, info, distinct=False, paging=None, dataSet=None, feature=None, pathway=None, maxPValue=None, minPValue=None):
    # The selection is nested under the 'items' node.
    selection_set = get_selection_set(info=info, child_node='items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=rare_variant_pathway_association_request_fields)

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_data_set_request_fields, child_node='dataSet')

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_feature_request_fields, child_node='feature')

    if distinct == False:
        # Add the id as a cursor if not selecting distinct
        requested.add('id')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_rare_variant_pathway_association_request(
        requested, data_set_requested, feature_requested, distinct=distinct, paging=paging, data_set=dataSet, feature=feature, pathway=pathway, max_p_value=maxPValue, min_p_value=minPValue)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    return paginate(query, count_query, paging, distinct, build_rvpa_graphql_response, pagination_requested)
