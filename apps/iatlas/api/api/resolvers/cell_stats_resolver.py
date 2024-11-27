from .resolver_helpers import (
    build_cell_stat_graphql_response,
    build_cell_stat_request,
    cell_stat_request_fields,
    get_requested,
    get_selection_set,
    simple_data_set_request_fields,
    simple_gene_request_fields,
)

from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_cell_stats(_obj, info, distinct=False, paging=None, entrez=None):
    # The selection is nested under the 'items' node.
    selection_set = get_selection_set(info=info, child_node="items")
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=cell_stat_request_fields
    )

    data_set_requested = get_requested(
        selection_set=selection_set,
        requested_field_mapping=simple_data_set_request_fields,
        child_node="dataSet",
    )

    gene_requested = get_requested(
        selection_set=selection_set,
        requested_field_mapping=simple_gene_request_fields,
        child_node="gene",
    )

    if distinct == False:
        # Add the id as a cursor if not selecting distinct
        requested.add("id")

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_cell_stat_request(
        requested,
        data_set_requested,
        gene_requested,
        distinct=distinct,
        paging=paging,
        entrez=entrez,
    )

    pagination_requested = get_requested(info, paging_fields, "paging")
    res = paginate(
        query,
        count_query,
        paging,
        distinct,
        build_cell_stat_graphql_response,
        pagination_requested,
    )

    return res
