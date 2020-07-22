from .resolver_helpers import build_graphql_response, get_value, request_samples


def resolve_samples_by_mutations_status(_obj, info, mutationId=None, mutationStatus=None, sample=None):
    results = request_samples(_obj, info, mutation_id=mutationId, mutation_status=mutationStatus,
                              name=sample, by_status=True, by_tag=True)
    build_sample_graphql_response = build_graphql_response

    status_map = dict()
    for row in results:
        sample_status = get_value(row, 'status')
        if sample_status:
            try:
                status_map[sample_status].append(row)
            except KeyError:
                status_map[sample_status] = [row]

    return [{
        'samples': list(map(build_sample_graphql_response, value)),
        'status': key
    } for key, value in status_map.items()]
