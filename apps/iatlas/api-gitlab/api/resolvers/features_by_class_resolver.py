from itertools import groupby
from .resolver_helpers import build_feature_graphql_response, feature_class_request_fields, feature_related_sample_request_fields, feature_request_fields, get_requested, get_selection_set, get_value, request_features, return_feature_derived_fields, simple_sample_request_fields


def resolve_features_by_class(_obj, info, dataSet=None, feature=None, featureClass=None, maxValue=None, minValue=None, related=None, sample=None, tag=None):
    class_requested = get_requested(
        info, feature_class_request_fields.union({'features'}))

    feature_selection_set = get_selection_set(info=info, child_node='features')
    requested = get_requested(
        selection_set=feature_selection_set, requested_field_mapping=feature_request_fields)

    sample_requested = get_requested(
        selection_set=feature_selection_set, requested_field_mapping=feature_related_sample_request_fields, child_node='samples')

    feature_results = request_features(requested, class_requested, set(), data_set=dataSet, feature=feature, feature_class=featureClass,
                                       max_value=maxValue, min_value=minValue, related=related, sample=sample, tag=tag, by_class=True)

    feature_ids = set(feature.id for feature in feature_results)

    max_min_dict, sample_dict = return_feature_derived_fields(
        requested, sample_requested, feature_ids=feature_ids, data_set=dataSet, max_value=maxValue, min_value=minValue, related=related, sample=sample, tag=tag)

    class_dict = dict()
    for feature_class, features_list in groupby(feature_results, key=lambda f: get_value(f, 'class')):
        class_dict[feature_class] = class_dict.get(
            feature_class, []) + list(features_list)

    return [{
        'class': feature_class,
        'features': map(build_feature_graphql_response(max_min_dict, sample_dict), features)
    } for feature_class, features in class_dict.items()]
