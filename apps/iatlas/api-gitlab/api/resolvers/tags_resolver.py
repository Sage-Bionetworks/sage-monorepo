from .resolver_helpers import (build_tag_graphql_response, get_requested, get_selection_set, request_tags,
                               return_tag_derived_fields, simple_tag_request_fields, tag_request_fields)


def resolve_tags(_obj, info, dataSet=None, feature=None, featureClass=None, related=None, sample=None, tag=None):
    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, True, 'items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=tag_request_fields)

    related_selection_set = get_selection_set(
        selection_set, 'related' in requested, 'related')
    related_requested = get_requested(
        selection_set=related_selection_set, requested_field_mapping=simple_tag_request_fields)

    tag_results = request_tags(requested, data_set=dataSet, feature=feature, feature_class=featureClass,
                               related=related, sample=sample, tag=tag, get_samples=False)
    tag_ids = set(tag.id for tag in tag_results)

    (related_dict, sample_dict) = return_tag_derived_fields(requested, related_requested, data_set=dataSet, feature=feature, feature_class=featureClass,
                                                            related=related, sample=sample, tag=tag, tag_ids=tag_ids)

    return map(build_tag_graphql_response(related_dict, sample_dict), tag_results)
