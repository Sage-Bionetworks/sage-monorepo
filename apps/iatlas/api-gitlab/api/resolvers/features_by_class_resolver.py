from itertools import groupby
from .resolver_helpers import build_feature_graphql_response, get_value, request_features


def resolve_features_by_class(_obj, info, dataSet=None, feature=None, featureClass=None, related=None, sample=None, tag=None):
    feature_results = request_features(_obj, info, data_set=dataSet, feature=feature, feature_class=featureClass,
                                       related=related, sample=sample, tag=tag, by_class=True)

    class_dict = dict()
    for feature_class, features_list in groupby(feature_results, key=lambda f: get_value(f, 'class')):
        class_dict[feature_class] = class_dict.get(
            feature_class, []) + list(features_list)

    return_list = []
    append_to_list = return_list.append
    for feature_class, features_list in class_dict.items():
        features = list(map(build_feature_graphql_response, features_list))
        append_to_list({
            'class': feature_class,
            'features': features
        })

    return return_list
