from .resolver_helpers import build_tag_graphql_response, get_requested, request_tags, return_tag_derived_fields, simple_publication_request_fields, simple_tag_request_fields, tag_request_fields


def resolve_tags(_obj, info, dataSet=None, feature=None, featureClass=None, related=None, sample=None, tag=None):
    requested = get_requested(info, tag_request_fields)

    publications_requested = get_requested(
        info, simple_publication_request_fields, 'publications')

    related_requested = get_requested(
        info, simple_tag_request_fields, 'related')

    tag_results = request_tags(
        requested, data_set=dataSet, feature=feature, feature_class=featureClass, related=related, sample=sample, tag=tag)

    tag_ids = set(tag.id for tag in tag_results) if tag_results else set()

    (publication_dict, related_dict, sample_dict) = return_tag_derived_fields(
        requested, publications_requested, related_requested, data_set=dataSet, feature=feature, feature_class=featureClass, related=related, sample=sample, tag_ids=tag_ids) if tag_results else (dict(), dict(), dict())

    return map(build_tag_graphql_response(publication_dict, related_dict, sample_dict), tag_results)
