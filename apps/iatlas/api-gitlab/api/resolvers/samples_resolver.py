from .resolver_helpers import build_sample_graphql_response, request_samples


def resolve_samples(_obj, info, name=None, patient=None):
    samples = request_samples(_obj, info, name=name, patient=patient)

    return map(build_sample_graphql_response, samples)
