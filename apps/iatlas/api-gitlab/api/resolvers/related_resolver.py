from itertools import groupby
from .resolver_helpers import build_related_graphql_response, get_requested, get_selection_set, related_request_fields, request_related, simple_tag_request_fields


def resolve_related(_obj, info, dataSet=None, related=None):
    selection_set = get_selection_set(
        info.field_nodes[0].selection_set, True, 'items')
    requested = get_requested(
        selection_set=selection_set, requested_field_mapping=related_request_fields)

    related_selection_set = get_selection_set(
        selection_set, 'related' in requested, 'related')
    related_requested = get_requested(
        selection_set=related_selection_set, requested_field_mapping=simple_tag_request_fields)

    related_results = request_related(
        requested=requested, related_requested=related_requested, data_set=dataSet, related=related)

    data_set_dict = dict()
    for key, tag_list in groupby(related_results, key=lambda r: r.data_set):
        data_set_dict[key] = data_set_dict.get(key, []) + list(tag_list)

    return map(build_related_graphql_response, data_set_dict.items())
