from itertools import groupby
from .resolver_helpers import build_feature_graphql_response, get_value, request_features, return_derived_fields


def resolve_features_by_tag(_obj, info, dataSet=None, feature=None, featureClass=None, maxValue=None, minValue=None, related=None, sample=None, tag=None):
    feature_results = request_features(_obj, info, data_set=dataSet, feature=feature, feature_class=featureClass, max_value=maxValue,
                                       min_value=minValue, related=related, sample=sample, tag=tag, by_tag=True)
    feature_ids = set(feature.id for feature in feature_results)

    tag_dict = dict()
    for feature_tag, features_list in groupby(feature_results, key=lambda f: f.tag):
        tag_dict[feature_tag] = tag_dict.get(
            feature_tag, []) + list(features_list)

    def build_response(feature_set):
        feature_tag, features = feature_set
        max_min_dict, sample_dict = return_derived_fields(info, feature_ids=feature_ids, data_set=dataSet, max_value=maxValue,
                                                          min_value=minValue, related=related, sample=sample, tag=[feature_tag], by_tag=True)
        return {
            'characteristics': get_value(features[0], 'tag_characteristics'),
            'color': get_value(features[0], 'tag_color'),
            'display': get_value(features[0], 'tag_display'),
            'features': list(
                map(build_feature_graphql_response(max_min_dict=max_min_dict, sample_dict=sample_dict), features)),
            'tag': feature_tag
        }

    return map(build_response, tag_dict.items())
