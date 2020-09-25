from itertools import groupby
from .resolver_helpers import build_mutation_by_sample_graphql_response, build_mutation_request, get_requested, get_selection_set, mutation_by_sample_request_fields, mutation_request_fields, mutation_type_request_fields, simple_gene_request_fields


def resolve_mutations_by_sample(_obj, info, dataSet=None, entrez=None, feature=None, featureClass=None, mutationCode=None, mutationId=None, mutationType=None, related=None, sample=None, status=None, tag=None, page=1):
    sample_selection_set = get_selection_set(info=info, child_node='items')
    sample_requested = get_requested(
        selection_set=sample_selection_set, requested_field_mapping=mutation_by_sample_request_fields)

    selection_set = get_selection_set(sample_selection_set, 'mutations')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=mutation_request_fields)

    gene_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=simple_gene_request_fields, child_node='gene')

    mutation_type_requested = get_requested(
        selection_set=selection_set, requested_field_mapping=mutation_type_request_fields, child_node='mutationType')

    mutation_dict = dict()
    mutation_results = None

    if 'mutations' in sample_requested:
        kwargs = {
            "data_set": dataSet, "entrez": entrez, "feature": feature, "feature_class": featureClass, "mutation_code": mutationCode, "mutation_id": mutationId, "mutation_type": mutationType, "related": related, "sample": sample, "status": status, "tag": tag, "by_sample": True}

        mutation_results = build_mutation_request(
            requested, gene_requested, mutation_type_requested, sample_requested, **kwargs).distinct().paginate(page, 100000, False)

        for key, collection in groupby(mutation_results.items, key=lambda s: s.sample_id):
            mutation_dict[key] = mutation_dict.get(key, []) + list(collection)

    return {
        'items': map(build_mutation_by_sample_graphql_response, mutation_dict.items()),
        'page': mutation_results.page if mutation_results else 0,
        'pages': mutation_results.pages if mutation_results else 0,
        'total': mutation_results.total if mutation_results else 0
    }
