from .resolver_helpers import build_mutation_graphql_response, get_requested, get_selection_set, mutation_related_sample_request_fields, mutation_request_fields, mutation_type_request_fields, build_mutation_request, simple_gene_request_fields, simple_patient_request_fields
from .resolver_helpers.paging_utils import create_paging, paginate, paging_fields, create_paging


def resolve_mutations(_obj, info, cohort=None, distinct=False, entrez=None, mutation=None, mutationCode=None, mutationType=None, paging=None, sample=None, status=None):

    selection_set = get_selection_set(info=info, child_node='items')

    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=mutation_request_fields)

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_gene_request_fields, child_node='gene')

    mutation_type_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=mutation_type_request_fields, child_node='mutationType')

    sample_selection_set = get_selection_set(
        selection_set=selection_set, child_node='samples')

    sample_requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=mutation_related_sample_request_fields)

    max_items = 10 if 'samples' in requested else 100_000

    paging = create_paging(paging, max_items)

    query, count_query = build_mutation_request(
        requested, gene_requested, mutation_type_requested, cohort=cohort, distinct=distinct, entrez=entrez, mutation_code=mutationCode, mutation_type=mutationType, mutation=mutation, paging=paging, sample=sample)

    pagination_requested = get_requested(info, paging_fields, 'paging')

    response_function = build_mutation_graphql_response(
        requested=requested, sample_requested=sample_requested, status=status, sample=sample, cohort=cohort)

    res = paginate(query, count_query, paging, distinct,
                   response_function, pagination_requested)
    return(res)
