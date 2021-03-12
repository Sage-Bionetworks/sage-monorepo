from .resolver_helpers import build_mutation_graphql_response, get_requested, get_selection_set, mutation_related_sample_request_fields, mutation_request_fields, mutation_type_request_fields, request_mutations, return_mutation_derived_fields, simple_gene_request_fields, simple_patient_request_fields
from .resolver_helpers.paging_utils import fetch_page, paginate, Paging, paging_fields, process_page


def resolve_mutations(_obj, info, dataSet=None, distinct=False, entrez=None, mutationCode=None, mutationId=None, mutationType=None, paging=None, related=None, sample=None, status=None, tag=None):

    selection_set = get_selection_set(info=info, child_node='items')
    requested = get_requested(selection_set=selection_set, requested_field_mapping=mutation_request_fields)

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_gene_request_fields, child_node='gene')

    mutation_type_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=mutation_type_request_fields, child_node='mutationType')

    sample_selection_set = get_selection_set(selection_set=selection_set, child_node='samples')
    sample_requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=mutation_related_sample_request_fields)

    patient_requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=simple_patient_request_fields, child_node='patient')

    paging = paging if paging else Paging.DEFAULT

    query, count_query = request_mutations(
        requested, gene_requested, mutation_type_requested, data_set=dataSet, distinct=distinct, entrez=entrez, mutation_id=mutationId, mutation_code=mutationCode, mutation_type=mutationType, paging=paging, related=related, sample=sample, status=status, tag=tag)

    items = list()
    sample_dict = dict()
    if len(sample_requested):
        items = fetch_page(query, paging, distinct)
        mutation_ids = set(mutation.id for mutation in items)
        sample_dict = return_mutation_derived_fields(
            requested, patient_requested, sample_requested, data_set=dataSet, entrez=entrez, mutation_id=mutation_ids, mutation_code=mutationCode, mutation_type=mutationType, related=related, sample=sample, status=status, tag=tag)
    else:
        items = fetch_page(query, paging, distinct)

    pagination_requested = get_requested(info, paging_fields, 'paging')
    return process_page(items, count_query, paging, distinct, build_mutation_graphql_response(sample_dict), pagination_requested)
