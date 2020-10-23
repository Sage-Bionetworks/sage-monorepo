from itertools import groupby
from .resolver_helpers import build_related_graphql_response, get_requested, get_selection_set, related_request_fields, request_related, simple_tag_request_fields


def resolve_related(_obj, info, dataSet=None, related=None):
    requested = get_requested(info, related_request_fields)

    related_requested = get_requested(
        info, simple_tag_request_fields, child_node='related')

    related_results = request_related(
        requested, related_requested, data_set=dataSet, related=related)

    data_set_dict = dict()
    for key, tag_list in groupby(related_results, key=lambda r: r.data_set):
        data_set_dict[key] = data_set_dict.get(key, []) + list(tag_list)

    return map(build_related_graphql_response, data_set_dict.items())
