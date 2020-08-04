from .resolver_helpers import build_data_set_graphql_response, request_data_sets


def resolve_data_sets(_obj, info, dataSet=None, sample=None):
    data_sets = request_data_sets(_obj, info, data_set=dataSet, sample=sample)

    return map(build_data_set_graphql_response, data_sets)
