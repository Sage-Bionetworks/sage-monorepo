from .resolver_helpers import build_feature_graphql_response, feature_related_sample_request_fields, feature_request_fields, get_requested, request_features, return_feature_derived_fields


def resolve_features(_obj, info, dataSet=None, feature=None, featureClass=None, maxValue=None, minValue=None, related=None, sample=None, tag=None):
    requested = get_requested(info, feature_request_fields)

    sample_requested = get_requested(
        info, feature_related_sample_request_fields, 'samples')

    features = request_features(requested, set(), set(), data_set=dataSet, feature=feature, feature_class=featureClass, max_value=maxValue,
                                min_value=minValue, related=related, sample=sample, tag=tag, by_class=False, by_tag=False)

    feature_ids = set(feature.id for feature in features)

    max_min_dict, sample_dict = return_feature_derived_fields(
        requested, sample_requested, feature_ids=feature_ids, data_set=dataSet, max_value=maxValue, min_value=minValue, related=related, sample=sample, tag=tag)

    return map(build_feature_graphql_response(max_min_dict=max_min_dict, sample_dict=sample_dict), features)
