from itertools import groupby
from .resolver_helpers import build_mutation_by_sample_graphql_response, build_mutation_request, get_requested, get_selection_set, mutation_by_sample_request_fields, mutation_request_fields, mutation_type_request_fields, simple_gene_request_fields
from .resolver_helpers.paging_utils import fetch_page, paginate, Paging, paging_fields, process_page


def resolve_mutations_by_sample(_obj, info, dataSet=None, distinct=None, entrez=None, feature=None, featureClass=None, mutationCode=None, mutationId=None, mutationType=None, paging=None, related=None, sample=None, status=None, tag=None):
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

    max_results = 12_000
    paging = paging if paging else Paging.DEFAULT
    Paging.MAX_LIMIT = Paging.MAX_LIMIT if Paging.MAX_LIMIT < max_results else max_results
    paging['first'] = paging['first'] if paging['first'] < max_results else max_results
    mutation_dict = dict()
    mutation_results = []

    if 'mutations' in sample_requested:
        kwargs = {
            'data_set': dataSet, 'distinct': distinct, 'entrez': entrez, 'feature': feature, 'feature_class': featureClass, 'mutation_code': mutationCode, 'mutation_id': mutationId, 'mutation_type': mutationType, 'paging': paging, 'related': related, 'sample': sample, 'status': status, 'tag': tag, 'by_sample': True}

        query, count_query = build_mutation_request(
            requested, gene_requested, mutation_type_requested, sample_requested, **kwargs)

        items = fetch_page(query, paging, distinct)

        for key, collection in groupby(items, key=lambda s: s.sample_id):
            mutation_dict[key] = mutation_dict.get(key, []) + list(collection)

        items = list(
            map(build_mutation_by_sample_graphql_response, mutation_dict.items()))

    # Request fields within 'paging'
    pagination_requested = get_requested(info, paging_fields, 'paging')
    return process_page(items, count_query, paging, distinct, None, pagination_requested)
