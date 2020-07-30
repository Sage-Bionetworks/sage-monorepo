from itertools import groupby
from .resolver_helpers import build_feature_graphql_response, get_value, request_features, return_feature_derived_fields


def resolve_features_by_class(_obj, info, dataSet=None, feature=None, featureClass=None, maxValue=None, minValue=None, related=None, sample=None, tag=None):
    feature_results = request_features(_obj, info, data_set=dataSet, feature=feature, feature_class=featureClass, max_value=maxValue,
                                       min_value=minValue, related=related, sample=sample, tag=tag, by_class=True)

    feature_ids = set(feature.id for feature in feature_results)

    max_min_dict, sample_dict = return_feature_derived_fields(
        info, feature_ids=feature_ids, data_set=dataSet, max_value=maxValue, min_value=minValue, related=related, sample=sample, tag=tag, by_class=True)

    class_dict = dict()
    for feature_class, features_list in groupby(feature_results, key=lambda f: get_value(f, 'class')):
        class_dict[feature_class] = class_dict.get(
            feature_class, []) + list(features_list)

    return [{
        'class': feature_class,
        'features': list(map(build_feature_graphql_response(max_min_dict=max_min_dict, sample_dict=sample_dict), features))
    } for feature_class, features in class_dict.items()]
