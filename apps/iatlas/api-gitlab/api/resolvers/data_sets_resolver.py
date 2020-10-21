from .resolver_helpers import build_data_set_graphql_response, data_set_request_fields, get_requested, request_data_sets


def resolve_data_sets(_obj, info, dataSet=None, sample=None, dataSetType=None):
    requested = get_requested(
        info=info, requested_field_mapping=data_set_request_fields)
    data_sets = request_data_sets(
        requested, data_set=dataSet, sample=sample, data_set_type=dataSetType)

    return map(build_data_set_graphql_response, data_sets)
