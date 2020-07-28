from itertools import groupby
from .resolver_helpers import build_feature_graphql_response, get_max_min_feature_values, get_value, request_features


def resolve_features_by_tag(_obj, info, dataSet=None, feature=None, featureClass=None, related=None, sample=None, tag=None):
    feature_results = request_features(_obj, info, data_set=dataSet, feature=feature, feature_class=featureClass,
                                       related=related, sample=sample, tag=tag, by_tag=True)

    max_min_dict = get_max_min_feature_values(
        info, features=feature_results, by_tag=True)

    tag_dict = dict()
    for feature_tag, features_list in groupby(feature_results, key=lambda f: f.tag):
        tag_dict[feature_tag] = tag_dict.get(
            feature_tag, []) + list(features_list)

    return_list = []
    append_to_list = return_list.append
    for feature_tag, features_list in tag_dict.items():
        features = list(
            map(build_feature_graphql_response(max_min_dict), features_list))
        append_to_list({
            'characteristics': get_value(features[0], 'tag_characteristics'),
            'color': get_value(features[0], 'tag_color'),
            'display': get_value(features[0], 'tag_display'),
            'features': features,
            'tag': feature_tag
        })

    return return_list
