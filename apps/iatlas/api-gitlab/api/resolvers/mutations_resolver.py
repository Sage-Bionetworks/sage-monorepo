from .resolver_helpers import build_mutation_graphql_response, get_requested, get_selection_set, mutation_related_sample_request_fields, mutation_request_fields, mutation_type_request_fields, request_mutations, return_mutation_derived_fields, simple_gene_request_fields, simple_patient_request_fields


def resolve_mutations(_obj, info, entrez=None, mutationCode=None, mutationId=None, mutationType=None, sample=None, status=None):
    selection_set = get_selection_set(info.field_nodes[0].selection_set, True)
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=mutation_request_fields)

    gene_selection_set = get_selection_set(selection_set, True, 'gene')
    gene_requested = get_requested(
        selection_set=gene_selection_set, requested_field_mapping=simple_gene_request_fields)

    mutation_type_selection_set = get_selection_set(
        selection_set, True, 'mutationType')
    mutation_type_requested = get_requested(
        selection_set=mutation_type_selection_set, requested_field_mapping=mutation_type_request_fields)

    sample_selection_set = get_selection_set(
        selection_set, True, 'samples')
    sample_requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=mutation_related_sample_request_fields)

    patient_selection_set = get_selection_set(
        sample_selection_set, True, 'patient')
    patient_requested = get_requested(
        selection_set=patient_selection_set, requested_field_mapping=simple_patient_request_fields)

    mutation_results = request_mutations(requested, gene_requested, mutation_type_requested,
                                         entrez=entrez, mutation_id=mutationId, mutation_code=mutationCode, mutation_type=mutationType, sample=sample, status=status)
    mutation_ids = set(mutation.id for mutation in mutation_results)

    sample_dict = return_mutation_derived_fields(requested, patient_requested, sample_requested,
                                                 mutation_ids=mutation_ids, entrez=entrez, mutation_id=mutationId, mutation_code=mutationCode, mutation_type=mutationType, sample=sample, status=status)

    return map(build_mutation_graphql_response(sample_dict), mutation_results)
