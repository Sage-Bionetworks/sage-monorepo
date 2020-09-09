from itertools import groupby
from .resolver_helpers import build_related_graphql_response, request_related


def resolve_related(_obj, info, dataSet=None, related=None):
    related_results = request_related(
        _obj, info=info, data_set=dataSet, related=related)

    data_set_dict = dict()
    for key, tag_list in groupby(related_results, key=lambda r: r.data_set):
        data_set_dict[key] = data_set_dict.get(key, []) + list(tag_list)

    return map(build_related_graphql_response, data_set_dict.items())
