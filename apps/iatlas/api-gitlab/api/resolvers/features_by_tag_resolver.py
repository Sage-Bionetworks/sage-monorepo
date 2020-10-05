from itertools import groupby
from .resolver_helpers import build_feature_graphql_response, feature_related_sample_request_fields, feature_request_fields, get_requested, get_selection_set, get_value, request_features, return_feature_derived_fields, simple_tag_request_fields


def resolve_features_by_tag(_obj, info, dataSet=None, feature=None, featureClass=None, maxValue=None, minValue=None, related=None, sample=None, tag=None):
    tag_requested = get_requested(
        info, simple_tag_request_fields.union({'features'}))

    feature_selection_set = get_selection_set(info=info, child_node='features')
    requested = get_requested(
        selection_set=feature_selection_set, requested_field_mapping=feature_request_fields)

    sample_requested = get_requested(
        selection_set=feature_selection_set, requested_field_mapping=feature_related_sample_request_fields, child_node='samples')

    feature_results = request_features(requested, set(), tag_requested, data_set=dataSet, feature=feature, feature_class=featureClass, max_value=maxValue,
                                       min_value=minValue, related=related, sample=sample, tag=tag, by_tag=True)
    feature_ids = set(feature.id for feature in feature_results)

    tag_dict = dict()
    for feature_tag, features_list in groupby(feature_results, key=lambda f: f.tag):
        tag_dict[feature_tag] = tag_dict.get(
            feature_tag, []) + list(features_list)

    def build_response(feature_set):
        feature_tag, features = feature_set
        max_min_dict, sample_dict = return_feature_derived_fields(requested, sample_requested, feature_ids=feature_ids, data_set=dataSet, max_value=maxValue,
                                                                  min_value=minValue, related=related, sample=sample, tag=[feature_tag])
        return {
            'characteristics': get_value(features[0], 'tag_characteristics'),
            'color': get_value(features[0], 'tag_color'),
            'features': map(build_feature_graphql_response(max_min_dict, sample_dict), features),
            'longDisplay': get_value(features[0], 'tag_long_display'),
            'shortDisplay': get_value(features[0], 'tag_short_display'),
            'tag': feature_tag
        }

    return map(build_response, tag_dict.items())
