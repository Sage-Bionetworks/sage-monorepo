from .resolver_helpers import build_cohort_graphql_response, build_cohort_request, cohort_request_fields, get_requested, get_selection_set, simple_data_set_request_fields, simple_tag_request_fields
from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_cohorts(_obj, info, distinct=False, paging=None, name=None, dataSet=None, tag=None, clinical=None):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=cohort_request_fields)

    data_set_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_data_set_request_fields, child_node='dataSet')

    tag_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_tag_request_fields, child_node='tag')

    if distinct == False:
        # Add the id as a cursor if not selecting distinct
        requested.add('id')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_cohort_request(
        requested, data_set_requested, tag_requested, distinct=distinct, paging=paging, name=name, data_set=dataSet, tag=tag, clinical=clinical)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    res = paginate(query, count_query, paging, distinct,
                   build_cohort_graphql_response, pagination_requested)

    return(res)
