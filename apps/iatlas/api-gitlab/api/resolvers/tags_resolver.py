from .resolver_helpers import build_tag_graphql_response, build_tag_request, get_requested, request_tags, return_tag_derived_fields, simple_publication_request_fields, simple_tag_request_fields, tag_request_fields, get_selection_set
from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_tags(_obj, info, distinct=False, paging=None, dataSet=None, related=None, sample=None, tag=None):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=tag_request_fields)

    publications_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_publication_request_fields, child_node='publications')

    related_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='related')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_tag_request(
        requested, distinct=distinct, paging=paging, data_set=dataSet, related=related, sample=sample, tag=tag)

    pagination_requested = get_requested(info, paging_fields, 'paging')

    res = paginate(query, count_query, paging, distinct,
                   build_tag_graphql_response(requested, publications_requested, related_requested, data_set=dataSet, related=related, sample=sample), pagination_requested)

    return(res)
