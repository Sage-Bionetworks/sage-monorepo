from .resolver_helpers import build_tag_graphql_response, build_tag_request, get_requested, simple_sample_request_fields, simple_publication_request_fields, simple_tag_request_fields, tag_request_fields, get_selection_set
from .resolver_helpers.paging_utils import paginate, paging_fields, create_paging


def resolve_tags(_obj, info, distinct=False, paging=None, cohort=None, dataSet=None, related=None, sample=None, tag=None, type=None):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=tag_request_fields)

    sample_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_sample_request_fields, child_node='samples')

    publications_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_publication_request_fields, child_node='publications')

    related_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='related')

    max_items = 10 if 'samples' in requested else 100_000

    paging = create_paging(paging, max_items)

    query, count_query = build_tag_request(
        requested, distinct=distinct, paging=paging, cohort=cohort, data_set=dataSet, related=related, sample=sample, tag=tag, type=type)

    pagination_requested = get_requested(info, paging_fields, 'paging')

    res = paginate(query, count_query, paging, distinct,
                   build_tag_graphql_response(requested, sample_requested, publications_requested, related_requested, cohort=cohort, sample=sample), pagination_requested)

    return(res)
