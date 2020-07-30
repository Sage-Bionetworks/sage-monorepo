from itertools import groupby
from .resolver_helpers import build_sample_graphql_response, get_value, request_samples


def resolve_samples_by_mutations_status(_obj, info, mutationId=None, mutationStatus=None, sample=None):
    sample_results = request_samples(_obj, info, mutation_id=mutationId, mutation_status=mutationStatus,
                                     sample=sample, by_status=True, by_tag=False)

    status_dict = dict()
    for sample_status, samples_list in groupby(sample_results, key=lambda s: s.status):
        status_dict[sample_status] = status_dict.get(
            sample_status, []) + list(samples_list)

    def build_response(sample_set):
        status, samples = sample_set
        return {
            'samples': list(map(build_sample_graphql_response, samples)),
            'status': status
        }

    return map(build_response, status_dict.items())
