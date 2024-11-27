from .resolver_helpers import (
    build_neoantigen_graphql_response,
    build_neoantigen_request,
    neoantigen_request_fields,
    get_requested,
    get_selection_set,
    simple_gene_request_fields,
    simple_patient_request_fields,
)

from .resolver_helpers.paging_utils import paginate, create_paging, paging_fields


def resolve_neoantigens(
    _obj, info, distinct=False, paging=None, patient=None, entrez=None, pmhc=None
):
    # The selection is nested under the 'items' node.
    selection_set = get_selection_set(info=info, child_node="items")

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=neoantigen_request_fields
    )

    patient_requested = get_requested(
        selection_set=selection_set,
        requested_field_mapping=simple_patient_request_fields,
        child_node="patient",
    )

    gene_requested = get_requested(
        selection_set=selection_set,
        requested_field_mapping=simple_gene_request_fields,
        child_node="gene",
    )

    max_items = 10000

    paging = create_paging(paging, max_items)

    query, count_query = build_neoantigen_request(
        requested,
        patient_requested,
        gene_requested,
        distinct=distinct,
        paging=paging,
        patient=patient,
        entrez=entrez,
        pmhc=pmhc,
    )

    pagination_requested = get_requested(info, paging_fields, "paging")

    return paginate(
        query=query,
        count_query=count_query,
        paging=paging,
        distinct=distinct,
        response_builder=build_neoantigen_graphql_response,
        pagination_requested=pagination_requested,
    )
