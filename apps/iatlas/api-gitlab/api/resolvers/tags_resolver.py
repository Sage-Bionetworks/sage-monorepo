from .resolver_helpers import build_tag_graphql_response, request_tags, return_tag_derived_fields


def resolve_tags(_obj, info, dataSet=None, feature=None, featureClass=None, related=None, sample=None, tag=None):
    tag_results = request_tags(_obj, info=info, data_set=dataSet, feature=feature, feature_class=featureClass,
                               related=related, sample=sample, tag=tag, get_samples=False)
    tag_ids = set(tag.id for tag in tag_results)

    sample_dict = return_tag_derived_fields(info, data_set=dataSet, feature=feature, feature_class=featureClass,
                                            related=related, sample=sample, tag_ids=tag_ids)

    return map(build_tag_graphql_response(sample_dict), tag_results)
