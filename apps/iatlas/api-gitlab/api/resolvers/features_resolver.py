from .resolver_helpers import build_feature_graphql_response, get_max_min_feature_values, get_selection_set, request_features


def resolve_features(_obj, info, dataSet=None, feature=None, featureClass=None, related=None, sample=None, tag=None):
    features = request_features(_obj, info, data_set=dataSet, feature=feature, feature_class=featureClass,
                                related=related, sample=sample, tag=tag, by_class=False, by_tag=False)

    max_min_dict = get_max_min_feature_values(info, features=features)

    return map(build_feature_graphql_response(max_min_dict), features)
