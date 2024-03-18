from .resolver_helpers import (
    build_cell_graphql_response,
    build_cell_request,
    cell_request_fields,
    get_requested,
    get_selection_set,
    cell_feature_request_fields,
    cell_gene_request_fields,
)

from .resolver_helpers.paging_utils import paginate, Paging, paging_fields


def resolve_cells(
        _obj,
        info,
        distinct=False,
        paging=None,
        entrez=None,
        feature=None,
        cohort=None,
        cell=None
    ):

    # The selection is nested under the 'items' node.
    selection_set = get_selection_set(info=info, child_node='items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=cell_request_fields)

    feature_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=cell_feature_request_fields, child_node='features')

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=cell_gene_request_fields, child_node='genes')

    if distinct == False:
        # Add the id as a cursor if not selecting distinct
        requested.add('id')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = build_cell_request(
        requested,
        feature_requested,
        gene_requested,
        distinct=distinct,
        paging=paging,
        entrez=entrez,
        feature=feature,
        cohort=cohort,
        cell=cell
    )

    pagination_requested = get_requested(info, paging_fields, 'paging')
    res = paginate(
        query,
        count_query,
        paging,
        distinct,
        build_cell_graphql_response(
            requested=requested,
            feature_requested=feature_requested,
            gene_requested=gene_requested,
            entrez=entrez,
            feature=feature
        ),
        pagination_requested
    )

    return(res)