from .resolver_helpers import build_mutation_graphql_response, get_requested, get_selection_set, mutation_related_sample_request_fields, mutation_request_fields, mutation_type_request_fields, request_mutations, return_mutation_derived_fields, simple_gene_request_fields, simple_patient_request_fields


def resolve_mutations(_obj, info, dataSet=None, entrez=None, mutationCode=None, mutationId=None, mutationType=None, related=None, sample=None, status=None):
    requested = get_requested(info, mutation_request_fields)

    gene_requested = get_requested(info, simple_gene_request_fields, 'gene')

    mutation_type_requested = get_requested(
        info, mutation_type_request_fields, 'mutationType')

    sample_selection_set = get_selection_set(info=info, child_node='samples')
    sample_requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=mutation_related_sample_request_fields)

    patient_requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=simple_patient_request_fields, child_node='patient')

    mutation_results = request_mutations(
        requested, gene_requested, mutation_type_requested, data_set=dataSet, entrez=entrez, mutation_id=mutationId, mutation_code=mutationCode, mutation_type=mutationType, related=related, sample=sample, status=status)
    mutation_ids = set(mutation.id for mutation in mutation_results)

    sample_dict = return_mutation_derived_fields(
        requested, patient_requested, sample_requested, data_set=dataSet, entrez=entrez, mutation_id=mutation_ids, mutation_code=mutationCode, mutation_type=mutationType, related=related, sample=sample, status=status)

    return map(build_mutation_graphql_response(sample_dict), mutation_results)
