from .resolver_helpers import build_feature_graphql_response, feature_related_sample_request_fields, feature_request_fields, get_requested, request_features, return_feature_derived_fields, build_features_query, get_selection_set, get_requested, simple_tag_request_fields, feature_class_request_fields, return_feature_derived_fields
from .resolver_helpers.paging_utils import paginate, paging_fields, create_paging


def resolve_features(_obj, info, distinct=False, paging=None, feature=None, featureClass=None, maxValue=None, minValue=None, sample=None, cohort=None):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=feature_request_fields)

    sample_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=feature_related_sample_request_fields, child_node='samples')

    max_items = 1 if 'samples' in requested else 100_000

    paging = create_paging(paging, max_items)

    query, count_query = build_features_query(
        requested, distinct, paging, feature=feature, feature_class=featureClass, max_value=maxValue, min_value=minValue, sample=sample, cohort=cohort)

    pagination_requested = get_requested(info, paging_fields, 'paging')

    res = paginate(query, count_query, paging, distinct,
                   build_feature_graphql_response(requested, sample_requested, maxValue, minValue, cohort, sample), pagination_requested)
    return(res)
