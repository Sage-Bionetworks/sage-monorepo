from .resolver_helpers import build_feature_graphql_response, request_features, return_derived_fields


def resolve_features(_obj, info, dataSet=None, feature=None, featureClass=None, maxValue=None, minValue=None, related=None, sample=None, tag=None):
    features = request_features(_obj, info, data_set=dataSet, feature=feature, feature_class=featureClass, max_value=maxValue,
                                min_value=minValue, related=related, sample=sample, tag=tag, by_class=False, by_tag=False)

    feature_dict = {feature.id: feature for feature in features}

    max_min_dict, sample_dict = return_derived_fields(
        info, feature_dict=feature_dict, data_set=dataSet, max_value=maxValue, min_value=minValue, related=related, sample=sample, tag=tag)

    return map(build_feature_graphql_response(max_min_dict=max_min_dict, sample_dict=sample_dict), features)
