from .resolver_helpers import build_feature_value_graphql_response, feature_value_request_fields, simple_feature_request_fields, simple_sample_request_fields, build_feature_values_query, get_requested, get_selection_set, get_requested
from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_feature_values(_obj, info, distinct=False, paging=None, feature=None, featureClass=None, maxValue=None, minValue=None, sample=None, cohort=None):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=feature_value_request_fields)

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_feature_request_fields, child_node='feature')

    sample_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_sample_request_fields, child_node='sample')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_feature_values_query(
        requested, feature_requested, sample_requested, distinct, paging, feature=feature, feature_class=featureClass, max_value=maxValue, min_value=minValue, sample=sample, cohort=cohort)

    pagination_requested = get_requested(info, paging_fields, 'paging')

    res = paginate(query, count_query, paging, distinct,
                   build_feature_value_graphql_response, pagination_requested)
    return(res)
